package com.caio.pinho.auth.auth.security;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.caio.pinho.auth.user.repository.UserRepository;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authorizationHeader = request.getHeader("Authorization");
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7);
			jwtService.parseClaims(token)
					.flatMap(claims -> userRepository.findById(Long.parseLong(claims.subject())))
					.filter(user -> Boolean.TRUE.equals(user.getActive()))
					.ifPresent(user -> {
						UsernamePasswordAuthenticationToken authentication =
								new UsernamePasswordAuthenticationToken(user, token, Collections.emptyList());
						SecurityContextHolder.getContext().setAuthentication(authentication);
					});
		}

		filterChain.doFilter(request, response);
	}
}
