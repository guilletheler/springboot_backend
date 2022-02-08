package com.gt.backend.controller.exceptions;

public class BackendException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BackendException() {
		super();
	}

	public BackendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BackendException(String message, Throwable cause) {
		super(message, cause);
	}

	public BackendException(String message) {
		super(message);
	}

	public BackendException(Throwable cause) {
		super(cause);
	}

	
}
