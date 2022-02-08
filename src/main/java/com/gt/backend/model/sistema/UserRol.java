package com.gt.backend.model.sistema;

import lombok.Getter;

public enum UserRol {
    ADMIN("Administrador de sistemas"),
    USER("Usuario autenticado");

    @Getter
    String nombre;

    private UserRol(String nombre) {
        this.nombre = nombre;
    }
}
