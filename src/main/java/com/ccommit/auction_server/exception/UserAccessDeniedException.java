package com.ccommit.auction_server.exception;

public class UserAccessDeniedException extends AuctionCommonException {
    public UserAccessDeniedException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
