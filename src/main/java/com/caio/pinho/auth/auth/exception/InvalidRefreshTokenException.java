package com.caio.pinho.auth.auth.exception;

public class InvalidRefreshTokenException extends RuntimeException {

	public InvalidRefreshTokenException() {
		super("Invalid or expired refresh token");
	}
}
