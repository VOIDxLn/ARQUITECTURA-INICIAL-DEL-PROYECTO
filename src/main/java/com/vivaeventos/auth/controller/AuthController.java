package com.vivaeventos.auth.controller;

import com.vivaeventos.auth.domain.model.User;
import com.vivaeventos.auth.domain.model.Role;
import com.vivaeventos.auth.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login") // Endpoint para el login, Si las credenciales son correctas devuelve un JWT
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        String token = userService.login(email, password);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Login exitoso");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {

        Map<String, Object> response = new HashMap<>();

        // Evitar que creen ADMIN
        if (user.getRole() != null && user.getRole().name().equals("ADMIN")) {
            response.put("mensaje", "No está permitido asignar rol ADMIN");
            return ResponseEntity.badRequest().body(response);
        }

        // Si no envía rol → por defecto CLIENT
        if (user.getRole() == null) {
            user.setRole(Role.CLIENT);
        }

        // Guardar usuario
        User newUser = userService.save(user);

        response.put("mensaje", "Usuario registrado correctamente");
        response.put("user", newUser);

        return ResponseEntity.status(201).body(response);
    }
}
