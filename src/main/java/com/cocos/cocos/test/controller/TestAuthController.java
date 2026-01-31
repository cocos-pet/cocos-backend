package com.cocos.cocos.test.controller;

import com.cocos.cocos.api.member.dto.response.TokenResponse;
import com.cocos.cocos.auth.JwtProvider;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.test.dto.FakeLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/test/auth")
@RequiredArgsConstructor
@Profile({"local", "dev", "test"})
public class TestAuthController implements TestAuthControllerSwagger {

    private final JwtProvider jwtProvider;

    @Value("${test.auth-secret}")
    private String testAuthSecret;

    @PostMapping("/login")
    public ResponseEntity<List<TokenResponse>> fakeLogin(
            @RequestHeader("X-Test-Auth-Secret") final String secretKey,
            @RequestBody final FakeLoginRequest request) {
        
        if (!testAuthSecret.equals(secretKey)) {
            throw new CocosException(FailMessage.UNAUTHORIZED);
        }

        final List<TokenResponse> tokens = request.memberIds().stream()
                .map(jwtProvider::reissueToken
                )
                .toList();

        return ResponseEntity.ok(tokens);
    }
}
