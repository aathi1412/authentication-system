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

    @SuppressWarnings("JpaDataSourceORMInspection")
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
