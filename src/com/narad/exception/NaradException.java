package com.narad.exception;

public class NaradException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 585224515658667320L;

	public NaradException() {
		super();
	}

	public NaradException(String message, Throwable t) {
		super(message, t);
	}

}
