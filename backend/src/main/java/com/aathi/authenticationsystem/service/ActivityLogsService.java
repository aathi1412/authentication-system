package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.dto.user.ActivityLogResponse;
import com.aathi.authenticationsystem.dto.user.PageResponse;
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
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogsService {

    private final ActivityLogsRepository activityLogsRepository;

    public void saveActivityLog(User user, String title, ActivityType type, String description, ActivityCategory category){

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

    public PageResponse<ActivityLogResponse> getActivities(
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

        Page<ActivityLogResponse> response = activityLogs.map(this::mapToActivityLogResponse);

        return PageResponse
                .<ActivityLogResponse>builder()
                .content(response.getContent())
                .page(response.getNumber())
                .pageSize(response.getSize())
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .build();
    }

    public ActivityLogResponse mapToActivityLogResponse(ActivityLogs activityLogs){
        return ActivityLogResponse.builder()
                .type(activityLogs.getType())
                .title(activityLogs.getTitle())
                .description(activityLogs.getDescription())
                .category(activityLogs.getCategory())
                .ActivityTime(activityLogs.getActivityTime())
                .build();
    }
}
