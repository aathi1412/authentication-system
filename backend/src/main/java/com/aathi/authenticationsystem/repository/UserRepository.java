package com.aathi.authenticationsystem.repository;

import com.aathi.authenticationsystem.enums.Role;
import com.aathi.authenticationsystem.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    long countByEnabledTrue();
    long countByAccountLockedTrue();
    long countByCreatedAtAfter(Instant after);
    long countByRole(Role role);

    @Query("""
        SELECT u FROM User u
        WHERE (:search IS NULL OR 
               LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
               LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:role IS NULL OR u.role = :role)
        AND (:accountLocked IS NULL OR u.accountLocked = :accountLocked)
        AND (:enabled IS NULL OR u.enabled = :enabled)
    """)
    Page<User> findFilteredUsers(
            @Param("search") String search,
            @Param("role") Role role,
            @Param("accountLocked") Boolean accountLocked,
            @Param("enabled") Boolean enabled,
            Pageable pageable
    );
}
