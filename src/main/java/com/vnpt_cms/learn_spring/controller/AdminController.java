package com.vnpt_cms.learn_spring.controller;

import com.vnpt_cms.learn_spring.dto.auth.CreateUserRequest;
import com.vnpt_cms.learn_spring.entity.ScRole;
import com.vnpt_cms.learn_spring.entity.ScUser;
import com.vnpt_cms.learn_spring.entity.ScUserRole;
import com.vnpt_cms.learn_spring.repository.ScRoleRepository;
import com.vnpt_cms.learn_spring.repository.ScUserRepository;
import com.vnpt_cms.learn_spring.repository.ScUserRoleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ScUserRepository userRepository;
    private final ScRoleRepository roleRepository;
    private final ScUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/create-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest request) {
        if (userRepository.existsByUserName(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username đã tồn tại"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email đã tồn tại"));
        }

        // Tìm role theo tên được truyền vào (ADMIN hoặc EMPLOYEE)
        ScRole role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new RuntimeException(
                        "Role không tồn tại: " + request.getRoleName()));

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
                .role(role)
                .build();
        userRoleRepository.save(userRole);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Tạo user thành công",
                        "username", user.getUserName(),
                        "role", role.getName()
                ));
    }
}
