package com.vnpt_cms.learn_spring.repository;

import com.vnpt_cms.learn_spring.entity.ScUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScUserRepository extends JpaRepository<ScUser, String> {
    Optional<ScUser> findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
}
