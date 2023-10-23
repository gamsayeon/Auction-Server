package com.ccommit.auction_server.exception;

public class UpdateFailedException extends AuctionCommonException {
    public UpdateFailedException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
