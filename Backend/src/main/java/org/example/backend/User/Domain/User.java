package org.example.backend.User.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * User Entity
 * Stores user information from social login
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"provider", "oauth_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Social login provider (kakao, naver, google, etc.)
     */
    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

    /**
     * User unique ID issued by social login provider
     * Combination of provider and oauthId must be unique
     * (Same ID from same social provider can only register once)
     */
    @Column(name = "oauth_id", nullable = false, length = 100)
    private String oauthId;

    /**
     * User email
     * Only has value if email consent was given during Kakao login
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * User name (nickname)
     * For Kakao, this is the profile nickname
     */
    @Column(name = "name", length = 50)
    private String name;

    /**
     * User role (USER, ADMIN)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    /**
     * Registration timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Last update timestamp
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
