package com.tickethub.ticket.infrastructure.repository;

import com.tickethub.ticket.domain.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByConcertId(Long concertId);
}
