package com.caio.pinho.auth.user.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.Test;

class UserCreateRequestValidationTest {

	private final Validator validator;

	UserCreateRequestValidationTest() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();
	}

	@Test
	void shouldApplyReusableValidationForEmailAndPassword() {
		UserCreateRequest request = new UserCreateRequest("", "invalid-email", "123");

		Set<String> messages = validator.validate(request).stream()
				.map(violation -> violation.getMessage())
				.collect(java.util.stream.Collectors.toSet());

		assertTrue(messages.contains("name is required"));
		assertTrue(messages.contains("email must be valid"));
		assertTrue(messages.contains("password must have at least 8 characters"));
	}

	@Test
	void shouldRequirePasswordToContainNumberAndSymbol() {
		UserCreateRequest request = new UserCreateRequest("Alice", "alice@example.com", "abcdefgh");

		Set<String> messages = validator.validate(request).stream()
				.map(violation -> violation.getMessage())
				.collect(java.util.stream.Collectors.toSet());

		assertTrue(messages.contains("password must contain at least one number and one symbol"));
	}
}
