package com.ccommit.auction_server.exception;

public class DuplicateException extends AuctionCommonException {
    public DuplicateException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
