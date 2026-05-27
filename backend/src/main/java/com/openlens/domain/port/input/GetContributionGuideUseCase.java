package com.openlens.domain.port.input;

import java.util.List;

public interface GetContributionGuideUseCase {

    GuideOutput getGuide(Long repoId, Long issueId);

    record GuideOutput(
            RepoInfo repo,
            IssueInfo issue,
            String matchReason,
            String estimatedHours,
            List<StepItem> steps
    ) {}

    record RepoInfo(
            String name,
            String description,
            String language,
            int openIssues,
            int mergedPrs,
            int avgResponseHours,
            boolean ciPassing,
            boolean hasTests
    ) {}

    record IssueInfo(
            Long id,
            int number,
            String title,
            List<String> labels,
            String description
    ) {}

    record StepItem(
            String title,
            String subtitle,
            String body,
            String code,
            String tip,
            String warn,
            List<String> checklist
    ) {}
}
