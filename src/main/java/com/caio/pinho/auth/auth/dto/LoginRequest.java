package com.caio.pinho.auth.auth.dto;

import com.caio.pinho.auth.api.validation.ValidEmail;
import com.caio.pinho.auth.api.validation.ValidPassword;

public record LoginRequest(
		@ValidEmail
		String email,
		@ValidPassword
		String password
) {
}
