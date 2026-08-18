# TicketHub API

A REST API for concert ticket booking, built to explore the engineering challenges behind high-concurrency systems — specifically, how to sell a finite number of tickets without overselling them under concurrent load.

## The Core Problem

When two users attempt to book the same ticket at the same time, a naive implementation will let both succeed. The last `UPDATE` wins, one booking is silently lost, and the system has oversold. TicketHub tackles this at two layers:

- **Distributed lock (Redis/Redisson)** — before any ticket state changes, a per-ticket lock is acquired (`ticket_lock_{id}`). Only one thread can enter the critical section at a time, across any number of application instances.
- **Optimistic locking (JPA `@Version`)** — the `Ticket` entity carries a version field. If two transactions somehow race past the distributed lock, the second `UPDATE` will find a stale version and throw an `OptimisticLockException`, preventing a silent overwrite.

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 21 |
| Framework | Spring Boot 3.5 |
| Persistence | Spring Data JPA + Hibernate, PostgreSQL |
| Distributed locks | Redisson (Redis client) |
| Auth | Spring Security + JWT (jjwt 0.12.6) |
| Utilities | Lombok |

## Architecture

The application is a **vertical-slice modular monolith**. Code is organized by business domain first, then by technical layer within each domain:

```
com.tickethub
├── concert/
│   ├── domain/                 # Concert entity
│   ├── infrastructure/
│   │   ├── controller/         # REST endpoints + DTOs
│   │   └── repository/
│   └── usecase/                # CreateConcert, GetAllConcerts
├── ticket/
│   ├── domain/
│   │   ├── model/              # Ticket, Booking entities + state machine
│   │   └── exception/
│   ├── infrastructure/
│   │   ├── controller/
│   │   ├── repository/
│   │   └── persistence/        # TicketPersister (transactional writes)
│   └── usecase/                # BookTicket, UnbookTicket, ConfirmBooking, ...
├── user/
│   ├── domain/
│   ├── infrastructure/
│   └── usecase/
└── shared/
    ├── security/               # JWT filter + Spring Security config
    ├── redisson/               # LockService
    └── exception/              # GlobalExceptionHandler
```

**Use-case pattern**: every business operation is a dedicated `@Service` class with a single `execute(...)` method. Controllers hold no business logic — they delegate immediately to the appropriate use case.

## Booking Lifecycle

```
Ticket:   AVAILABLE ──book──► BOOKED ──confirm──► SOLD
                                  └──unbook──► AVAILABLE

Booking:  PENDING ──confirm──► CONFIRMED
               └──unbook──► CANCELLED
```

A booking is created in `PENDING` state and expires after **15 minutes**. During that window the ticket is `BOOKED` (reserved) and unavailable to others. The user must explicitly confirm the booking to transition to `CONFIRMED`/`SOLD`, or cancel it to release the ticket back to the pool.

## Async Batch Ticket Creation

When a concert is created, its tickets are generated asynchronously (`@Async`) using `EntityManager` directly rather than calling a JPA repository in a loop. Every 50 tickets the entity manager is flushed and cleared, keeping the persistence context from growing unbounded. This is controlled by `spring.jpa.properties.hibernate.jdbc.batch_size` (default: 50).

The concert is saved and its ID is available before ticket creation begins, so the async task can start immediately after `concertRepository.save()` returns.

## API Reference

All endpoints except registration and login require a JWT in the `Authorization: Bearer <token>` header.

### Auth

| Method | Path | Description |
|---|---|---|
| `POST` | `/auth/login` | Authenticate and receive a JWT |

### Users

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/users` | Register a new user |
| `GET` | `/api/users/{userId}` | Get user by ID |

### Concerts

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/concerts` | Create a concert (triggers async ticket batch) |
| `GET` | `/api/concerts` | List all concerts |

### Tickets

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/tickets` | List tickets by concert (`?concertId=`) |
| `GET` | `/api/tickets/{ticketId}` | Get ticket by ID |
| `POST` | `/api/tickets/{ticketId}/book` | Reserve a ticket (acquires distributed lock) |
| `POST` | `/api/tickets/{ticketId}/unbook` | Cancel a pending booking |
| `POST` | `/api/tickets/{ticketId}/confirm` | Confirm a pending booking |

## Running Locally

**Prerequisites**: Java 21, PostgreSQL, Redis.

```bash
# Start the app (PostgreSQL and Redis must be running)
./mvnw spring-boot:run
```

Default configuration (`application.yml`) connects to:
- PostgreSQL: `localhost:5432/tickethub_db`
- Redis: `localhost:6379`

The schema is managed by Hibernate (`ddl-auto: update`) — no migration tool is required to get started.

```bash
# Run tests
./mvnw test

# Build JAR
./mvnw clean package -DskipTests
```
