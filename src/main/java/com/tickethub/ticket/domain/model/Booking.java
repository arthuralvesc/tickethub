package com.tickethub.ticket.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_bookings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "ticket_id", nullable = false, unique = true)
    private Ticket ticket;

    @JoinColumn(name = "buyer_id")
    @Setter
    private Long buyerId;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Booking(Long buyerId, Ticket ticket) {
        ticket.reserve();

        this.buyerId = buyerId;
        this.ticket = ticket;
        this.status = BookingStatus.PENDING;
        this.expiresAt = LocalDateTime.now().plus(Duration.ofMinutes(15));
        this.createdAt = LocalDateTime.now();
    }

    public void cancel(){
        this.ticket.release();
        this.status = BookingStatus.CANCELLED;
    }

    public void confirm(){
        this.ticket.confirmAsSold();
        this.status = BookingStatus.CONFIRMED;
    }
}