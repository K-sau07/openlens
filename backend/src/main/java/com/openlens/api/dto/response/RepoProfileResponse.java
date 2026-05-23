package com.openlens.api.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record RepoProfileResponse(
        Long id,
        String url,
        String owner,
        String name,
        String description,
        String primaryLanguage,
        int stars,
        int forkCount,
        int watchersCount,
        int openIssuesCount,
        String license,
        String defaultBranch,
        List<String> topics,
        Map<String, Long> languages,
        boolean hasWiki,
        boolean hasDiscussions,
        LocalDateTime createdAtGitHub,
        LocalDateTime lastPushedAt,
        String status,
        LocalDateTime lastAnalyzedAt
) {}
