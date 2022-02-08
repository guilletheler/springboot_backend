package com.gt.backend.repo.personal;

import java.util.List;
import java.util.Optional;

import com.gt.backend.model.sistema.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Integer>, JpaSpecificationExecutor<Usuario> {

	Optional<Usuario> findByCodigo(Integer codigo);

	Optional<Usuario> findByUsernameAndPassword(String username, String password);

	Optional<Usuario> findByUsername(String username);

	Optional<Usuario> findByDocumentoAndPassword(String documento, String password);

	@Query("SELECT COALESCE(MAX(u.codigo), 0) + 1 FROM Usuario u")
	Integer nextCodigo();

    List<Usuario> findByUsernameOrNombre(String string, String string2);

}
