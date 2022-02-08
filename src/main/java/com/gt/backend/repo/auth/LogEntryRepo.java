package com.gt.backend.repo.auth;

import java.util.Date;
import java.util.Optional;

import com.gt.backend.model.sistema.LogEntry;
import com.gt.backend.model.sistema.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface LogEntryRepo extends JpaRepository<LogEntry, Integer>,
		JpaSpecificationExecutor<LogEntry> {

	Optional<LogEntry> findByUsuario(Usuario usuario);

	void deleteByFechaHoraBefore(Date fecha);

	@Modifying
	@Query(value = "DELETE FROM logfile "
			+ "WHERE id NOT IN (SELECT id FROM logfile ORDER BY id DESC LIMIT :keepRows);", nativeQuery = true)
	void deleteOverflow(int keepRows);
}
