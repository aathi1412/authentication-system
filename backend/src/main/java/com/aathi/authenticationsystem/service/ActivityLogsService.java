package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.enums.ActivityCategory;
import com.aathi.authenticationsystem.models.ActivityLogs;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.repository.ActivityLogsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogsService {

    private final ActivityLogsRepository activityLogsRepository;
    private final UserService userService;

    public void saveActivityLog(Long userId, String title, String description, String category){

        User user = userService.getUserById(userId);
        ActivityLogs activityLogs = ActivityLogs.builder()
                .user(user)
                .title(title)
                .description(description)
                .category(ActivityCategory.AUTHENTICATION)
                .build();

        activityLogsRepository.save(activityLogs);
        log.info("ActivityLogs saved successfully");
    }
}
