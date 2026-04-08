package com.caio.pinho.auth.user.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.caio.pinho.auth.api.dto.ErrorResponse;
import com.caio.pinho.auth.user.controller.UserController;
import com.caio.pinho.auth.user.dto.UserCreateRequest;
import com.caio.pinho.auth.user.exception.EmailAlreadyExistsException;

class UserRestControllerAdviceTest {

	private final UserRestControllerAdvice advice = new UserRestControllerAdvice();

	@Test
	void shouldReturnConflictForEmailAlreadyExists() {
		EmailAlreadyExistsException exception = new EmailAlreadyExistsException("alice@example.com");

		ResponseEntity<ErrorResponse> response = advice.handleEmailAlreadyExists(exception);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Email already exists: alice@example.com", response.getBody().message());
		assertNull(response.getBody().details());
	}

	@Test
	void shouldReturnBadRequestForValidationErrors() throws NoSuchMethodException {
		UserCreateRequest request = new UserCreateRequest("", "invalid-email", "abcdefgh");
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "userCreateRequest");
		bindingResult.addError(new FieldError("userCreateRequest", "name", "name is required"));
		bindingResult.addError(new FieldError("userCreateRequest", "email", "email must be valid"));
		bindingResult.addError(new FieldError("userCreateRequest", "password", "password must contain at least one number and one symbol"));

		Method method = UserController.class.getMethod("signup", UserCreateRequest.class);
		MethodParameter parameter = new MethodParameter(method, 0);
		MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

		ResponseEntity<ErrorResponse> response = advice.handleMethodArgumentNotValid(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Request validation failed", response.getBody().message());
		assertEquals("name is required", response.getBody().details().get("name"));
		assertEquals("email must be valid", response.getBody().details().get("email"));
		assertEquals("password must contain at least one number and one symbol", response.getBody().details().get("password"));
	}
}
