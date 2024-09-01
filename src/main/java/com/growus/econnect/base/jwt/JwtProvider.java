package com.growus.econnect.base.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    public Long getSubject(String token) {
        return Long.valueOf(getJwtParser().parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    public String generateJwtToken(Long userId, long expireTime) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expireTime * 1000);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(secretKey.getBytes()).build().parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰의 유효성 검증을 수행
    public boolean validateAccessToken(String accessToken) {
        try {
            getJwtParser().parseClaimsJws(accessToken);
            //Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build().parseClaimsJws(accessToken);
            return true;
        } catch (ExpiredJwtException e) {
            //throw new UnauthorizedException(EXPIRED_ACCESS_TOKEN);
            return false;
        } catch (Exception e) {
            //throw new UnauthorizedException(INVALID_ACCESS_TOKEN_VALUE);
            return false;
        }
    }

    private JwtParser getJwtParser() {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build();
    }

    private Key getSigningKey() {
        String encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        return Keys.hmacShaKeyFor(encoded.getBytes());
    }
}
