package com.caio.pinho.auth.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caio.pinho.auth.user.dto.UserCreateRequest;
import com.caio.pinho.auth.user.dto.UserResponse;
import com.caio.pinho.auth.user.model.User;
import com.caio.pinho.auth.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse signup(@Valid @RequestBody UserCreateRequest request) {
		return userService.create(request);
	}

	@GetMapping("/me")
	public UserResponse me(@AuthenticationPrincipal Object principal) {
		if (!(principal instanceof User user)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		return userService.me(user);
	}
}
