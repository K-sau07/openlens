package com.openlens.application.service;

import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;
import com.openlens.domain.model.Repository;
import com.openlens.domain.model.RepositoryStatus;
import com.openlens.domain.port.input.GenerateQuizUseCase.QuizOutput;
import com.openlens.domain.port.input.SubmitQuizUseCase.AnswerInput;
import com.openlens.domain.port.input.SubmitQuizUseCase.QuizResult;
import com.openlens.domain.port.output.AiGenerationPort;
import com.openlens.domain.port.output.IssuePort;
import com.openlens.domain.port.output.PullRequestPort;
import com.openlens.domain.port.output.RepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock private RepositoryPort repositoryPort;
    @Mock private IssuePort issuePort;
    @Mock private PullRequestPort pullRequestPort;
    @Mock private AiGenerationPort aiGenerationPort;

    @InjectMocks private QuizService quizService;

    private Repository testRepo;

    @BeforeEach
    void setUp() {
        testRepo = Repository.builder()
                .id(1L).url("https://github.com/test/repo")
                .owner("test").name("repo")
                .primaryLanguage("Java").stars(100)
                .status(RepositoryStatus.READY)
                .build();
    }

    @Test
    void generate_returnsFallbackQuestionsWhenAiUnavailable() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(testRepo));
        when(issuePort.findOpenByRepoId(1L)).thenReturn(List.of());
        when(pullRequestPort.findByRepoId(1L)).thenReturn(List.of());
        when(aiGenerationPort.generateQuizQuestions(any(), any(), any(), any())).thenReturn(null);

        QuizOutput result = quizService.generate(1L);

        assertEquals(1L, result.repoId());
        assertEquals(5, result.questions().size());
        assertTrue(result.questions().get(0).text().contains("Java"));
    }

    @Test
    void generate_throwsWhenRepoNotFound() {
        when(repositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(com.openlens.domain.exception.ResourceNotFoundException.class,
                () -> quizService.generate(99L));
    }

    @Test
    void submit_returnsAdvancedForLowAverageAnswers() {
        when(issuePort.findOpenByRepoId(1L)).thenReturn(List.of());

        List<AnswerInput> answers = List.of(
                new AnswerInput(0, 0),
                new AnswerInput(1, 0),
                new AnswerInput(2, 1),
                new AnswerInput(3, 0),
                new AnswerInput(4, 1)
        );

        QuizResult result = quizService.submit(1L, answers);
        assertEquals("advanced", result.skillLevel());
    }

    @Test
    void submit_returnsIntermediateForMidAverageAnswers() {
        when(issuePort.findOpenByRepoId(1L)).thenReturn(List.of());

        List<AnswerInput> answers = List.of(
                new AnswerInput(0, 1),
                new AnswerInput(1, 2),
                new AnswerInput(2, 1),
                new AnswerInput(3, 2),
                new AnswerInput(4, 1)
        );

        QuizResult result = quizService.submit(1L, answers);
        assertEquals("intermediate", result.skillLevel());
    }

    @Test
    void submit_returnsBeginnerForHighAverageAnswers() {
        when(issuePort.findOpenByRepoId(1L)).thenReturn(List.of());

        List<AnswerInput> answers = List.of(
                new AnswerInput(0, 3),
                new AnswerInput(1, 3),
                new AnswerInput(2, 2),
                new AnswerInput(3, 3),
                new AnswerInput(4, 3)
        );

        QuizResult result = quizService.submit(1L, answers);
        assertEquals("beginner", result.skillLevel());
    }

    @Test
    void submit_returnsBeginnerForEmptyAnswers() {
        when(issuePort.findOpenByRepoId(1L)).thenReturn(List.of());

        QuizResult result = quizService.submit(1L, List.of());
        assertEquals("beginner", result.skillLevel());
    }

    @Test
    void submit_matchesBeginnerFriendlyIssues() {
        Issue goodFirst = Issue.builder()
                .id(10L).repoId(1L).number(1).title("add readme")
                .labels(List.of("good first issue")).state("open")
                .build();
        Issue advanced = Issue.builder()
                .id(11L).repoId(1L).number(2).title("refactor core")
                .labels(List.of("enhancement")).state("open")
                .build();

        when(issuePort.findOpenByRepoId(1L)).thenReturn(List.of(goodFirst, advanced));

        List<AnswerInput> beginnerAnswers = List.of(
                new AnswerInput(0, 3),
                new AnswerInput(1, 3),
                new AnswerInput(2, 3),
                new AnswerInput(3, 3),
                new AnswerInput(4, 3)
        );

        QuizResult result = quizService.submit(1L, beginnerAnswers);
        assertEquals("beginner", result.skillLevel());
        assertTrue(result.matchedIssues().stream()
                .anyMatch(i -> i.title().equals("add readme")));
    }

    @Test
    void submit_estimatesHoursBasedOnTitle() {
        Issue fixIssue = Issue.builder()
                .id(10L).repoId(1L).number(1).title("fix null pointer in parser")
                .labels(List.of("bug")).state("open")
                .build();
        Issue addIssue = Issue.builder()
                .id(11L).repoId(1L).number(2).title("add support for yaml config")
                .labels(List.of("enhancement")).state("open")
                .build();
        Issue bigIssue = Issue.builder()
                .id(12L).repoId(1L).number(3).title("redesign authentication flow")
                .labels(List.of("enhancement")).state("open")
                .build();

        when(issuePort.findOpenByRepoId(1L)).thenReturn(List.of(fixIssue, addIssue, bigIssue));

        List<AnswerInput> answers = List.of(
                new AnswerInput(0, 0),
                new AnswerInput(1, 0),
                new AnswerInput(2, 0),
                new AnswerInput(3, 0),
                new AnswerInput(4, 0)
        );

        QuizResult result = quizService.submit(1L, answers);

        assertEquals("1-2 hours", result.matchedIssues().stream()
                .filter(i -> i.title().equals("fix null pointer in parser"))
                .findFirst().get().estimatedHours());
        assertEquals("2-4 hours", result.matchedIssues().stream()
                .filter(i -> i.title().equals("add support for yaml config"))
                .findFirst().get().estimatedHours());
        assertEquals("3-6 hours", result.matchedIssues().stream()
                .filter(i -> i.title().equals("redesign authentication flow"))
                .findFirst().get().estimatedHours());
    }
}
