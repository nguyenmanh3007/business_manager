package com.vnpt_cms.learn_spring.repository;

import com.vnpt_cms.learn_spring.entity.ScUserSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScUserSessionRepository extends CrudRepository<ScUserSession,String> {
}
