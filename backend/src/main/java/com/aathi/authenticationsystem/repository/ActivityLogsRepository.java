package com.aathi.authenticationsystem.repository;

import com.aathi.authenticationsystem.models.ActivityLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, Long> {
}
