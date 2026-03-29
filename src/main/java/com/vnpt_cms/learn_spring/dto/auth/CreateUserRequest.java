package com.vnpt_cms.learn_spring.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank
    private String employeeCode;

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Email
    private String email;

    private String phone;

    @NotBlank(message = "roleName không được trống (ADMIN hoặc EMPLOYEE)")
    private String roleName;
}
