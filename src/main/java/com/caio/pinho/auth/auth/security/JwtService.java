package com.caio.pinho.auth.auth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.caio.pinho.auth.user.model.User;

@Service
public class JwtService {

	private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
	private static final String JWT_HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
	private static final Pattern SUBJECT_PATTERN = Pattern.compile("\"sub\":\"([^\"]+)\"");
	private static final Pattern EXPIRATION_PATTERN = Pattern.compile("\"exp\":(\\d+)");

	private final Clock clock;
	private final String secret;
	private final long accessTokenTtlSeconds;

	public JwtService(
			Clock clock,
			@Value("${auth.jwt.secret:dev-jwt-secret-change-me-dev-jwt-secret}") String secret,
			@Value("${auth.jwt.access-token-ttl-seconds:900}") long accessTokenTtlSeconds) {
		this.clock = clock;
		this.secret = secret;
		this.accessTokenTtlSeconds = accessTokenTtlSeconds;
	}

	public String generateAccessToken(User user) {
		Instant now = Instant.now(clock);
		Instant expiresAt = now.plusSeconds(accessTokenTtlSeconds);

		String header = encodeBase64Url(JWT_HEADER_JSON);
		String payload = encodeBase64Url(buildPayload(user.getId(), now, expiresAt));
		String signature = encodeBase64Url(sign(header + "." + payload));

		return header + "." + payload + "." + signature;
	}

	public boolean isValid(String token) {
		return parseClaims(token).isPresent();
	}

	public Optional<JwtClaims> parseClaims(String token) {
		if (token == null || token.isBlank()) {
			return Optional.empty();
		}

		String[] parts = token.split("\\.");
		if (parts.length != 3) {
			return Optional.empty();
		}

		String headerAndPayload = parts[0] + "." + parts[1];
		byte[] expectedSignature = sign(headerAndPayload);
		byte[] actualSignature;
		try {
			actualSignature = BASE64_URL_DECODER.decode(parts[2]);
		}
		catch (IllegalArgumentException exception) {
			return Optional.empty();
		}

		if (!MessageDigest.isEqual(expectedSignature, actualSignature)) {
			return Optional.empty();
		}

		String payloadJson;
		try {
			payloadJson = new String(BASE64_URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
		}
		catch (IllegalArgumentException exception) {
			return Optional.empty();
		}

		Matcher subjectMatcher = SUBJECT_PATTERN.matcher(payloadJson);
		Matcher expirationMatcher = EXPIRATION_PATTERN.matcher(payloadJson);
		if (!subjectMatcher.find() || !expirationMatcher.find()) {
			return Optional.empty();
		}

		Instant expiresAt = Instant.ofEpochSecond(Long.parseLong(expirationMatcher.group(1)));
		if (!expiresAt.isAfter(Instant.now(clock))) {
			return Optional.empty();
		}

		return Optional.of(new JwtClaims(subjectMatcher.group(1), expiresAt));
	}

	private String buildPayload(Long userId, Instant issuedAt, Instant expiresAt) {
		return "{\"sub\":\"" + userId + "\",\"iat\":" + issuedAt.getEpochSecond() + ",\"exp\":" + expiresAt.getEpochSecond() + "}";
	}

	private String encodeBase64Url(String value) {
		return encodeBase64Url(value.getBytes(StandardCharsets.UTF_8));
	}

	private String encodeBase64Url(byte[] value) {
		return BASE64_URL_ENCODER.encodeToString(value);
	}

	private byte[] sign(String value) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
		}
		catch (Exception exception) {
			throw new IllegalStateException("Unable to sign JWT", exception);
		}
	}
}
