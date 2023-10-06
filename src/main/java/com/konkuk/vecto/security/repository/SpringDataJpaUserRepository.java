package com.konkuk.vecto.security.repository;

import com.konkuk.vecto.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
}
