package com.openlens.api.dto.response;

public record AnalyzeRepoResponse(
        String repoUrl,
        String jobId,
        String status
) {}
