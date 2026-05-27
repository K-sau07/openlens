package com.openlens.domain.port.input;

import java.util.List;

public interface SubmitQuizUseCase {

    QuizResult submit(Long repoId, List<AnswerInput> answers);

    record AnswerInput(
            int questionIndex,
            int selectedOption
    ) {}

    record QuizResult(
            String skillLevel,
            List<MatchedIssueItem> matchedIssues
    ) {}

    record MatchedIssueItem(
            Long id,
            int number,
            String title,
            List<String> labels,
            String complexity,
            String estimatedHours
    ) {}
}
