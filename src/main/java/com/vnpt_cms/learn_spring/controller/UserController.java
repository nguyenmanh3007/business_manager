package com.vnpt_cms.learn_spring.controller;

import com.vnpt_cms.learn_spring.entity.ScUser;
import com.vnpt_cms.learn_spring.repository.ScUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final ScUserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<ScUser>> getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('user:read')")
    public ResponseEntity<ScUser> getById(@PathVariable String id) {
        return ResponseEntity.ok(userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScUser> create(@RequestBody @Valid ScUser user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userRepository.save(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or " + "(hasAuthority('user:write') and #id == authentication.name)")
    public ResponseEntity<ScUser> update(@PathVariable String id,
                                         @RequestBody ScUser user) {
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('user:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
