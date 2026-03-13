package com.caio.pinho.auth.user.repository;

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

import com.caio.pinho.auth.user.factory.UserFactory;
import com.caio.pinho.auth.user.model.User;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest {

	private final UserRepository userRepository;

	private final EntityManager entityManager;

	@Autowired
	UserRepositoryTest(UserRepository userRepository, EntityManager entityManager) {
		this.userRepository = userRepository;
		this.entityManager = entityManager;
	}

	@Test
	void shouldFindUserByEmail() {
		String email = "alice@example.com";
		User user = UserFactory.create("Alice", email);

		entityManager.persist(user);
		entityManager.flush();
		entityManager.clear();

		Optional<User> result = userRepository.findByEmail(email);

		assertTrue(result.isPresent());
		assertNotNull(result.get().getId());
		assertEquals(email, result.get().getEmail());
		assertEquals("Alice", result.get().getName());
	}

	@Test
	void shouldReturnEmptyWhenEmailDoesNotExist() {
		Optional<User> result = userRepository.findByEmail("missing@example.com");

		assertTrue(result.isEmpty());
	}
}
