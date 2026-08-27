package com.example.userdemo.common.exception;

import com.example.userdemo.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        int code = e.getCode();
        String message = e.getMessage();

        ApiResponse<Void> response = ApiResponse.error(code, message);

        return ResponseEntity.status(code).body(response);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = "参数校验失败";

        if (e.getBindingResult().getFieldError() != null) {
            message = e.getBindingResult().getFieldError().getDefaultMessage();
        }

        ApiResponse<Void> response = ApiResponse.error(400, message);

        return ResponseEntity.status(400).body(response);


    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().iterator().next().getMessage();

        ApiResponse<Void> response = ApiResponse.error(400, message);

        return  ResponseEntity.status(400).body(response);

    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        ApiResponse<Void> response = ApiResponse.error(500, "系统繁忙，请稍后重试");

        return ResponseEntity.status(500).body(response);
    }
}
