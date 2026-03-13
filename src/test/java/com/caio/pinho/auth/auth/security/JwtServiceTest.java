package com.caio.pinho.auth.auth.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.caio.pinho.auth.user.factory.UserFactory;
import com.caio.pinho.auth.user.model.User;

class JwtServiceTest {

	@Test
	void shouldGenerateTokenWithSubjectAndExpiry() {
		Clock fixedClock = Clock.fixed(Instant.parse("2026-03-13T12:00:00Z"), ZoneOffset.UTC);
		JwtService jwtService = new JwtService(fixedClock, "unit-test-jwt-secret-unit-test-jwt-secret", 900);
		User user = UserFactory.create("Alice", "alice@example.com");
		user.setId(42L);

		String token = jwtService.generateAccessToken(user);
		JwtClaims claims = jwtService.parseClaims(token).orElseThrow();

		assertTrue(jwtService.isValid(token));
		assertEquals("42", claims.subject());
		assertEquals(Instant.parse("2026-03-13T12:15:00Z"), claims.expiresAt());
	}
}
