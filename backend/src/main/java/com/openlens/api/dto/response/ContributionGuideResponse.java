package com.openlens.api.dto.response;

import java.util.List;

public record ContributionGuideResponse(
        RepoInfo repo,
        IssueInfo issue,
        String matchReason,
        String estimatedHours,
        List<Step> steps
) {

    public record RepoInfo(
            String name,
            String description,
            String language,
            int openIssues,
            int mergedPrs,
            int avgResponseHours,
            boolean ciPassing,
            boolean hasTests
    ) {}

    public record IssueInfo(
            Long id,
            int number,
            String title,
            List<String> labels,
            String description
    ) {}

    public record Step(
            String title,
            String subtitle,
            String body,
            String code,
            String tip,
            String warn,
            List<String> checklist
    ) {}
}
