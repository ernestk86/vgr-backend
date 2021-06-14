package com.vgrental.exceptions;

public class UsernameAlreadyExistException extends AuthorizationException {

	public UsernameAlreadyExistException() {
		super();
	}

	public UsernameAlreadyExistException(String message) {
		super(message);
	}

	public UsernameAlreadyExistException(Throwable cause) {
		super(cause);
	}

	public UsernameAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public UsernameAlreadyExistException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
