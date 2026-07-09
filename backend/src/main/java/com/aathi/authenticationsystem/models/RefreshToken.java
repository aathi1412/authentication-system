package com.aathi.authenticationsystem.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name = "refresh_token",
        indexes = {
            @Index(name = "idx_refresh_token", columnList = "token"),
            @Index(name = "idx_refresh_token_user", columnList = "user_id")
        },
        uniqueConstraints = @UniqueConstraint(name = "UK_refresh_token", columnNames = "token")
)
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
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "FK_refresh_token_user")
    )
    private User user;
}
