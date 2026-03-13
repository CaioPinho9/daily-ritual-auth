package com.caio.pinho.auth.user.factory;

import java.time.LocalDateTime;

import com.caio.pinho.auth.user.model.User;

public final class UserFactory {

	private UserFactory() {
	}

	public static User create(String name, String email) {
		LocalDateTime now = LocalDateTime.of(2026, 3, 13, 12, 0);
		User user = new User();

		user.setName(name);
		user.setEmail(email);
		user.setPasswordHash("hashed-password");
		user.setActive(true);
		user.setCreatedAt(now);
		user.setUpdatedAt(now);

		return user;
	}
}
