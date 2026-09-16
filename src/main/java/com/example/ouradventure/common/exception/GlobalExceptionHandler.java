package com.example.ouradventure.common.exception;

import com.example.ouradventure.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<Void> response = ApiResponse.error(errorCode.getCode(), e.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = ErrorCode.PARAM_INVALID.getMessage();

        if (e.getBindingResult().getFieldError() != null) {
            message = e.getBindingResult().getFieldError().getDefaultMessage();
        }

        ApiResponse<Void> response = ApiResponse.error(ErrorCode.PARAM_INVALID.getCode(), message);

        return ResponseEntity.status(ErrorCode.PARAM_INVALID.getHttpStatus()).body(response);


    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().iterator().next().getMessage();

        ApiResponse<Void> response = ApiResponse.error(ErrorCode.PARAM_INVALID.getCode(), message);

        return  ResponseEntity.status(ErrorCode.PARAM_INVALID.getHttpStatus()).body(response);

    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());

        return ResponseEntity.status(ErrorCode.SYSTEM_ERROR.getHttpStatus()).body(response);
    }
}
