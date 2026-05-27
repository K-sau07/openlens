package com.openlens.api.dto.response;

import java.util.List;

public record QuizResultResponse(
        String skillLevel,
        List<MatchedIssue> matchedIssues
) {

    public record MatchedIssue(
            Long id,
            int number,
            String title,
            List<String> labels,
            String complexity,
            String estimatedHours
    ) {}
}
