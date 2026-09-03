package com.example.ouradventure.config;

import com.example.ouradventure.common.security.JwtAuthenticationFilter;
import com.example.ouradventure.common.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 返回 BCryptPasswordEncoder 实例

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint) throws Exception {

        http
                // 1. 关掉 CSRF：无状态接口不用 cookie 存身份，不存在 CSRF 场景
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 不创建 session，身份完全来自每个请求自带的 token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 注册和登录公开，其余一律要求已认证
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())

                // 4. 未认证时返回 401 JSON，而不是 Spring Security 的默认 403
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(restAuthenticationEntryPoint))

                // 5. 把 JWT 过滤器插到用户名密码过滤器之前
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
