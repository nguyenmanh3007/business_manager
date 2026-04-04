package com.vnpt_cms.learn_spring.service;

import com.vnpt_cms.learn_spring.dto.auth.request.CreateUserRequest;
import com.vnpt_cms.learn_spring.entity.ScRole;
import com.vnpt_cms.learn_spring.entity.ScUser;
import com.vnpt_cms.learn_spring.entity.ScUserRole;
import com.vnpt_cms.learn_spring.repository.ScRoleRepository;
import com.vnpt_cms.learn_spring.repository.ScUserRepository;
import com.vnpt_cms.learn_spring.repository.ScUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ScUserRepository userRepository;
    private final ScRoleRepository roleRepository;
    private final ScUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> createUser(CreateUserRequest request) {
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        List<ScRole> roles = roleRepository.findByNameIn(request.getRoleNames());
        List<String> foundRoleNames = roles.stream().map(ScRole::getName).toList();
        List<String> notFoundRoles = request.getRoleNames().stream().filter(name -> !foundRoleNames.contains(name)).toList();
        if (!notFoundRoles.isEmpty()) {
            throw new IllegalArgumentException(
                    "Role không tồn tại: " + String.join(", ", notFoundRoles));
        }

        ScUser user = ScUser.builder()
                .employeeCode(request.getEmployeeCode())
                .userName(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
        userRepository.save(user);

        List<ScUserRole> userRoles = roles.stream()
                .map(role -> ScUserRole.builder()
                        .user(user)
                        .role(role)
                        .build())
                .collect(Collectors.toList());
        userRoleRepository.saveAll(userRoles);

        return Map.of(
                "message", "Tạo user thành công",
                "username", user.getUserName(),
                "roles", String.join(", ", request.getRoleNames())
        );
    }
}
