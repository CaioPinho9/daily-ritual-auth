package com.caio.pinho.auth.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.caio.pinho.auth.auth.model.RefreshToken;
import com.caio.pinho.auth.auth.repository.RefreshTokenRepository;
import com.caio.pinho.auth.user.factory.UserFactory;
import com.caio.pinho.auth.user.model.User;

@DataJpaTest
@ActiveProfiles("test")
@Import(RefreshTokenService.class)
class RefreshTokenServiceTest {

	private final RefreshTokenService refreshTokenService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final EntityManager entityManager;

	@Autowired
	RefreshTokenServiceTest(
			RefreshTokenService refreshTokenService,
			RefreshTokenRepository refreshTokenRepository,
			EntityManager entityManager) {
		this.refreshTokenService = refreshTokenService;
		this.refreshTokenRepository = refreshTokenRepository;
		this.entityManager = entityManager;
	}

	@Test
	void shouldCreateAndValidateRefreshToken() {
		User user = UserFactory.create("Alice", "alice@example.com");
		entityManager.persist(user);
		entityManager.flush();

		String rawToken = refreshTokenService.createRefreshToken(user);
		RefreshToken savedToken = refreshTokenRepository.findAll().get(0);

		assertNotEquals(rawToken, savedToken.getTokenHash());
		assertEquals(user.getId(), savedToken.getUser().getId());
		RefreshTokenService.RefreshTokenValidationResult validationResult = refreshTokenService.validate(rawToken);
		assertTrue(validationResult.user().isPresent());
		assertEquals(user.getId(), validationResult.user().orElseThrow().getId());
	}

	@TestConfiguration
	static class TestConfig {

		@Bean
		Clock clock() {
			return Clock.fixed(Instant.parse("2026-03-13T12:00:00Z"), ZoneOffset.UTC);
		}
	}
}
