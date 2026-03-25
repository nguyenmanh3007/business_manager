package com.vnpt_cms.learn_spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
        name = "SC_ROLE_PERMISSION",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ROLE_ID", "PERMISSION_ID"})
        }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScRolePermission {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private ScRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERMISSION_ID", nullable = false)
    private ScPermission permission;
}
