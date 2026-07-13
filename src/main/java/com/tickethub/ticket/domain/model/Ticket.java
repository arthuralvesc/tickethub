package com.tickethub.ticket.domain.model;

import com.tickethub.concert.domain.Concert;
import com.tickethub.ticket.domain.exception.TicketNotAvailableException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_tickets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String identifier;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketType type;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Version
    private Long version;

    public Ticket(String identifier, TicketType type, Concert concert) {
        this.identifier = identifier;
        this.type = type;
        this.concert = concert;
        this.status = TicketStatus.AVAILABLE;
    }

    public void reserve() {
        if (this.status != TicketStatus.AVAILABLE) {
            throw new TicketNotAvailableException(this.id);
        }
        this.status = TicketStatus.BOOKED;
    }

    public void release() {
        this.status = TicketStatus.AVAILABLE;
    }

    public void confirmAsSold(){
        this.setStatus(TicketStatus.SOLD);
    }
}
