package com.openlens.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AnalyzeRepoRequest(
        @NotBlank(message = "repo URL is required")
        String repoUrl,

        @NotBlank(message = "skill level is required")
        @Pattern(regexp = "(?i)BEGINNER|INTERMEDIATE|ADVANCED", message = "skill level must be BEGINNER, INTERMEDIATE, or ADVANCED")
        String skillLevel
) {}
