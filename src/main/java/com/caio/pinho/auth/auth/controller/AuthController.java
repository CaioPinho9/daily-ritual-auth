package com.caio.pinho.auth.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caio.pinho.auth.auth.dto.AccessTokenResponse;
import com.caio.pinho.auth.auth.dto.AuthTokens;
import com.caio.pinho.auth.auth.dto.LoginRequest;
import com.caio.pinho.auth.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

	private final AuthService authService;
	private final long refreshTokenTtlDays;

	public AuthController(AuthService authService, @Value("${auth.refresh-token.ttl-days:30}") long refreshTokenTtlDays) {
		this.authService = authService;
		this.refreshTokenTtlDays = refreshTokenTtlDays;
	}

	@PostMapping("/login")
	public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
		AuthTokens authTokens = authService.login(loginRequest);
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, buildRefreshTokenCookie(authTokens.refreshToken()).toString())
				.body(new AccessTokenResponse(authTokens.accessToken()));
	}

	@PostMapping("/refresh")
	public AccessTokenResponse refresh(@CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
		return authService.refresh(refreshToken);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
		authService.logout(refreshToken);
		return ResponseEntity.noContent()
				.header(HttpHeaders.SET_COOKIE, clearRefreshTokenCookie().toString())
				.build();
	}

	private ResponseCookie buildRefreshTokenCookie(String rawRefreshToken) {
		return ResponseCookie.from(REFRESH_TOKEN_COOKIE, rawRefreshToken)
				.httpOnly(true)
				.secure(false)
				.path("/")
				.sameSite("Lax")
				.maxAge(refreshTokenTtlDays * 24 * 60 * 60)
				.build();
	}

	private ResponseCookie clearRefreshTokenCookie() {
		return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
				.httpOnly(true)
				.secure(false)
				.path("/")
				.sameSite("Lax")
				.maxAge(0)
				.build();
	}
}
