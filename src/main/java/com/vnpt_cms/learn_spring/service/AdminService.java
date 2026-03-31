package com.vnpt_cms.learn_spring.service;

import com.vnpt_cms.learn_spring.dto.auth.CreateUserRequest;
import com.vnpt_cms.learn_spring.entity.ScRole;
import com.vnpt_cms.learn_spring.entity.ScUser;
import com.vnpt_cms.learn_spring.entity.ScUserRole;
import com.vnpt_cms.learn_spring.repository.ScRoleRepository;
import com.vnpt_cms.learn_spring.repository.ScUserRepository;
import com.vnpt_cms.learn_spring.repository.ScUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ScUserRepository userRepository;
    private final ScRoleRepository roleRepository;
    private final ScUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public Map<String, String> createUser(CreateUserRequest request) {
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        ScRole role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException(
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

        return Map.of(
                "message", "Tạo user thành công",
                "username", user.getUserName(),
                "role", role.getName()
        );
    }
}
