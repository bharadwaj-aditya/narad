package com.narad.exception;

public class NaradDataAccessException extends NaradException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 977480102645816940L;

	private ExceptionErrorCodes errorCode;
	private String dbService;

	public NaradDataAccessException(String message, Throwable cause, ExceptionErrorCodes errorCode, String dbService) {
		super(message, cause);
		this.errorCode = errorCode;
		this.dbService = dbService;
	}

	public int getErrorCodeNum() {
		return errorCode.getErrorCode();
	}

	public ExceptionErrorCodes getErrorCode() {
		return errorCode;
	}

	public String getDbService() {
		return dbService;
	}

	public String getMessage() {
		StringBuilder br = new StringBuilder();
		br.append("Exception: ").append(errorCode.getMessage());
		br.append(" in service: ").append(dbService);
		Throwable cause = getCause();
		if (cause != null) {
			br.append(" due to: ").append(cause.getMessage());
		}
		String message = super.getMessage();
		if (message != null) {
			br.append(" ").append(message);
		}
		return br.toString();
	}

}
