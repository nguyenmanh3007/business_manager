package com.vnpt_cms.learn_spring.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="SC_USER")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScUser {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name="EMPLOYEE_CODE",unique = true, nullable = false)
    private String employeeCode;

    @Column(name="USERNAME",unique = true, nullable = false)
    private String userName;

    @Column(name="PASSWORD", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name="EMAIL", unique = true, nullable = false)
    @Email(message = "email invalid")
    private String email;

    @Column(name="PHONE")
    private String phone;

    @CreationTimestamp
    @Column(name="CREATED_DATE", updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdDate;

    @CreationTimestamp
    @Column(name="UPDATED_DATE", updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedDate;

    @CreationTimestamp
    @Column(name="LAST_LOGIN", updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ScUserRole> userRoles = new HashSet<>();
}
