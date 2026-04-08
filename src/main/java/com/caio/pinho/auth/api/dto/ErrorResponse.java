package com.caio.pinho.auth.api.dto;

import java.util.Map;

public record ErrorResponse(
        String message,
        Map<String, String> details
) {
	public static ErrorResponse simple(String message) {
		return new ErrorResponse(message, null);
	}
}
