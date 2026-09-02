package com.example.ouradventure.common.exception;

public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message); // 把错误信息传给父类 RuntimeException
        this.code = code;
    }


    public int getCode() {
        return code;
    }
}
