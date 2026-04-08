package com.caio.pinho.auth.auth.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.caio.pinho.auth.api.dto.ErrorResponse;
import com.caio.pinho.auth.auth.exception.InvalidCredentialsException;
import com.caio.pinho.auth.auth.exception.InvalidRefreshTokenException;
import com.caio.pinho.auth.auth.exception.UserDisabledException;

@RestControllerAdvice(basePackages = "com.caio.pinho.auth.auth.controller")
public class AuthRestControllerAdvice {

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception) {
		ErrorResponse error = ErrorResponse.simple(exception.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(InvalidRefreshTokenException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException exception) {
		ErrorResponse error = ErrorResponse.simple(exception.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(UserDisabledException.class)
	public ResponseEntity<ErrorResponse> handleUserDisabled(UserDisabledException exception) {
		ErrorResponse error = ErrorResponse.simple(exception.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}
}
