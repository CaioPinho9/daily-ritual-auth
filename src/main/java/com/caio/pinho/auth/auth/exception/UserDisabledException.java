package com.caio.pinho.auth.auth.exception;

public class UserDisabledException extends RuntimeException {

	public UserDisabledException() {
		super("User is disabled");
	}
}
