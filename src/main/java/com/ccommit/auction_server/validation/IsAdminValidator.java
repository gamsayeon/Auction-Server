package com.ccommit.auction_server.validation;

import com.ccommit.auction_server.enums.UserType;
import com.ccommit.auction_server.exception.UserAccessDeniedException;
import com.ccommit.auction_server.validation.annotation.IsAdminValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IsAdminValidator implements ConstraintValidator<IsAdminValidation, UserType> {
    private static final Logger logger = LogManager.getLogger(IsAdminValidator.class);

    @Override
    public boolean isValid(UserType userType, ConstraintValidatorContext context) {
        if (userType == UserType.ADMIN) {
            logger.warn("ADMIN 으로 회원가입할 수 없습니다.");
            throw new UserAccessDeniedException("USER_USER_ACCESS_DENIED", userType);
        }

        return true;
    }
}
