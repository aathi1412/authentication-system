package com.aathi.authenticationsystem.repository;

import com.aathi.authenticationsystem.enums.ActivityCategory;
import com.aathi.authenticationsystem.models.ActivityLogs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, Long> {

    @Query("""
        SELECT a FROM ActivityLogs a
        WHERE a.user.id = :userId
        AND (:search IS NULL OR 
             LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) 
             OR LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:category IS NULL OR a.category = :category) 
    """)
    Page<ActivityLogs> findUserActivities(
            @Param("userId") Long userId,
            @Param("search") String search,
            @Param("category") ActivityCategory category,
            Pageable pageable
    );
}
