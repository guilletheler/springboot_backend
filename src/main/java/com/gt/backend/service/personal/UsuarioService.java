package com.gt.backend.service.personal;

import java.util.Map;

import com.gt.backend.model.sistema.Usuario;
import com.gt.backend.repo.personal.UsuarioRepo;
import com.gt.toolbox.spb.webapps.commons.infra.service.QueryHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;

@Service
@Transactional
public class UsuarioService {

	@Getter
	@Autowired
	UsuarioRepo repo;

	public Page<Usuario> findByFilter(Map<String, String> filters, Pageable pageable) {

		Specification<Usuario> filterSpecification = QueryHelper.getFilterSpecification(filters);

		return repo.findAll(filterSpecification, pageable);
	}

	public Usuario getCurrentUser() {
		if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null
				|| SecurityContextHolder.getContext().getAuthentication().getDetails() == null) {
			return null;
		}
		
		UserDetails uDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();

		Usuario usuario = getRepo().findByUsername(uDetails.getUsername()).orElse(null);

		return usuario;
	}

	public Usuario save(Usuario usuario) {
		return repo.save(usuario);
	}

	public String findOrCreateParam(String paramName, String defaultValue) {
		return findOrCreateParam(null, paramName, defaultValue);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String findOrCreateParam(Usuario usuario, String paramName, String defaultValue) {

		if (usuario == null) {
			usuario = getCurrentUser();
		}

		Integer idUsuario = usuario.getId();

		usuario = getRepo().findById(idUsuario).orElse(null);

		if (usuario == null) {
			throw new RuntimeException("No se puede encontrar usuario con id " + idUsuario);
		}

		if (!usuario.getParametros().containsKey(paramName)) {
			usuario.getParametros().put(paramName, defaultValue);
			usuario = getRepo().save(usuario);
		}

		return usuario.getParametros().get(paramName);
	}

	public Usuario saveParam(String paramName, String defaultValue) {
		return saveParam(null, paramName, defaultValue);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Usuario saveParam(Usuario usuario, String paramName, String defaultValue) {
		if (usuario == null) {
			usuario = getCurrentUser();
		}

		Integer idUsuario = usuario.getId();

		usuario = getRepo().findById(idUsuario).orElse(null);

		if (usuario == null) {
			throw new RuntimeException("No se puede encontrar usuario con id " + idUsuario);
		}

		usuario.getParametros().put(paramName, defaultValue);

		return getRepo().save(usuario);
	}
	
}
