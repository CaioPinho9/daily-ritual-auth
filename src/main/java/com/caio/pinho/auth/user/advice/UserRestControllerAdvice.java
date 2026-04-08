package com.caio.pinho.auth.user.advice;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.caio.pinho.auth.api.dto.ErrorResponse;
import com.caio.pinho.auth.user.exception.EmailAlreadyExistsException;
import com.caio.pinho.auth.user.exception.UserNotFoundException;

@RestControllerAdvice(basePackages = "com.caio.pinho.auth.user.controller")
public class UserRestControllerAdvice {

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException exception) {
		ErrorResponse error = ErrorResponse.simple(exception.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException exception) {
		ErrorResponse error = ErrorResponse.simple(exception.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
		Map<String, String> details = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(fieldError -> details.put(fieldError.getField(), fieldError.getDefaultMessage()));

		ErrorResponse error = new ErrorResponse("Request validation failed", details);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
}
