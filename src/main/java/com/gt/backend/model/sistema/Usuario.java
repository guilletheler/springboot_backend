/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gt.backend.model.sistema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gt.backend.model.CodigoNombre;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Tabla de usuarios para el inicio de sesion
 *
 * @author guille
 */

@Entity
@Table(name = "usuarios")
@Data
@EqualsAndHashCode(callSuper = true)
public class Usuario extends CodigoNombre implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Size(min = 5, max = 80, message = "username tiene que ser entre 5 y 80")
	@Column(unique = true)
	private String username;

	@JsonIgnore
	private String password;

	@Column(length = 255)
	private String legajo;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Getter(value = AccessLevel.NONE)
	@ElementCollection(targetClass = UserRol.class, fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING) // Possibly optional (I'm not sure) but defaults to ORDINAL.
	@CollectionTable(name = "usuario_rol")
	@Column(name = "role_name") // Column name in person_interest
	private Set<UserRol> roles;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Getter(value = AccessLevel.NONE)
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "clave")
	@Column(name = "valor", length = 255)
	@CollectionTable(name = "parametros_usuarios", joinColumns = @JoinColumn(name = "parametros_usuario_id"))
	Map<String, String> parametros; // maps from attribute name to value

	private Boolean activo;

	@Temporal(javax.persistence.TemporalType.DATE)
	private Date fechaAlta;

	@Temporal(javax.persistence.TemporalType.DATE)
	private Date vencimientoPass;

	@JsonIgnore
	private String initPage;

	@Column(length = 10000)
	private String observaciones;

	private String documento;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Getter(value = AccessLevel.NONE)
	@ElementCollection
	@CollectionTable(name = "telefonos_usuarios", joinColumns = @JoinColumn(name = "usuario_id"))
	@Column(name = "telefono")
	private List<String> telefonos;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Getter(value = AccessLevel.NONE)
	@ElementCollection
	@CollectionTable(name = "email_usuarios", joinColumns = @JoinColumn(name = "usuario_id"))
	@Column(name = "emails")
	private List<String> emails;

	public void setAndEncryptPassword(String unencryptedPasswd) {

		this.setPassword(org.apache.commons.codec.digest.DigestUtils.sha256Hex(unencryptedPasswd));
	}

	public Set<UserRol> getRoles() {
		if (roles == null) {
			roles = new HashSet<>();
		}
		return roles;
	}

	public Map<String, String> getParametros() {
		if (parametros == null) {
			parametros = new HashMap<>();
		}
		return parametros;
	}

	public List<String> getEmails() {
		if (emails == null) {
			emails = new ArrayList<>();
		}

		return emails;
	}

	public List<String> getTelefonos() {
		if (telefonos == null) {
			telefonos = new ArrayList<>();
		}

		return telefonos;
	}

}
