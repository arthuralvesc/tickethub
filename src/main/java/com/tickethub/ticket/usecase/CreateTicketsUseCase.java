package com.tickethub.ticket.usecase;

import com.tickethub.concert.domain.Concert;
import com.tickethub.ticket.domain.model.Ticket;
import com.tickethub.ticket.domain.model.TicketType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTicketsUseCase {
    @PersistenceContext private final EntityManager entityManager;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:50}")
    private int batchSize;

    @Transactional
    @Async
    // Executes a batch creation of the number of tickets specified in the concert creation request
    public void execute(Long concertId, Integer numberOfTickets) {
        Concert concertRef = entityManager.getReference(Concert.class, concertId);

        for (int i = 0; i < numberOfTickets; i++) {
            Ticket ticket = new Ticket("Floor seat" + (i+1), TicketType.FLOOR, concertRef);
            entityManager.persist(ticket);

            if ((i + 1) % batchSize == 0) {
                System.out.println("Flushing and clearing the batch...");

                entityManager.flush();

                entityManager.clear();
            }
        }
    }
}
