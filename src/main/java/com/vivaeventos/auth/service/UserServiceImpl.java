package com.vivaeventos.auth.service;

import com.vivaeventos.auth.domain.UserNotFoundException;
import com.vivaeventos.auth.domain.model.User;
import com.vivaeventos.auth.repository.IUserRepository;
import org.springframework.stereotype.Service;
import com.vivaeventos.auth.security.InvalidCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.vivaeventos.auth.security.JwtUtil;

@Service
public class UserServiceImpl implements UserService {
    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final void validatePassword(String raw, String encoded) {
        if (!passwordEncoder.matches(raw, encoded)) {
            throw new InvalidCredentialsException();
        }
    }

    public UserServiceImpl(IUserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public User save(User user) {

        if (userRepository.existsByDni(user.getDni())) {
        throw new RuntimeException("El DNI ya existe");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    @Override
    public User findByDni(String dni) {
        return userRepository.findByDni(dni)
                .orElseThrow(() -> new UserNotFoundException(dni));
    }

    @Override
    public void deleteByDni(String dni) {
        User user = findByDni(dni);
        userRepository.delete(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public User update(User user) {
        User existingUser = userRepository.findByDni(user.getDni())
                .orElseThrow(() -> new UserNotFoundException(user.getDni()));

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        // La contraseña no se actualizara aqui, se manejara un endpoint separado para
        // eso
        existingUser.setDateBorn(user.getDateBorn());
        existingUser.setRole(user.getRole());

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public String login(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
            
        validatePassword(password, user.getPassword());

        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }
}
