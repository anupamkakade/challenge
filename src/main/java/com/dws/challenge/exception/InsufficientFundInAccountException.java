package com.dws.challenge.exception;

public class InsufficientFundInAccountException extends RuntimeException {

    public InsufficientFundInAccountException() {
        super();
    }

    public InsufficientFundInAccountException(String message) {
        super(message);
    }
}
