package com.caio.pinho.auth.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caio.pinho.auth.auth.model.RefreshToken;
import com.caio.pinho.auth.auth.repository.RefreshTokenRepository;
import com.caio.pinho.auth.user.model.User;

@Service
public class RefreshTokenService {

	private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

	private final RefreshTokenRepository refreshTokenRepository;
	private final Clock clock;
	private final long refreshTokenTtlDays;
	private final SecureRandom secureRandom = new SecureRandom();

	public RefreshTokenService(
			RefreshTokenRepository refreshTokenRepository,
			Clock clock,
			@Value("${auth.refresh-token.ttl-days:30}") long refreshTokenTtlDays) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.clock = clock;
		this.refreshTokenTtlDays = refreshTokenTtlDays;
	}

	@Transactional
	public String createRefreshToken(User user) {
		String rawToken = generateRawToken();
		RefreshToken refreshToken = new RefreshToken();
		LocalDateTime now = LocalDateTime.now(clock);

		refreshToken.setUser(user);
		refreshToken.setTokenHash(hash(rawToken));
		refreshToken.setCreatedAt(now);
		refreshToken.setExpiresAt(now.plusDays(refreshTokenTtlDays));
		refreshToken.setRevokedAt(null);
		refreshTokenRepository.save(refreshToken);

		return rawToken;
	}

	@Transactional(readOnly = true)
	public Optional<User> validateAndGetUser(String rawToken) {
		if (rawToken == null || rawToken.isBlank()) {
			return Optional.empty();
		}

		LocalDateTime now = LocalDateTime.now(clock);
		return refreshTokenRepository.findByTokenHash(hash(rawToken))
				.filter(refreshToken -> refreshToken.getRevokedAt() == null)
				.filter(refreshToken -> refreshToken.getExpiresAt().isAfter(now))
				.map(RefreshToken::getUser)
				.filter(user -> Boolean.TRUE.equals(user.getActive()));
	}

	@Transactional
	public void revoke(String rawToken) {
		if (rawToken == null || rawToken.isBlank()) {
			return;
		}

		refreshTokenRepository.findByTokenHash(hash(rawToken))
				.filter(refreshToken -> refreshToken.getRevokedAt() == null)
				.ifPresent(refreshToken -> refreshToken.setRevokedAt(LocalDateTime.now(clock)));
	}

	String hash(String rawToken) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] digest = messageDigest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest);
		}
		catch (Exception exception) {
			throw new IllegalStateException("Unable to hash refresh token", exception);
		}
	}

	long getRefreshTokenTtlDays() {
		return refreshTokenTtlDays;
	}

	private String generateRawToken() {
		byte[] bytes = new byte[32];
		secureRandom.nextBytes(bytes);
		return BASE64_URL_ENCODER.encodeToString(bytes);
	}
}
