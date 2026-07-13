package com.tickethub.ticket.domain.exception;

public class LockNotAcquiredException extends RuntimeException {
    public LockNotAcquiredException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace(){
        return this;
    }
}
