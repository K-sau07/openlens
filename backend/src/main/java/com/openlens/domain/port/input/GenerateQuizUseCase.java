package com.openlens.domain.port.input;

import java.util.List;

public interface GenerateQuizUseCase {

    QuizOutput generate(Long repoId);

    record QuizOutput(
            Long repoId,
            List<QuestionItem> questions
    ) {}

    record QuestionItem(
            String context,
            String text,
            String sub,
            List<OptionItem> options
    ) {}

    record OptionItem(
            String title,
            String sub
    ) {}
}
