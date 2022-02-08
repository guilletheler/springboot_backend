package com.gt.backend.service.sistema;

import java.util.Map;

import com.gt.backend.model.sistema.AppParam;
import com.gt.backend.repo.sistema.AppParamRepo;
import com.gt.toolbox.spb.webapps.commons.infra.service.QueryHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Service
public class AppParamService  {

	@Getter
	@Autowired
	private AppParamRepo repo;

	public Page<AppParam> findByFilter(Map<String, String> filters, Pageable pageable) {
		return repo.findAll(QueryHelper.getFilterSpecification(filters), pageable);
	}

	public AppParam findOrCreateParam(String paramName, String defaultValue) {
		return findOrCreateParam(paramName, defaultValue, null);
	}
	
	public AppParam findOrCreateParam(String paramName, String defaultValue, String observaciones) {

		AppParam param = repo.findByNombre(paramName).orElse(null);

		if(param == null) {
			param = new AppParam();
			param.setNombre(paramName);
			param.setValor(defaultValue);
			param.setCodigo(repo.nextCodigo());
			param = repo.save(param);
			param.setObservaciones(observaciones);
		}

		return param;
	}
	
}
