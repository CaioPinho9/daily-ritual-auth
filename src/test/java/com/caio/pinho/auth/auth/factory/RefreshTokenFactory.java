package com.caio.pinho.auth.auth.factory;

import java.time.LocalDateTime;

import com.caio.pinho.auth.auth.model.RefreshToken;
import com.caio.pinho.auth.user.model.User;

public final class RefreshTokenFactory {

	private RefreshTokenFactory() {
	}

	public static RefreshToken create(User user, String tokenHash) {
		LocalDateTime now = LocalDateTime.of(2026, 3, 13, 12, 30);
		RefreshToken refreshToken = new RefreshToken();

		refreshToken.setUser(user);
		refreshToken.setTokenHash(tokenHash);
		refreshToken.setExpiresAt(now.plusDays(30));
		refreshToken.setCreatedAt(now);

		return refreshToken;
	}
}
