package com.openlens.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuizSubmitRequest(
        @NotNull(message = "answers are required")
        @Valid
        List<Answer> answers
) {

    public record Answer(
            int questionIndex,
            int selectedOption
    ) {}
}
