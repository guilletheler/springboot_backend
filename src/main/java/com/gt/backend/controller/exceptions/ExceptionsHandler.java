package com.gt.backend.controller.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.java.Log;

@Log
@RestControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ Exception.class, BackendException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public final ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {

		log.log(Level.SEVERE, "BACKEND EXCEPTION", ex);

		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());

		return ResponseEntity.internalServerError().body(ex.getMessage());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public final ResponseEntity<?> handleEntityNotFoundExceptions(EntityNotFoundException ex,
			WebRequest request) {

		log.log(Level.SEVERE, "ENTIDAD NO ENCONTRADA", ex);

		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(BackendAuthenticationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public final ResponseEntity<?> handleAuthenticationExceptions(BackendAuthenticationException ex,
			WebRequest request) {

		log.log(Level.SEVERE, "ERROR DE AUTENTICACIÓN");

		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

	@ExceptionHandler(TransactionSystemException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public final ResponseEntity<?> handleTransacionSystemExceptions(TransactionSystemException ex,
			WebRequest request) {

		Throwable cause = ex;

		while (cause.getCause() != null) {
			cause = cause.getCause();
			if (cause instanceof ConstraintViolationException) {
				return handleConstraintViolationExceptions((ConstraintViolationException) cause, request);
			}
		}

		log.log(Level.SEVERE, "error al guardar entidad", ex);

		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ERROR DE TRANSACCION");
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public final ResponseEntity<?> handleConstraintViolationExceptions(ConstraintViolationException ex,
			WebRequest request) {

		String message = "";

		for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
			if (!message.isEmpty()) {
				message += ", ";
			}
			message += constraintViolation.getMessage();
		}

		log.log(Level.SEVERE, "error al guardar entidad " + message);

		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
	}
}
