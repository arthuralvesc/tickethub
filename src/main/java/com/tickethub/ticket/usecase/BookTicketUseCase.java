package com.tickethub.ticket.usecase;

import com.tickethub.shared.redisson.LockService;
import com.tickethub.ticket.infrastructure.persistence.TicketPersister;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BookTicketUseCase {
    private final LockService lockService;
    private final TicketPersister ticketPersister;

    public void execute(Long ticketId, Long userId) {
        lockService.getLock(ticketId, () -> ticketPersister.book(ticketId, userId));
    }
}
