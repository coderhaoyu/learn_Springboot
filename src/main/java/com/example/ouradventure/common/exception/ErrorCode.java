package com.example.ouradventure.common.exception;

/**
 * 全局错误码表。
 * <p>
 * 每个常量带两个数：httpStatus 是「这一趟请求的传输结论」，给网关、监控、axios 读；
 * code 是「哪条业务规则拦住了你」，给前端做文案分支。两者给不同的人看，不能互相替代。
 * <p>
 * Service 层抛异常时只按业务语义选常量，不需要知道 HTTP 状态；
 * 由 {@link GlobalExceptionHandler} 一次性把两个数分别放回各自的位置。
 */
public enum ErrorCode {

    PARAM_INVALID(400, 1, "参数校验失败"),

    INVITATION_CODE_INVALID(400,2,"邀请码无效"),

    UNAUTHORIZED(401, 1, "未登录或登录已过期"),

    LOGIN_FAILED(401, 2, "邮箱或密码错误"),

    USER_NOT_FOUND(404, 1, "用户不存在"),

    EMAIL_ALREADY_USED(409, 1, "邮箱已被注册"),

    ALREADY_BOUND(409, 2, "您已绑定伴侣，不能再发起邀请"),

    SYSTEM_ERROR(500, 1, "系统繁忙，请稍后重试"),

    INVITATION_CODE_FAILED(500, 2, "邀请码生成失败，请重试");

    private final int httpStatus;

    private final int code;

    private final String message;

    /**
     * code 由 httpStatus 派生（前三位是状态码，后两位是该状态下的序号），
     * 这样「业务码」和「HTTP 状态」在结构上就不可能写歪：填 seq 而不是填整个 code，
     * 也就不会出现 40902 配 404 这种要人工对表的错。
     */
    ErrorCode(int httpStatus, int seq, String message) {
        this.httpStatus = httpStatus;
        this.code = httpStatus * 100 + seq;
        this.message = message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
