package com.gt.backend.repo.sistema;

import java.util.Optional;

import com.gt.backend.model.sistema.AppParam;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppParamRepo extends PagingAndSortingRepository<AppParam, Integer>,
		JpaSpecificationExecutor<AppParam> {

	Optional<AppParam> findByNombre(String name);
	
	@Query("SELECT COALESCE(MAX(ap.codigo), 0) + 1 FROM AppParam ap")
	Integer nextCodigo();
	
	Boolean existsByNombre(String nombre);
}
