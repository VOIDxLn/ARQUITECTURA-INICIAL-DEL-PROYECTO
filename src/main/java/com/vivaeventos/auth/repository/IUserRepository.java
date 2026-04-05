package com.vivaeventos.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vivaeventos.auth.domain.model.User;

import java.util.Optional;


public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByDni(String dni);
    boolean existsByDni(String dni);
    Optional<User> findByEmail(String email);
}