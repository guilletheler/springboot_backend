package com.gt.backend.controller.admin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.gt.backend.model.sistema.UserRol;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@CrossOrigin(origins = { "http://localhost:4200" })
@RequestMapping("enums")
public class EnumsController {

    @Operation(summary = "Obtiene una lista de roles de usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRolesUsuario() {

        return ResponseEntity
                .ok(Arrays.asList(UserRol.values()).stream().map(UserRol::name).collect(Collectors.toList()));

    }

}
