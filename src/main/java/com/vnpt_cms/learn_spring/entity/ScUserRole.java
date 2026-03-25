package com.vnpt_cms.learn_spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
        name = "SC_USER_ROLE",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"USER_ID", "ROLE_ID"})
        }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScUserRole {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private ScUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private ScRole role;
}
