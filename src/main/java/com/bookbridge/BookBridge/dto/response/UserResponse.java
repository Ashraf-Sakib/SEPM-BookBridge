package com.bookbridge.BookBridge.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private boolean enabled;
    private Set<String> roles;
    private LocalDateTime disabledAt;
    private LocalDateTime deletionScheduledAt;

    public UserResponse(Integer id, String username, String email, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
    }
}