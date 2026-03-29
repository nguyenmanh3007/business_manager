package com.vnpt_cms.learn_spring.controller;

import com.vnpt_cms.learn_spring.dto.auth.JwtResponse;
import com.vnpt_cms.learn_spring.dto.auth.LoginRequest;
import com.vnpt_cms.learn_spring.dto.auth.RegisterRequest;
import com.vnpt_cms.learn_spring.entity.ScRole;
import com.vnpt_cms.learn_spring.entity.ScUser;
import com.vnpt_cms.learn_spring.entity.ScUserRole;
import com.vnpt_cms.learn_spring.jwt.JwtUtils;
import com.vnpt_cms.learn_spring.repository.ScRoleRepository;
import com.vnpt_cms.learn_spring.repository.ScUserRepository;
import com.vnpt_cms.learn_spring.repository.ScUserRoleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ScUserRepository userRepository;
    private final ScRoleRepository roleRepository;
    private final ScUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        userRepository.findByUserName(request.getUsername()).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });

        String jwt = jwtUtils.generateToken(request.getUsername());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(JwtResponse.builder()
                .token(jwt)
                .username(userDetails.getUsername())
                .roles(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        if (userRepository.existsByUserName(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username đã tồn tại"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email đã tồn tại"));
        }

        // Mặc định role EMPLOYEE
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

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Đăng ký thành công"));
    }
}