package com.ccommit.auction_server.validation.annotation;

import com.ccommit.auction_server.validation.isExistUserIdlValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = isExistUserIdlValidator.class)
public @interface isExistUserIdlValidation {
    String message() default "이미 사용 중인 아이디입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
