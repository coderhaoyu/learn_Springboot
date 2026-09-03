package com.example.ouradventure.common.security;

import com.example.ouradventure.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 未认证请求的统一出口。
 * <p>
 * 过滤器跑在 DispatcherServlet 之前，异常到不了 GlobalExceptionHandler，
 * 所以 401 的响应体必须在这里自己写。
 * <p>
 * 用 ObjectMapper 序列化 {@link ApiResponse}，而不是手写 JSON 字符串，
 * 这样响应格式跟 GlobalExceptionHandler 出去的错误响应始终一致。
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String MESSAGE = "未登录或登录已过期";

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 必须在 getWriter() 之前设置，否则编码不生效，中文会乱码
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiResponse<Void> body = ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), MESSAGE);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
