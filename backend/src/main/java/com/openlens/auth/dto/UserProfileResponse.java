package com.openlens.auth.dto;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String email,
        String name,
        LocalDateTime createdAt
) {}
