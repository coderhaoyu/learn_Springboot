package com.example.ouradventure.common.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 签发。
 * <p>
 * 只负责 token 本身的生成，不查数据库，不做业务判断。
 */
@Component
public class JwtService {

    /** 签名密钥，构造时派生一次，之后不再变化 */
    private final SecretKey secretKey;

    /** token 有效期，单位分钟 */
    private final long expireMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expire-minutes}") long expireMinutes) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMinutes = expireMinutes;
    }

    /**
     * 为指定用户签发 token。
     *
     * @param userId 用户主键，写入 JWT 的 sub 字段
     * @return 形如 xxx.yyy.zzz 的 token 字符串
     */
    public String generateToken(Long userId) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(getExpireSeconds());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    /** token 有效期，单位秒，用于返回给前端 */
    public long getExpireSeconds() {
        return expireMinutes * 60;
    }
}
