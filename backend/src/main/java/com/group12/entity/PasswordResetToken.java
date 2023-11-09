package com.group12.entity;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Entity
public class PasswordResetToken  {
    private static final int EXPIRATION = 60 * 24; //24 hours expiration
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private Optional<User> user;

    public Long getId() {
        return id;
    }

    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, Optional<User> user) {
        this.token = token;
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Optional<User> getUser() {
        return user;
    }

    public void setUser(Optional<User> user) {
        this.user = user;
    }
}


