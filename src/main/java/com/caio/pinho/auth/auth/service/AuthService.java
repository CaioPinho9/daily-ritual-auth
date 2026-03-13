package com.caio.pinho.auth.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.caio.pinho.auth.auth.dto.AccessTokenResponse;
import com.caio.pinho.auth.auth.dto.AuthTokens;
import com.caio.pinho.auth.auth.dto.LoginRequest;
import com.caio.pinho.auth.auth.exception.InvalidCredentialsException;
import com.caio.pinho.auth.auth.exception.InvalidRefreshTokenException;
import com.caio.pinho.auth.auth.security.JwtService;
import com.caio.pinho.auth.user.model.User;
import com.caio.pinho.auth.user.repository.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;
	private final JwtService jwtService;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			RefreshTokenService refreshTokenService,
			JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.refreshTokenService = refreshTokenService;
		this.jwtService = jwtService;
	}

	@Transactional
	public AuthTokens login(LoginRequest loginRequest) {
		User user = userRepository.findByEmail(loginRequest.email())
				.filter(foundUser -> Boolean.TRUE.equals(foundUser.getActive()))
				.filter(foundUser -> passwordEncoder.matches(loginRequest.password(), foundUser.getPasswordHash()))
				.orElseThrow(InvalidCredentialsException::new);

		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = refreshTokenService.createRefreshToken(user);
		return new AuthTokens(accessToken, refreshToken);
	}

	@Transactional(readOnly = true)
	public AccessTokenResponse refresh(String rawRefreshToken) {
		User user = refreshTokenService.validateAndGetUser(rawRefreshToken)
				.orElseThrow(InvalidRefreshTokenException::new);
		return new AccessTokenResponse(jwtService.generateAccessToken(user));
	}

	@Transactional
	public void logout(String rawRefreshToken) {
		refreshTokenService.revoke(rawRefreshToken);
	}
}
