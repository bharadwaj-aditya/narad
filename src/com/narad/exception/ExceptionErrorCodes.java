package com.narad.exception;

public enum ExceptionErrorCodes {

	// DATA ACCESS
	DATA_ACCESS_MULTIPLE_MATCHES(101, "More than one match for given criteria."), 
	DATA_ACCESS_INCOMPLETE_NETWORK_INFORMATION(102, "Network not found."),
	DATA_ACCESS_INCOMPLETE_EMAIL_INFORMATION(103, "Email not found."),
	DATA_ACCESS_EMAIL_CONFLICT(104, "Another person with same email found."),

	// WORKER
	WORKER_NODE_NOT_FOUND(201, "Could not find node with given criteria.");

	private int errorCode;
	private String message;

	ExceptionErrorCodes(int errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}
}
