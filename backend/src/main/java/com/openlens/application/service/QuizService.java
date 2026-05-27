package com.openlens.application.service;

import com.openlens.domain.exception.ResourceNotFoundException;
import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;
import com.openlens.domain.model.Repository;
import com.openlens.domain.port.input.GenerateQuizUseCase;
import com.openlens.domain.port.input.SubmitQuizUseCase;
import com.openlens.domain.port.output.AiGenerationPort;
import com.openlens.domain.port.output.IssuePort;
import com.openlens.domain.port.output.PullRequestPort;
import com.openlens.domain.port.output.RepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QuizService implements GenerateQuizUseCase, SubmitQuizUseCase {

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    private final RepositoryPort repositoryPort;
    private final IssuePort issuePort;
    private final PullRequestPort pullRequestPort;
    private final AiGenerationPort aiGenerationPort;

    public QuizService(RepositoryPort repositoryPort, IssuePort issuePort,
                       PullRequestPort pullRequestPort, AiGenerationPort aiGenerationPort) {
        this.repositoryPort = repositoryPort;
        this.issuePort = issuePort;
        this.pullRequestPort = pullRequestPort;
        this.aiGenerationPort = aiGenerationPort;
    }

    @Override
    public QuizOutput generate(Long repoId) {
        Repository repo = repositoryPort.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("repository not found: " + repoId));

        String language = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage() : "unknown";
        String repoName = repo.getOwner() + "/" + repo.getName();

        log.info("generating quiz for repo={} language={}", repoName, language);

        List<Issue> issues = issuePort.findOpenByRepoId(repoId);
        List<PullRequest> mergedPrs = pullRequestPort.findByRepoId(repoId);

        List<QuestionItem> questions = tryAiQuestions(repoName, language, issues, mergedPrs);
        if (questions == null || questions.isEmpty()) {
            log.info("AI unavailable, using rule-based questions for repo={}", repoName);
            questions = buildFallbackQuestions(language, repoName);
        }

        return new QuizOutput(repoId, questions);
    }

    @Override
    public QuizResult submit(Long repoId, List<AnswerInput> answers) {
        String skillLevel = scoreAnswers(answers);
        List<Issue> issues = issuePort.findOpenByRepoId(repoId);
        List<MatchedIssueItem> matched = matchIssues(issues, skillLevel);

        log.info("quiz submitted for repo={} — scored as {} with {} matched issues",
                repoId, skillLevel, matched.size());

        return new QuizResult(skillLevel, matched);
    }

    private String scoreAnswers(List<AnswerInput> answers) {
        if (answers == null || answers.isEmpty()) return "beginner";

        double avg = answers.stream()
                .mapToInt(AnswerInput::selectedOption)
                .average()
                .orElse(0);

        if (avg <= 1.0) return "advanced";
        if (avg <= 2.0) return "intermediate";
        return "beginner";
    }

    private List<MatchedIssueItem> matchIssues(List<Issue> issues, String skillLevel) {
        List<Issue> filtered = issues.stream()
                .filter(i -> matchesSkillLevel(i, skillLevel))
                .limit(5)
                .toList();

        if (filtered.isEmpty()) {
            filtered = issues.stream().limit(3).toList();
        }

        return filtered.stream()
                .map(i -> new MatchedIssueItem(
                        i.getId(),
                        i.getNumber(),
                        i.getTitle(),
                        i.getLabels() != null ? i.getLabels() : List.of(),
                        skillLevel,
                        estimateHours(i)
                ))
                .toList();
    }

    private boolean matchesSkillLevel(Issue issue, String skillLevel) {
        if (!"beginner".equals(skillLevel)) return true;
        if (issue.getLabels() == null) return false;
        return issue.getLabels().stream()
                .anyMatch(l -> l.toLowerCase().contains("good first")
                        || l.toLowerCase().contains("beginner"));
    }

    private String estimateHours(Issue issue) {
        String title = issue.getTitle().toLowerCase();
        if (title.contains("fix") || title.contains("typo") || title.contains("doc")) return "1-2 hours";
        if (title.contains("add") || title.contains("support") || title.contains("improve")) return "2-4 hours";
        return "3-6 hours";
    }

    @SuppressWarnings("unchecked")
    private List<QuestionItem> tryAiQuestions(String repoName, String language,
                                              List<Issue> issues, List<PullRequest> mergedPrs) {
        List<Map<String, Object>> raw = aiGenerationPort.generateQuizQuestions(
                repoName, language, issues, mergedPrs);

        if (raw == null || raw.isEmpty()) return null;

        try {
            return raw.stream().map(q -> {
                List<Map<String, Object>> opts = (List<Map<String, Object>>) q.get("options");
                List<OptionItem> options = opts.stream()
                        .map(o -> new OptionItem(
                                (String) o.get("title"),
                                (String) o.get("sub")
                        ))
                        .toList();
                return new QuestionItem(
                        (String) q.get("context"),
                        (String) q.get("text"),
                        (String) q.get("sub"),
                        options
                );
            }).toList();
        } catch (Exception e) {
            log.warn("failed to map AI questions to typed response — {}", e.getMessage());
            return null;
        }
    }

    private List<QuestionItem> buildFallbackQuestions(String language, String repoName) {
        String lang = language != null && !language.equalsIgnoreCase("unknown")
                ? language : "the primary language";

        List<QuestionItem> questions = new ArrayList<>();

        questions.add(new QuestionItem("Language fit",
                "How comfortable are you reading " + lang + " code you didn't write?",
                "Based on what open issues in " + repoName + " actually require.",
                List.of(
                        new OptionItem("Very comfortable", "I can follow any unfamiliar " + lang + " code within a few minutes"),
                        new OptionItem("Mostly comfortable", "I can follow it with some time and docs open"),
                        new OptionItem("Some experience", "I get there eventually but it takes a while"),
                        new OptionItem("Still learning", "I struggle with unfamiliar codebases"))));

        questions.add(new QuestionItem("Code reading",
                "You open an unfamiliar file with 150 lines. What's true for you?",
                "Most issues in this repo touch files of this size or larger.",
                List.of(
                        new OptionItem("I can figure out what it does in a few minutes", "Even without comments I can trace the logic"),
                        new OptionItem("I can follow it with comments or docs", "I need some anchors to navigate"),
                        new OptionItem("I need someone to walk me through it", "Large unfamiliar files take me a long time"),
                        new OptionItem("I look for the parts I need to change", "I search rather than reading the whole file"))));

        questions.add(new QuestionItem("Git and PRs",
                "What's your experience contributing to codebases that aren't yours?",
                "This helps us understand your workflow familiarity.",
                List.of(
                        new OptionItem("I've opened PRs and had them merged", "Full fork, branch, PR, merge cycle done"),
                        new OptionItem("I've forked and made changes, never opened a PR", "Done the work but never submitted"),
                        new OptionItem("I mostly work on my own projects", "Haven't contributed to someone else's codebase yet"),
                        new OptionItem("Still learning git basics", "Branching and PRs are new to me"))));

        questions.add(new QuestionItem("Testing",
                "When you write code, do you write tests for it?",
                "Merged PRs in " + repoName + " consistently include test coverage.",
                List.of(
                        new OptionItem("Almost always", "Tests are part of how I work"),
                        new OptionItem("Sometimes", "When it's required or I have time"),
                        new OptionItem("Rarely", "I test manually but not with automated tests"),
                        new OptionItem("I haven't written tests before", "This would be a first"))));

        questions.add(new QuestionItem("PR style",
                "How do you prefer to work when making changes to a codebase?",
                "Understanding your style helps us match you to issues that suit your pace.",
                List.of(
                        new OptionItem("Small focused changes, one thing at a time", "I'd rather do one thing well"),
                        new OptionItem("I tend to go broad but can scope down", "I refactor nearby things but can hold back"),
                        new OptionItem("Whatever it takes to fix the issue", "I'll do what the issue needs"),
                        new OptionItem("I'm not sure yet, this would be my first PR", "I'll follow whatever the guide recommends"))));

        return questions;
    }
}
