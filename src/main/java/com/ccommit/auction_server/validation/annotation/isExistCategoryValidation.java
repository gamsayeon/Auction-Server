package com.ccommit.auction_server.validation.annotation;

import com.ccommit.auction_server.validation.isExistCategoryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = isExistCategoryValidator.class)
public @interface isExistCategoryValidation {
    String message() default "중복된 카테고리입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
