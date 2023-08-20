package com.example.auction_server.validation;

import com.example.auction_server.exception.DuplicateException;
import com.example.auction_server.repository.UserRepository;
import com.example.auction_server.validation.annotation.UniqueUserId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UniqueUserIdValidator implements ConstraintValidator<UniqueUserId, String> {
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(UniqueUserIdValidator.class);


    public UniqueUserIdValidator(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(UniqueUserId constraintAnnotation) {
    }

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        if (userId == null) {
            return false;
        }

        boolean isDuplicationUserId = userRepository.existsByUserId(userId);

        if (isDuplicationUserId) {
            logger.warn("중복된 ID 입니다.");
            throw new DuplicateException("ERR_2001", userId);
        }
        return true;
    }
}