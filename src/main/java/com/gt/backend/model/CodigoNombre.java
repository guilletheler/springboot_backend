/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gt.backend.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author guille
 */
@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class CodigoNombre  {

	@NotNull(message = "El código no puede ser nulo")
	@PositiveOrZero(message = "El código debe ser un valor positivo")
	Integer codigo;

	@NotNull(message = "El nombre no puede ser nulo")
	@Column(length = 255)
	String nombre;

}
