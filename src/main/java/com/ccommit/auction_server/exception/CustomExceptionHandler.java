package com.ccommit.auction_server.exception;

import com.ccommit.auction_server.model.CommonResponse;
import com.ccommit.auction_server.validation.isExistUserIdlValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    private static final Logger logger = LogManager.getLogger(isExistUserIdlValidator.class);

    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseBody
    public ResponseEntity<Object> handleException(RuntimeException ex, HttpServletRequest request) {
        String exceptionCode = ex.getMessage();
        CommonResponse commonResponse = new CommonResponse(exceptionCode,
                ExceptionMessage.getExceptionMessage(exceptionCode), request.getServletPath());
        logger.error(commonResponse.toString());
        return ResponseEntity.badRequest().body(commonResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.error(ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        CommonResponse commonResponse = new CommonResponse("COMMON_VALID",
                errorMessage, request.getServletPath());
        return ResponseEntity.badRequest().body(commonResponse);
    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseBody
    public ResponseEntity<Object> handleValidationException(ValidationException ex, HttpServletRequest request) {
        String exceptionCode = ex.getCause().getMessage();
        AuctionCommonException commonException = (AuctionCommonException) ex.getCause();
        CommonResponse commonResponse = new CommonResponse(exceptionCode,
                ExceptionMessage.getExceptionMessage(exceptionCode), request.getServletPath(), commonException.getResponseBody());
        logger.error(commonResponse.toString());
        return ResponseEntity.badRequest().body(commonResponse);
    }

    @ExceptionHandler(value = {DuplicateException.class, AddFailedException.class, NotMatchingException.class,
            InputMismatchException.class, UpdateFailedException.class, UserAccessDeniedException.class,
            DeleteFailedException.class})
    @ResponseBody
    public ResponseEntity<Object> handleAuctionCommonException(AuctionCommonException ex, HttpServletRequest request) {
        String exceptionCode = ex.getMessage();
        CommonResponse commonResponse = new CommonResponse(exceptionCode,
                ExceptionMessage.getExceptionMessage(exceptionCode), request.getServletPath(), ex.getResponseBody());
        logger.error(commonResponse.toString());
        return ResponseEntity.badRequest().body(commonResponse);
    }
}
