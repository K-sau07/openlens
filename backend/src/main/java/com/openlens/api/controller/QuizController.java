package com.openlens.api.controller;

import com.openlens.api.dto.request.QuizSubmitRequest;
import com.openlens.api.dto.response.QuizQuestionsResponse;
import com.openlens.api.dto.response.QuizResultResponse;
import com.openlens.domain.port.input.GenerateQuizUseCase;
import com.openlens.domain.port.input.SubmitQuizUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final GenerateQuizUseCase generateQuizUseCase;
    private final SubmitQuizUseCase submitQuizUseCase;

    public QuizController(GenerateQuizUseCase generateQuizUseCase,
                          SubmitQuizUseCase submitQuizUseCase) {
        this.generateQuizUseCase = generateQuizUseCase;
        this.submitQuizUseCase = submitQuizUseCase;
    }

    @GetMapping("/{repoId}/questions")
    public ResponseEntity<QuizQuestionsResponse> getQuestions(@PathVariable Long repoId) {
        GenerateQuizUseCase.QuizOutput output = generateQuizUseCase.generate(repoId);
        return ResponseEntity.ok(toQuestionsResponse(output));
    }

    @PostMapping("/{repoId}/submit")
    public ResponseEntity<QuizResultResponse> submit(@PathVariable Long repoId,
                                                      @Valid @RequestBody QuizSubmitRequest request) {
        List<SubmitQuizUseCase.AnswerInput> answers = request.answers().stream()
                .map(a -> new SubmitQuizUseCase.AnswerInput(a.questionIndex(), a.selectedOption()))
                .toList();

        SubmitQuizUseCase.QuizResult result = submitQuizUseCase.submit(repoId, answers);
        return ResponseEntity.ok(toResultResponse(result));
    }

    private QuizQuestionsResponse toQuestionsResponse(GenerateQuizUseCase.QuizOutput output) {
        List<QuizQuestionsResponse.Question> questions = output.questions().stream()
                .map(q -> new QuizQuestionsResponse.Question(
                        q.context(), q.text(), q.sub(),
                        q.options().stream()
                                .map(o -> new QuizQuestionsResponse.Option(o.title(), o.sub()))
                                .toList()
                ))
                .toList();
        return new QuizQuestionsResponse(output.repoId(), questions);
    }

    private QuizResultResponse toResultResponse(SubmitQuizUseCase.QuizResult result) {
        List<QuizResultResponse.MatchedIssue> issues = result.matchedIssues().stream()
                .map(i -> new QuizResultResponse.MatchedIssue(
                        i.id(), i.number(), i.title(), i.labels(),
                        i.complexity(), i.estimatedHours()
                ))
                .toList();
        return new QuizResultResponse(result.skillLevel(), issues);
    }
}
