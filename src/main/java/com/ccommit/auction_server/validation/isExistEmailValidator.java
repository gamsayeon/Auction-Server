package com.ccommit.auction_server.validation;

import com.ccommit.auction_server.exception.DuplicateException;
import com.ccommit.auction_server.repository.UserRepository;
import com.ccommit.auction_server.validation.annotation.isExistEmailValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class isExistEmailValidator implements ConstraintValidator<isExistEmailValidation, String> {
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(isExistEmailValidator.class);


    public isExistEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        boolean isDuplicationEmail = userRepository.existsByEmail(email);

        if (isDuplicationEmail) {
            logger.warn("중복된 Email 주소입니다.");
            throw new DuplicateException("USER_DUPLICATE_EMAIL", email);
        }

        return true;
    }
}
