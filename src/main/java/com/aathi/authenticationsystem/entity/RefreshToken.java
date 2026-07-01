package com.aathi.authenticationsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@SuppressWarnings("ALL")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name = "refresh_tokens",
        indexes = {
            @Index(name = "idx_refresh_token", columnList = "token")
        })
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Instant expiryDate;

    public boolean isExpired(){
        return expiryDate.isBefore(Instant.now());
    }

    private Instant createdAt;

    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
