# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=MyTestClassName
```

Requires PostgreSQL on `localhost:5432` (db: `tickethub_db`) and Redis. Override credentials via env vars:
- `POSTGRES_USER` / `POSTGRES_PASSWORD`
- `JWT_SECRET`

## Architecture

Vertical-slice modular monolith. Three feature domains — `user`, `concert`, `ticket` — each structured as:

```
<domain>/
  domain/model/       # JPA entities with domain behavior methods
  domain/exception/   # Domain-specific exceptions
  infrastructure/
    controller/       # REST controllers
    controller/dto/   # Request/response records
    repository/       # Spring Data JPA interfaces
    persistence/      # Transactional write services (ticket domain only)
  usecase/            # One class per use case, injected into controllers
```

Shared cross-cutting code lives in `shared/`:
- `security/` — `SecurityFilter` (JWT extraction), `SecurityConfig`, `JwtService`
- `redisson/` — `LockService` (distributed lock wrapper)
- `exception/` — `GlobalExceptionHandler`

## Key Patterns

**Use-case pattern**: Controllers delegate entirely to use-case classes (`BookTicketUseCase`, `ConfirmBookingUseCase`, etc.). Use cases are `@Service` beans with a single `execute(...)` method.

**Distributed locking**: All ticket state mutations go through Redis/Redisson locks. `LockService` (`shared/redisson/`) uses `tryLock(1s wait, 5s lease)` with key `"ticket_lock_" + ticketId`. `ConfirmBookingUseCase` acquires the lock inline with key `"ticket_lock" + ticketId` (note: missing underscore — inconsistency with `LockService`).

**`BookingTransactionManager`** exists as a separate `@Component` from `ConfirmBookingUseCase` so that `@Transactional` is honoured through the Spring proxy. Do not inline `confirmBookingTransaction` back into `ConfirmBookingUseCase`.

**Async batch ticket creation**: `CreateTicketsUseCase` is `@Async` and uses `EntityManager` directly with manual `flush()`/`clear()` every 50 rows (configurable via `spring.jpa.properties.hibernate.jdbc.batch_size`). This runs after `concertRepository.save()` returns — the concert ID is available, but the ticket creation is non-blocking.

**Ticket lifecycle**: `AVAILABLE → BOOKED (reserved) → SOLD (confirmed)` or back to `AVAILABLE (released)`. Booking status: `PENDING → CONFIRMED / CANCELLED`. Bookings expire 15 minutes after creation (enforced by domain logic in `Booking`). `Ticket` uses `@Version` for JPA optimistic locking as a secondary safeguard.

**Auth**: Stateless JWT. `SecurityFilter` sets the `Authentication` in the `SecurityContext`. Only `POST auth/login` and `POST /api/users` are public; all other routes require a valid JWT.
