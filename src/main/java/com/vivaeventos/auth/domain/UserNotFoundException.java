package com.vivaeventos.auth.domain;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String dni) {
        super("Usuerio con dni " + dni + " no encontrado");
    }
}
