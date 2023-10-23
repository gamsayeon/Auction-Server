package com.ccommit.auction_server.validation.annotation;

import com.ccommit.auction_server.validation.IsAdminValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsAdminValidator.class)
public @interface IsAdminValidation {
    String message() default "ADMIN으로 회원가입 할 수 없습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
