package com.ccommit.auction_server.exception;

public class NullDataException extends RuntimeException {
    public NullDataException(String code) {
        super(code);
    }
}