package com.openlens.api.controller;

import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;
import com.openlens.domain.port.output.IssuePort;
import com.openlens.domain.port.output.PullRequestPort;
import com.openlens.domain.port.output.RepositoryPort;
import com.openlens.infrastructure.ai.AiAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private static final Logger log = LoggerFactory.getLogger(QuizController.class);

    private final RepositoryPort repositoryPort;
    private final IssuePort issuePort;
    private final PullRequestPort pullRequestPort;
    private final AiAnalysisService aiService;

    public QuizController(RepositoryPort repositoryPort, IssuePort issuePort,
                          PullRequestPort pullRequestPort, AiAnalysisService aiService) {
        this.repositoryPort = repositoryPort;
        this.issuePort = issuePort;
        this.pullRequestPort = pullRequestPort;
        this.aiService = aiService;
    }

    @GetMapping("/{repoId}/questions")
    public ResponseEntity<Map<String, Object>> getQuestions(@PathVariable Long repoId) {
        var repo = repositoryPort.findById(repoId);
        String language = repo.map(r -> r.getPrimaryLanguage()).orElse("unknown");
        String repoName = repo.map(r -> r.getOwner() + "/" + r.getName()).orElse("this repo");

        log.info("quiz questions requested — repoId={} language={} repo={}", repoId, language, repoName);

        List<Issue> issues = issuePort.findOpenByRepoId(repoId);
        List<PullRequest> mergedPrs = pullRequestPort.findByRepoId(repoId);

        log.info("loaded {} issues and {} PRs for quiz generation", issues.size(), mergedPrs.size());

        List<Map<String, Object>> questions = aiService.generateQuizQuestions(repoName, language, issues, mergedPrs);
        if (questions == null || questions.isEmpty()) {
            log.warn("AI quiz generation failed or returned empty — falling back to rule-based for repoId={}", repoId);
            questions = buildQuestions(language, repoName);
        } else {
            log.info("using AI-generated questions ({} questions) for repoId={}", questions.size(), repoId);
        }

        return ResponseEntity.ok(Map.of("questions", questions, "repoId", repoId));
    }

    @PostMapping("/{repoId}/submit")
    public ResponseEntity<Map<String, Object>> submit(@PathVariable Long repoId,
                                                       @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> answers = (List<Map<String, Object>>) body.get("answers");

        String skillLevel = scoreAnswers(answers);
        List<Issue> issues = issuePort.findOpenByRepoId(repoId);
        List<Map<String, Object>> matched = matchIssues(issues, skillLevel);

        return ResponseEntity.ok(Map.of("skillLevel", skillLevel, "matchedIssues", matched));
    }

    private String scoreAnswers(List<Map<String, Object>> answers) {
        if (answers == null || answers.isEmpty()) return "beginner";
        double avg = answers.stream()
                .mapToInt(a -> {
                    Object opt = a.get("selectedOption");
                    return opt instanceof Number ? ((Number) opt).intValue() : 0;
                })
                .average().orElse(0);
        if (avg <= 1.0) return "advanced";
        if (avg <= 2.0) return "intermediate";
        return "beginner";
    }

    private List<Map<String, Object>> matchIssues(List<Issue> issues, String skillLevel) {
        List<Issue> filtered = issues.stream()
                .filter(i -> {
                    if ("beginner".equals(skillLevel)) {
                        return i.getLabels() != null &&
                               i.getLabels().stream().anyMatch(l ->
                                   l.toLowerCase().contains("good first") || l.toLowerCase().contains("beginner"));
                    }
                    return true;
                })
                .limit(5).toList();

        if (filtered.isEmpty()) filtered = issues.stream().limit(3).toList();

        return filtered.stream().map(i -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", i.getId());
            m.put("number", i.getNumber());
            m.put("title", i.getTitle());
            m.put("labels", i.getLabels() != null ? i.getLabels() : List.of());
            m.put("complexity", skillLevel);
            m.put("estimatedHours", estimateHours(i));
            m.put("filesCount", 3);
            return m;
        }).toList();
    }

    private String estimateHours(Issue issue) {
        String title = issue.getTitle().toLowerCase();
        if (title.contains("fix") || title.contains("typo") || title.contains("doc")) return "1–2 hours";
        if (title.contains("add") || title.contains("support") || title.contains("improve")) return "2–4 hours";
        return "3–6 hours";
    }

    private List<Map<String, Object>> buildQuestions(String language, String repoName) {
        String lang = language != null && !language.equalsIgnoreCase("unknown") ? language : "the primary language";
        List<Map<String, Object>> qs = new ArrayList<>();

        qs.add(question("Language fit",
                "How comfortable are you reading " + lang + " code you didn't write?",
                "Based on what open issues in " + repoName + " actually require.",
                List.of(
                        option("Very comfortable", "I can follow any unfamiliar " + lang + " code within a few minutes"),
                        option("Mostly comfortable", "I can follow it with some time and docs open"),
                        option("Some experience", "I get there eventually but it takes a while"),
                        option("Still learning", "I struggle with unfamiliar codebases"))));

        qs.add(question("Code reading",
                "You open an unfamiliar file with 150 lines. What's true for you?",
                "Most issues in this repo touch files of this size or larger.",
                List.of(
                        option("I can figure out what it does in a few minutes", "Even without comments I can trace the logic"),
                        option("I can follow it with comments or docs", "I need some anchors to navigate"),
                        option("I need someone to walk me through it", "Large unfamiliar files take me a long time"),
                        option("I look for the parts I need to change", "I search rather than reading the whole file"))));

        qs.add(question("Git & PRs",
                "What's your experience contributing to codebases that aren't yours?",
                "This helps us understand your workflow familiarity.",
                List.of(
                        option("I've opened PRs and had them merged", "Full fork → branch → PR → merge cycle done"),
                        option("I've forked and made changes, never opened a PR", "Done the work but never submitted"),
                        option("I mostly work on my own projects", "Haven't contributed to someone else's codebase yet"),
                        option("Still learning git basics", "Branching and PRs are new to me"))));

        qs.add(question("Testing",
                "When you write code, do you write tests for it?",
                "Merged PRs in " + repoName + " consistently include test coverage.",
                List.of(
                        option("Almost always", "Tests are part of how I work"),
                        option("Sometimes", "When it's required or I have time"),
                        option("Rarely", "I test manually but not with automated tests"),
                        option("I haven't written tests before", "This would be a first"))));

        qs.add(question("PR style",
                "How do you prefer to work when making changes to a codebase?",
                "Understanding your style helps us match you to issues that suit your pace.",
                List.of(
                        option("Small focused changes, one thing at a time", "I'd rather do one thing well"),
                        option("I tend to go broad but can scope down", "I refactor nearby things but can hold back"),
                        option("Whatever it takes to fix the issue", "I'll do what the issue needs"),
                        option("I'm not sure yet — this would be my first PR", "I'll follow whatever the guide recommends"))));

        return qs;
    }

    private Map<String, Object> question(String context, String text, String sub, List<Map<String, Object>> options) {
        return Map.of("context", context, "text", text, "sub", sub, "options", options);
    }

    private Map<String, Object> option(String title, String sub) {
        return Map.of("title", title, "sub", sub);
    }
}
