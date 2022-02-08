package com.gt.backend.service.auth;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.gt.backend.controller.exceptions.BackendException;
import com.gt.backend.model.sistema.Usuario;
import com.gt.backend.repo.personal.UsuarioRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    UsuarioRepo usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws BackendException {
        Optional<Usuario> usuario = usuarioRepo.findByUsername(username);

        if (usuario.isPresent()) {
            return loadByUser(usuario.get());
        } else {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Usuario " + username + " no encontrado");
            throw new BackendException("User not found with username: " + username);
        }
    }

    public UserDetails loadByUser(Usuario usuario) {
        return new User(usuario.getUsername(), usuario.getPassword(), usuario.getRoles().stream()
                    .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.name().trim())).collect(Collectors.toList()));
    }
}
