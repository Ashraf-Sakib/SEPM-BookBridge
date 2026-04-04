package com.bookbridge.BookBridge.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLifecycleCleanupJob {

    private final UserService userService;

    // Runs hourly and removes non-admin accounts past the scheduled deletion timestamp.
    @Scheduled(cron = "0 0 * * * *")
    public void purgeScheduledUsers() {
        int deleted = userService.purgeUsersScheduledForDeletion(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Purged {} users that passed the 14-day scheduled deletion window", deleted);
        }
    }
}

