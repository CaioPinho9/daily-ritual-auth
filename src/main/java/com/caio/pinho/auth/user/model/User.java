package com.caio.pinho.auth.user.model;

import java.time.LocalDateTime;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "tb_users")
public class User {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "co_seq_user")
	    private Long id;

	    @Column(name = "no_name", nullable = false)
	    private String name;

	    @Column(name = "no_email", nullable = false, unique = true)
	    private String email;

	    @Column(name = "no_password_hash", nullable = false)
	    private String passwordHash;

	    @Column(name = "st_active", nullable = false)
	    private Boolean active;

	    @Column(name = "dt_created_at", nullable = false)
	    private LocalDateTime createdAt;

	    @Column(name = "dt_updated_at", nullable = false)
	    private LocalDateTime updatedAt;
}
