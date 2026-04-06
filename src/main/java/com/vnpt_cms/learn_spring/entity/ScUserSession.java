package com.vnpt_cms.learn_spring.entity;


import jakarta.persistence.Id;
import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Builder
@RedisHash("sc_user_session")
public class ScUserSession {
    @Id
    private String id;

    @Indexed
    private String userId;

    @TimeToLive
    private Long expiredTime;
 }
