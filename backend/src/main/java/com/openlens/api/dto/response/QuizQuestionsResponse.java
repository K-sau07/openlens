package com.openlens.api.dto.response;

import java.util.List;

public record QuizQuestionsResponse(
        Long repoId,
        List<Question> questions
) {

    public record Question(
            String context,
            String text,
            String sub,
            List<Option> options
    ) {}

    public record Option(
            String title,
            String sub
    ) {}
}
