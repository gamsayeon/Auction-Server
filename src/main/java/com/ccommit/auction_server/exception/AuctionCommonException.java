package com.ccommit.auction_server.exception;

public class AuctionCommonException extends RuntimeException {
    private Object responseBody;

    public AuctionCommonException(String code, Object responseBody) {
        super(code);
        this.responseBody = responseBody;
    }

    public Object getResponseBody() {
        return responseBody;
    }

}
