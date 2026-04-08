package com.caio.pinho.auth.auth.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.caio.pinho.auth.auth.repository.RefreshTokenRepository;
import com.caio.pinho.auth.user.model.User;
import com.caio.pinho.auth.user.repository.UserRepository;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

	private static final Pattern ACCESS_TOKEN_PATTERN = Pattern.compile("\"accessToken\":\"([^\"]+)\"");
	private static final Pattern REFRESH_TOKEN_PATTERN = Pattern.compile("refresh_token=([^;]+)");

	private final MockMvc mockMvc;
	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	AuthFlowIntegrationTest(
			MockMvc mockMvc,
			UserRepository userRepository,
			RefreshTokenRepository refreshTokenRepository,
			PasswordEncoder passwordEncoder) {
		this.mockMvc = mockMvc;
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@BeforeEach
	void setUp() {
		refreshTokenRepository.deleteAll();
		userRepository.deleteAll();

		User user = new User();
		user.setName("Alice");
		user.setEmail("alice@example.com");
		user.setPasswordHash(passwordEncoder.encode("Password1!"));
		user.setActive(true);
		user.setCreatedAt(java.time.LocalDateTime.now());
		user.setUpdatedAt(java.time.LocalDateTime.now());
		userRepository.save(user);
	}

	@Test
	void shouldLoginRefreshAndLogoutWithHttpOnlyCookie() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "alice@example.com",
								  "password": "Password1!"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isString())
				.andReturn();

		String loginCookie = loginResult.getResponse().getHeader(HttpHeaders.SET_COOKIE);
		assertNotNull(loginCookie);
		assertTrue(loginCookie.contains("refresh_token="));
		assertTrue(loginCookie.contains("HttpOnly"));

		String accessToken = extractValue(ACCESS_TOKEN_PATTERN, loginResult.getResponse().getContentAsString());
		String refreshToken = extractValue(REFRESH_TOKEN_PATTERN, loginCookie);
		Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);

		assertFalse(refreshTokenRepository.findAll().isEmpty());

		mockMvc.perform(get("/users/me")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("alice@example.com"));

		MvcResult refreshResult = mockMvc.perform(post("/auth/refresh")
						.cookie(refreshTokenCookie))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isString())
				.andReturn();

		String refreshedAccessToken = extractValue(ACCESS_TOKEN_PATTERN, refreshResult.getResponse().getContentAsString());
		assertNotNull(refreshedAccessToken);

		MvcResult logoutResult = mockMvc.perform(post("/auth/logout")
						.cookie(refreshTokenCookie))
				.andExpect(status().isNoContent())
				.andReturn();

		String logoutCookie = logoutResult.getResponse().getHeader(HttpHeaders.SET_COOKIE);
		assertNotNull(logoutCookie);
		assertTrue(logoutCookie.contains("refresh_token="));
		assertTrue(logoutCookie.contains("Max-Age=0"));
		assertTrue(refreshTokenRepository.findAll().stream().allMatch(token -> token.getRevokedAt() != null));

		mockMvc.perform(post("/auth/refresh")
						.cookie(refreshTokenCookie))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Invalid or expired refresh token"));
	}

	@Test
	void shouldDisableUserAndBlockLoginRefreshAndProtectedAccess() throws Exception {
		User user = userRepository.findByEmail("alice@example.com").orElseThrow();

		MvcResult loginResult = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "alice@example.com",
								  "password": "Password1!"
								}
								"""))
				.andExpect(status().isOk())
				.andReturn();

		String accessToken = extractValue(ACCESS_TOKEN_PATTERN, loginResult.getResponse().getContentAsString());
		String refreshToken = extractValue(
				REFRESH_TOKEN_PATTERN,
				loginResult.getResponse().getHeader(HttpHeaders.SET_COOKIE));

		mockMvc.perform(patch("/admin/users/{id}/disable", user.getId())
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNoContent());

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "alice@example.com",
								  "password": "Password1!"
								}
								"""))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message").value("User is disabled"));

		mockMvc.perform(post("/auth/refresh")
						.cookie(new Cookie("refresh_token", refreshToken)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message").value("User is disabled"));

		mockMvc.perform(get("/users/me")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isUnauthorized());
	}

	private String extractValue(Pattern pattern, String value) {
		Matcher matcher = pattern.matcher(value);
		if (!matcher.find()) {
			throw new IllegalStateException("Unable to extract value from: " + value);
		}
		return matcher.group(1);
	}
}
