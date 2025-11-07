package com.example.MoviesApp.repository;

import com.example.MoviesApp.entity.AuthProvider;
import com.example.MoviesApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
