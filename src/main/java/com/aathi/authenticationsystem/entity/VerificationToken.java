package com.aathi.authenticationsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "verification_token",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_verification_token", columnNames = "token"),
                @UniqueConstraint(name = "UK_verification_token_user", columnNames = "user_id")
        }
)
public class VerificationToken {
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

    @Column(nullable = false)
    private Instant createdAt;


    @OneToOne
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "FK_verification_token_user")
    )
    private User user;
}
