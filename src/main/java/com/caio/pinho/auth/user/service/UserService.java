package com.caio.pinho.auth.user.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.caio.pinho.auth.user.dto.UserCreateRequest;
import com.caio.pinho.auth.user.dto.UserResponse;
import com.caio.pinho.auth.user.exception.EmailAlreadyExistsException;
import com.caio.pinho.auth.user.exception.UserNotFoundException;
import com.caio.pinho.auth.user.model.User;
import com.caio.pinho.auth.user.repository.UserRepository;

import jakarta.validation.Valid;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserResponse create(@Valid UserCreateRequest request) {
		userRepository.findByEmail(request.email()).ifPresent(user -> {
			throw new EmailAlreadyExistsException(request.email());
		});

		LocalDateTime now = LocalDateTime.now();
		User user = new User();
		user.setName(request.name());
		user.setEmail(request.email());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setActive(true);
		user.setCreatedAt(now);
		user.setUpdatedAt(now);

		User savedUser = userRepository.save(user);
		return new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
	}

	public UserResponse me(User user) {
		return new UserResponse(user.getId(), user.getName(), user.getEmail());
	}

	public void disable(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
		user.setActive(false);
		user.setUpdatedAt(LocalDateTime.now());
		userRepository.save(user);
	}
}
