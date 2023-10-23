package com.ccommit.auction_server.exception;

public class LogoutFailedException extends RuntimeException {
    public LogoutFailedException(String code) {
        super(code);
    }
}
