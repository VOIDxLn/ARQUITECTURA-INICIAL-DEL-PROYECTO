package com.vivaeventos.auth.domain.model;

import java.time.LocalDate;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotEmpty(message = "No puede estar vacio")
    @Size(min=8, max=14, message="El Dni debe ser entre 8 y 14 caracteres")
    @Column(unique = true, nullable = false)
    private String dni;
    @NotEmpty(message="El nombre es obligatorio")
    private String name;
    @NotEmpty(message="El email es obligatorio")
    @jakarta.validation.constraints.Email(message="El email no es válido")
    @Column(unique = true, nullable = false)
    private String email;
    @Basic
    private String phone;
    @Size(min=8, message="La contraseña no debe ser menor a 8 caracteres")
    @Column(nullable= false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Basic
    private LocalDate dateBorn;
    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {

    }

    public User(Long id, String dni, String name, String email, String phone, String password, LocalDate dateBorn, Role role){
        this.id = id;
        this.dni = dni;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.dateBorn = dateBorn;
        this.role = role;
    }

}
