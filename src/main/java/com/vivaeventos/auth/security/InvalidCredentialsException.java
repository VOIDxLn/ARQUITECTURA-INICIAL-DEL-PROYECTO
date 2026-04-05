package com.vivaeventos.auth.security;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(){
        super("Credenciales invalidas");
    }
}
