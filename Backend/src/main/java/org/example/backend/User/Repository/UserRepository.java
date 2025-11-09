package org.example.backend.User.Repository;

import org.example.backend.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User Repository
 * Repository for finding users by provider and oauthId
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndOauthId(String provider, String oauthId);
}
