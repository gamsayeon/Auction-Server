package com.ccommit.auction_server.exception;

public class EnumConvertersException extends AuctionCommonException {
    public EnumConvertersException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
