package com.openlens.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AnalyzeRepoRequest(
        @NotBlank(message = "repo URL is required")
        String repoUrl,

        // skill level is optional at this stage — determined after quiz
        String skillLevel
) {}
