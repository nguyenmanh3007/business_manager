package com.vnpt_cms.learn_spring.service;

import com.vnpt_cms.learn_spring.dto.auth.response.JwtResponse;
import com.vnpt_cms.learn_spring.dto.auth.request.LoginRequest;
import com.vnpt_cms.learn_spring.dto.auth.request.RegisterRequest;
import com.vnpt_cms.learn_spring.dto.auth.response.RefreshTokenResponse;
import com.vnpt_cms.learn_spring.entity.*;
import com.vnpt_cms.learn_spring.jwt.JwtUtils;
import com.vnpt_cms.learn_spring.repository.ScRoleRepository;
import com.vnpt_cms.learn_spring.repository.ScUserRepository;
import com.vnpt_cms.learn_spring.repository.ScUserRoleRepository;
import com.vnpt_cms.learn_spring.repository.ScUserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ScUserRepository userRepository;
    private final ScRoleRepository roleRepository;
    private final ScUserRoleRepository userRoleRepository;
    private final ScUserSessionRepository scUserSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ScUser user = userRepository.findByUserName(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String jwt = jwtUtils.generateToken(request.getUsername());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        ScUserSession scUserSession = ScUserSession.builder()
                .id(jwt)
                .userId(user.getId())
                .expiredTime(864000L)
                .build();
        scUserSessionRepository.save(scUserSession);


        return JwtResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken.getToken())
                .username(userDetails.getUsername())
                .roles(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }

    public Map<String, String> register(RegisterRequest request) {
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        ScRole employeeRole = roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("Role EMPLOYEE không tồn tại trong DB"));

        ScUser user = ScUser.builder()
                .employeeCode(request.getEmployeeCode())
                .userName(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        userRepository.save(user);

        ScUserRole userRole = ScUserRole.builder()
                .user(user)
                .role(employeeRole)
                .build();
        userRoleRepository.save(userRole);

        return Map.of("message", "Đăng ký thành công");
    }

    public RefreshTokenResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenService.verifyToken(refreshToken);
        ScUser user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String newAccessToken = jwtUtils.generateToken(user.getUserName());
        refreshTokenService.revokeToken(refreshToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return new RefreshTokenResponse(newAccessToken, newRefreshToken.getToken());
    }

}
