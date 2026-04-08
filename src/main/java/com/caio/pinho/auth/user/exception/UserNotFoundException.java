package com.caio.pinho.auth.user.exception;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(Long userId) {
		super("User not found: " + userId);
	}
}
