package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.models.ActivityLogs;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.repository.ActivityLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogsService {

    private final ActivityLogsRepository activityLogsRepository;

    public void saveActivityLog(User user, String title, String description, String type, String category){

        ActivityLogs activityLogs = ActivityLogs.builder()
                .user(user)
                .type(type)
                .title(title)
                .description(description)
                .category(category)
                .build();

        activityLogsRepository.save(activityLogs);
    }
}
