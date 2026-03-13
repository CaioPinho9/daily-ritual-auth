package com.caio.pinho.auth.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.caio.pinho.auth.user.model.User;

@Entity
@Data
@Table(name = "tb_refresh_tokens")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "co_seq_refresh_token")
	private UUID uuid;

	@ManyToOne
	@JoinColumn(name = "co_user", nullable = false)
	private User user;

	@Column(name = "no_token_hash", nullable = false)
	private String tokenHash;

	@Column(name = "dt_expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "dt_revoked_at")
	private LocalDateTime revokedAt;

	@Column(name = "dt_created_at", nullable = false)
	private LocalDateTime createdAt;
}
