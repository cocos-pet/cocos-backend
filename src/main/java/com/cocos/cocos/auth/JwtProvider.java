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
import java.util.Date;

@Component
public class JwtProvider {

    private static final String MEMBER_ID = "memberId";

    // 2시간
    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 2;
    // 1주일
    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7;

    private final SecretKey signingKey;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.signingKey =  Keys.hmacShaKeyFor(secret.getBytes());
    }

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
                .signWith(signingKey) // Signature
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
                .signWith(signingKey) // Signature
                .compact();
    }

    public JwtValidationType validateToken(String token) {
        try {
            final Claims claims = getBody(token);
            return JwtValidationType.VALID_JWT;
        } catch (MalformedJwtException ex) {
            throw new CocosException(FailMessage.UNAUTHORIZED_MALFORMED_JWT);
        } catch (ExpiredJwtException ex) {
            throw new CocosException(FailMessage.UNAUTHORIZED_EXPIRATION_JWT_EXCEPTION);
        } catch (UnsupportedJwtException ex) {
            throw new CocosException(FailMessage.UNAUTHORIZED_UNSUPPORTED_JWT);
        } catch (IllegalArgumentException ex) {
            return JwtValidationType.EMPTY_JWT;
        }
    }

    private Claims getBody(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserFromJwt(String token) {
        Claims claims = getBody(token);
        return Long.valueOf(claims.get(MEMBER_ID).toString());
    }

}
