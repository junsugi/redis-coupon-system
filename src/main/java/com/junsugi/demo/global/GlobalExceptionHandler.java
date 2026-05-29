package com.junsugi.demo.global;

import com.junsugi.demo.coupon.presentation.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e
    ) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("ILLEGAL_ARGUMENT", e.getMessage()));
    }
}
