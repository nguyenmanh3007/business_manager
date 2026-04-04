package com.vnpt_cms.learn_spring.controller;

import com.vnpt_cms.learn_spring.dto.auth.request.RefreshTokenRequest;
import com.vnpt_cms.learn_spring.dto.auth.response.JwtResponse;
import com.vnpt_cms.learn_spring.dto.auth.request.LoginRequest;
import com.vnpt_cms.learn_spring.dto.auth.request.RegisterRequest;
import com.vnpt_cms.learn_spring.dto.auth.response.RefreshTokenResponse;
import com.vnpt_cms.learn_spring.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }
}
