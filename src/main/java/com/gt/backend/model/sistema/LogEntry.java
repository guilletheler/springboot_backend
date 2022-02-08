package com.gt.backend.model.sistema;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

import lombok.Data;

/**
 *
 * @author guille
 */
@Entity(name = "logfile")
@Data
public class LogEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	private Usuario usuario;

	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date fechaHora;

	@Column(length = 10000)
	private String detalle;

}
