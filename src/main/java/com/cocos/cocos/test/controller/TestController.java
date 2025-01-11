package com.cocos.cocos.test.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController implements TestControllerSwagger {

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/token-check")
    public ResponseEntity<String> tokenCheck(final HttpServletRequest request) {
        return ResponseEntity.ok("token : " + request.getHeader("Authorization"));
    }
}
