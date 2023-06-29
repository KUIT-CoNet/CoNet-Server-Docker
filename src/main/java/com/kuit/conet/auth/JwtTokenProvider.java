package com.kuit.conet.auth;

import com.kuit.conet.common.exception.InvalidTokenException;
import com.kuit.conet.common.exception.TokenExpiredException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.util.Date;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.EXPIRED_TOKEN;
import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.MALFORMED_TOKEN;

@Component
public class JwtTokenProvider {
    private final String secretKey;
    private final long validityInMilliseconds;
    private final JwtParser jwtParser;

    private static final long ACCESS_TOKEN_EXPIRED_IN = 24 * 60 * 60 * 1000; // 24시간
    private static final long REFRESH_TOKEN_EXPIRED_IN = 15L * 24 * 60 * 60 * 1000; // 30일

    public JwtTokenProvider(@Value("${secret.jwt-secret-key}") String secretKey,
                            @Value("${secret.jwt-expired-in}") long validityInMilliseconds) {
        this.secretKey = secretKey;
        this.validityInMilliseconds = validityInMilliseconds;
        this.jwtParser = Jwts.parser().setSigningKey(secretKey);
    }

    public String createAccessToken(Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + ACCESS_TOKEN_EXPIRED_IN);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_TOKEN_EXPIRED_IN);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Long getUserIdFromRefreshToken(String refreshtoken) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshtoken);
        return Long.parseLong(claims.getBody().getSubject());
    }

    public void validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException(EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new InvalidTokenException(MALFORMED_TOKEN);
        }
    }

    public String getPayload(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException(EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new InvalidTokenException(MALFORMED_TOKEN);
        }
    }
}