package com.gt.backend.dto;

import com.gt.backend.model.sistema.UserRol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    Integer id;
    Integer codigo;
    String nombre;
    String username;
    UserRol[] roles;
    String token;
    String unencryptedPassword;
}
