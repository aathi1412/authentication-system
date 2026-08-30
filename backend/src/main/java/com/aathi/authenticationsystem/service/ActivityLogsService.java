package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.enums.ActivityCategory;
import com.aathi.authenticationsystem.enums.ActivityType;
import com.aathi.authenticationsystem.models.ActivityLogs;
import com.aathi.authenticationsystem.models.User;
import com.aathi.authenticationsystem.repository.ActivityLogsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogsService {

    private final ActivityLogsRepository activityLogsRepository;
    private final UserService userService;
    private final SortHandlerMethodArgumentResolverCustomizer sortCustomizer;

    public void saveActivityLog(Long userId, String title, ActivityType type, String description, ActivityCategory category){

        User user = userService.getUserById(userId);
        ActivityLogs activityLogs = ActivityLogs.builder()
                .user(user)
                .title(title)
                .type(type)
                .description(description)
                .category(category)
                .build();

        activityLogsRepository.save(activityLogs);
        log.info("ActivityLogs saved successfully");
    }

    public Page<ActivityLogs> getActivities(
            Long userId,
            int page,
            int pageSize,
            String search,
            String category) {

        Pageable pageable = PageRequest.of(
                page,
                pageSize,
                Sort.by("activityTime").descending()
        );

        Page<ActivityLogs> activityLogs = activityLogsRepository
                .findUserActivities(
                    userId,
                    search,
                    category,
                    pageable
        );


    }
}
