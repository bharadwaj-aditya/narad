package com.narad.exception;

public class NaradWorkerException extends NaradException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4497109153342715231L;

	private ExceptionErrorCodes errorCode;
	private String workerName;

	public NaradWorkerException(String message, Throwable cause, ExceptionErrorCodes errorCode, String workerName) {
		super(message, cause);
		this.errorCode = errorCode;
		this.workerName = workerName;
	}

	public int getErrorCodeNum() {
		return errorCode.getErrorCode();
	}

	public ExceptionErrorCodes getErrorCode() {
		return errorCode;
	}

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public String getMessage() {
		StringBuilder br = new StringBuilder();
		br.append("Exception: ").append(errorCode.getMessage());
		br.append(" in worker: ").append(workerName);
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
