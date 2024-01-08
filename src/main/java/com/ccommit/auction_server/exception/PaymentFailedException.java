package com.ccommit.auction_server.exception;

public class PaymentFailedException extends AuctionCommonException {
    public PaymentFailedException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
