package com.caio.pinho.auth.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.caio.pinho.auth.user.dto.UserCreateRequest;

class UserControllerTest {

	@Test
	void signupShouldReturnCreatedStatus() throws NoSuchMethodException {
		Method method = UserController.class.getMethod("signup", UserCreateRequest.class);

		ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);

		assertNotNull(responseStatus);
		assertEquals(HttpStatus.CREATED, responseStatus.value());
	}
}
