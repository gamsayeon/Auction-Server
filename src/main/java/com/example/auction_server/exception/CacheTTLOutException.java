package com.example.auction_server.exception;

public class CacheTTLOutException extends RuntimeException {
    public CacheTTLOutException(String code) {
        super(code);
    }
}