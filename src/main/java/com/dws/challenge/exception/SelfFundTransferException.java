package com.dws.challenge.exception;

public class SelfFundTransferException extends RuntimeException {

    public SelfFundTransferException() {
        super();
    }

    public SelfFundTransferException(String s) {
        super(s);
    }
}
