package com.gt.backend.service.sistema;

import java.util.Date;
import java.util.Map;

import com.gt.backend.model.sistema.AppParam;
import com.gt.backend.model.sistema.LogEntry;
import com.gt.backend.model.sistema.Usuario;
import com.gt.backend.repo.auth.LogEntryRepo;
import com.gt.backend.service.personal.UsuarioService;
import com.gt.toolbox.spb.webapps.commons.infra.service.QueryHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.extern.java.Log;

@Log
@Service
public class LogEntryService {

	@Getter
	@Autowired
	LogEntryRepo logEntryRepo;

	@Autowired
	AppParamService appParamService;
	
	@Autowired
	UsuarioService usuarioService;

	public Page<LogEntry> findByFilter(Map<String, String> filters, Pageable pageable) {
		return logEntryRepo.findAll(QueryHelper.getFilterSpecification(filters), pageable);
	}

	public void registrar(String texto) {
		registrar(texto, usuarioService.getCurrentUser());
	}

	public void registrar(String texto, Usuario usuario) {
		LogEntry logEntry = new LogEntry();
		logEntry.setFechaHora(new Date());
		logEntry.setUsuario(usuario);
		logEntry.setDetalle(texto);

		logEntryRepo.save(logEntry);
	}

	public void rotarBitacora() {

		AppParam paramRegistrosBitacora = appParamService.findOrCreateParam("REGISTROS_BITACORA", "1000");

		try {
			registrar("Eliminando los registros de la bitacora que superan los " + paramRegistrosBitacora.getValor());
			getLogEntryRepo().deleteOverflow(Integer.parseInt(paramRegistrosBitacora.getValor()));
		} catch (Exception ex) {
			log.severe("Error al eliminar el excedente de la bitácora");
		}
	}
}
