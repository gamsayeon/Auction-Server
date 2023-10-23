package com.ccommit.auction_server.exception;

public class EmailSendFailedException extends RuntimeException {
    public EmailSendFailedException(String code) {
        super(code);
    }
}
