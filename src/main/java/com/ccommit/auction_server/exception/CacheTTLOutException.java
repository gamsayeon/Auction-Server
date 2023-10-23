package com.ccommit.auction_server.exception;

public class CacheTTLOutException extends RuntimeException {
    public CacheTTLOutException(String code) {
        super(code);
    }
}
