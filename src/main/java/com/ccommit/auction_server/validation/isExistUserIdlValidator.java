package com.ccommit.auction_server.validation;

import com.ccommit.auction_server.exception.DuplicateException;
import com.ccommit.auction_server.repository.UserRepository;
import com.ccommit.auction_server.validation.annotation.isExistUserIdlValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class isExistUserIdlValidator implements ConstraintValidator<isExistUserIdlValidation, String> {
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(isExistUserIdlValidator.class);

    public isExistUserIdlValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        boolean isDuplicationUserId = userRepository.existsByUserId(userId);

        if (isDuplicationUserId) {
            logger.warn("중복된 ID 입니다.");
            throw new DuplicateException("USER_DUPLICATE_ID", userId);
        }
        return true;
    }
}
