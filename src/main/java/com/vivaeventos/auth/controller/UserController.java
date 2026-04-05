package com.vivaeventos.auth.controller;

import com.vivaeventos.auth.domain.ValidationException;
import com.vivaeventos.auth.domain.model.User;
import com.vivaeventos.auth.service.UserService;

import jakarta.validation.Valid;

import java.util.HashMap;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.Map;
import org.springframework.validation.BindingResult;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    private static final String MENSAJE = "mensaje";
    private static final String USER = "user";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> save(@Valid @RequestBody User user, BindingResult result) {
        if(result.hasErrors()){
            throw new ValidationException(result);
        }
        
        Map<String, Object> response = new HashMap<>();
        User newUser = userService.save(user);
        response.put(MENSAJE, "El usuario ha sido creado con éxito!");
        response.put(USER, newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{dni}")
    public void delete(@PathVariable String dni) {
        userService.deleteByDni(dni);
    }

    @PutMapping("/{dni}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable String dni, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        user.setDni(dni);
        User updatedUser = userService.update(user);

        response.put(MENSAJE, "Usuario actualizado correctamente");
        response.put(USER, updatedUser);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<User>> getUsers(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 4);
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String dni) {
        User user = userService.findByDni(dni);
        Map<String, Object> response = new HashMap<>();
        response.put(USER, user);
        return ResponseEntity.ok(response);
    }

}
