package com.caio.pinho.auth.auth.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import com.caio.pinho.auth.auth.factory.RefreshTokenFactory;
import com.caio.pinho.auth.auth.model.RefreshToken;
import com.caio.pinho.auth.user.factory.UserFactory;
import com.caio.pinho.auth.user.model.User;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class RefreshTokenRepositoryTest {

	private final RefreshTokenRepository refreshTokenRepository;

	private final EntityManager entityManager;

	@Autowired
	RefreshTokenRepositoryTest(RefreshTokenRepository refreshTokenRepository, EntityManager entityManager) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.entityManager = entityManager;
	}

	@Test
	void shouldFindRefreshTokenByTokenHash() {
		String email = "refresh@example.com";
		String tokenHash = "token-hash-123";
		User user = UserFactory.create("Refresh User", email);

		entityManager.persist(user);

		RefreshToken refreshToken = RefreshTokenFactory.create(user, tokenHash);
		entityManager.persist(refreshToken);
		entityManager.flush();
		entityManager.clear();

		Optional<RefreshToken> result = refreshTokenRepository.findByTokenHash(tokenHash);

		assertTrue(result.isPresent());
		assertNotNull(result.get().getUuid());
		assertEquals(tokenHash, result.get().getTokenHash());

		User persistedUser = result.get().getUser();
		assertNotNull(persistedUser);
		assertEquals(email, persistedUser.getEmail());
	}

	@Test
	void shouldReturnEmptyWhenTokenHashDoesNotExist() {
		Optional<RefreshToken> result = refreshTokenRepository.findByTokenHash("missing-token-hash");

		assertTrue(result.isEmpty());
	}
}
