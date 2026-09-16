package com.example.ouradventure.common.exception;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 把错误信息传给父类 RuntimeException
        this.errorCode = errorCode;
    }

    /**
     * 同一条业务规则的不同实例共用一个错误码，只换文案（例如邀请码不存在 / 已过期 / 已被使用）。
     * 这样错误码数量跟着「前端有几种应对动作」增长，而不是跟着业务文案增长。
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message); // 只覆盖父类的 message，code 和 httpStatus 仍然取自 errorCode
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
