package com.example.auction_server.exception;

public class DeleteFailedException extends AuctionCommonException {
    public DeleteFailedException(String code, Object responseBody) {
        super(code, responseBody);
    }
}