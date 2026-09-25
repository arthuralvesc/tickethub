package com.tickethub.shared.redisson;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient redissonClient;

    // Tries to Lock the ticket and immediately performs the function passed as the parameter if successful.
    // Currently, the function passed as a parameter can be to book the ticket or unbook it.
    public void getLock(Long ticketId, Runnable function) {
        RLock lock = redissonClient.getLock("ticket_lock_" + ticketId);
        boolean acquired = false;

        try {
            acquired = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (!acquired) throw new RuntimeException("Could not acquire lock");
            function.run();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted", e);

        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
