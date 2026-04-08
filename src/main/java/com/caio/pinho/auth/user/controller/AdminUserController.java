package com.caio.pinho.auth.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.caio.pinho.auth.user.service.UserService;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

	private final UserService userService;

	public AdminUserController(UserService userService) {
		this.userService = userService;
	}

	@PatchMapping("/{id}/disable")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void disable(@PathVariable Long id) {
		userService.disable(id);
	}
}
