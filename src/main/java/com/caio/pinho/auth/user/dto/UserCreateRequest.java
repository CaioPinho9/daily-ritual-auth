package com.caio.pinho.auth.user.dto;

import jakarta.validation.constraints.NotBlank;

import com.caio.pinho.auth.api.validation.ValidEmail;
import com.caio.pinho.auth.api.validation.ValidPassword;

public record UserCreateRequest(
		@NotBlank(message = "name is required")
		String name,
		@ValidEmail
		String email,
		@ValidPassword
		String password
) {
}
