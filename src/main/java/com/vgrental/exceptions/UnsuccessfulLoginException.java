package com.vgrental.exceptions;

public class UnsuccessfulLoginException extends AuthorizationException {

	public UnsuccessfulLoginException() {
		super();
	}

	public UnsuccessfulLoginException(String message) {
		super(message);
	}

	public UnsuccessfulLoginException(Throwable cause) {
		super(cause);
	}

	public UnsuccessfulLoginException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsuccessfulLoginException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}