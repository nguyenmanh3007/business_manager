package com.vnpt_cms.learn_spring.repository;

import com.vnpt_cms.learn_spring.entity.ScRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScRoleRepository extends JpaRepository<ScRole, String> {
    Optional<ScRole> findByName(String name);
}
