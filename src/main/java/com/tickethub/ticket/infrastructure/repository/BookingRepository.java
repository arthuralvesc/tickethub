package com.tickethub.ticket.infrastructure.repository;

import com.tickethub.ticket.domain.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.ticket t " +
            "JOIN FETCH t.concert c " +
            "WHERE b.id = :id")
    Optional<Booking> findBookingWithTicketAndConcert(@Param("id") Long id);

    List<Booking> findByBuyerId(Long buyerId);
}
