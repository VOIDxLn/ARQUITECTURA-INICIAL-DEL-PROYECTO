package com.vivaeventos.auth.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vivaeventos.auth.domain.model.User;

public interface UserService {
    User save(User user);
    void deleteByDni(String dni);
    User findByDni(String dni);
    User findByEmail(String email);
    User update(User user);
    Page<User> findAll(Pageable pageable);
    String login(String email, String password);
}
