package com.vnpt_cms.learn_spring.service;

import com.vnpt_cms.learn_spring.entity.ScUser;
import com.vnpt_cms.learn_spring.repository.ScUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ScUserRepository userRepository;

    public List<ScUser> getAll() {
        return userRepository.findAll();
    }

    public ScUser getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại: " + id));
    }

    public ScUser create(ScUser user) {
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        return userRepository.save(user);
    }

    public ScUser update(String id, ScUser request) {
        ScUser existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại: " + id));

        existing.setEmployeeCode(request.getEmployeeCode());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());

        return userRepository.save(existing);
    }

    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User không tồn tại: " + id);
        }
        userRepository.deleteById(id);
    }
}
