package com.cocos.cocos.auth;

import com.cocos.cocos.api.member.dto.response.TokenResponse;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.enums.auth.JwtValidationType;
import com.cocos.cocos.enums.message.FailMessage;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    private static final String MEMBER_ID = "memberId";

    //1분
    //1주일 1000ms = 1초
    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7;
    // 3분
    // 1주일
    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7;

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    public TokenResponse reissueToken(Long memberId) {
        return TokenResponse.of(
                generateAccessToken(memberId),
                generateRefreshToken(memberId)
        );
    }

    public String generateAccessToken(Long memberId) {
        final Date now = new Date();
        final Claims claims = Jwts.claims()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME));      // 만료 시간

        claims.put(MEMBER_ID, memberId);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // Header
                .setClaims(claims) // Claim
                .signWith(getSigningKey()) // Signature
                .compact();
    }

    public String generateRefreshToken(Long memberId) {
        final Date now = new Date();
        final Claims claims = Jwts.claims()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME));      // 만료 시간

        claims.put(MEMBER_ID, memberId);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // Header
                .setClaims(claims) // Claim
                .signWith(getSigningKey()) // Signature
                .compact();
    }

    public JwtValidationType validateToken(String token) {
        try {
            final Claims claims = getBody(token);
            return JwtValidationType.VALID_JWT;
        } catch (MalformedJwtException ex) {
            return JwtValidationType.INVALID_JWT_TOKEN;
        } catch (ExpiredJwtException ex) {
            throw new CocosException(FailMessage.UNAUTHORIZED_EXPIRATION_JWT_EXCEPTION);
        } catch (UnsupportedJwtException ex) {
            return JwtValidationType.UNSUPPORTED_JWT_TOKEN;
        } catch (IllegalArgumentException ex) {
            return JwtValidationType.EMPTY_JWT;
        }
    }

    private SecretKey getSigningKey() {
        String encodedKey = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes()); //SecretKey 통해 서명 생성
        return Keys.hmacShaKeyFor(encodedKey.getBytes());   //일반적으로 HMAC (Hash-based Message Authentication Code) 알고리즘 사용
    }

    private Claims getBody(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserFromJwt(String token) {
        Claims claims = getBody(token);
        return Long.valueOf(claims.get(MEMBER_ID).toString());
    }

}
