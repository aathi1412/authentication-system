package com.aathi.authenticationsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@SuppressWarnings("ALL")
@Builder
@Getter
@Setter
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

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    public boolean isExpired(){
        return expiryDate.isBefore(Instant.now());
    }

    private Instant createdAt;

    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
