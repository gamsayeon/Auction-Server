package com.ccommit.auction_server.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoginCheck {
    public static enum LoginType {
        USER, ADMIN, UNAUTHORIZED_USER
    }

    LoginType[] types() default {};
}