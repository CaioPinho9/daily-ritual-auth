package com.caio.pinho.auth.auth.security;

import java.time.Instant;

public record JwtClaims(String subject, Instant expiresAt) {
}
