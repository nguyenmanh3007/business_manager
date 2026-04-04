package com.vnpt_cms.learn_spring.repository;

import com.vnpt_cms.learn_spring.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    @Transactional
    void deleteByUserId(String userId);
}
