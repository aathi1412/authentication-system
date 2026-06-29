package com.aathi.authenticationsystem.repository;

import com.aathi.authenticationsystem.entity.RefreshToken;
import com.aathi.authenticationsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

}
