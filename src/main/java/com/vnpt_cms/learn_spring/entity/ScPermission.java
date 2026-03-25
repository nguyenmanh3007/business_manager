package com.vnpt_cms.learn_spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name="SC_PERMISSION")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScPermission {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "NAME", unique = true, nullable = false)
    private String name;
}
