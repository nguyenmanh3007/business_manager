package com.vnpt_cms.learn_spring.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class JwtResponse {
    private String token;
    private String username;
    private Set<String> roles;
}
