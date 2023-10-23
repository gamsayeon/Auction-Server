package com.ccommit.auction_server.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CommonResponse<T> {
    private String code;
    private String message;
    private String url;
    private T responseBody;

    public CommonResponse(String code, String message, String url) {
        this.code = code;
        this.message = message;
        this.url = url;
    }

    public CommonResponse(String code, String message, String url, T responseBody) {
        this.code = code;
        this.message = message;
        this.url = url;
        this.responseBody = responseBody;
    }
}