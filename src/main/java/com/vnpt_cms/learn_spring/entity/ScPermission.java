package com.vnpt_cms.learn_spring.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name="SC_PERMISSION")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScPermission {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "NAME", unique = true, nullable = false)
    private String name;
}
