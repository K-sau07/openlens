package com.openlens.infrastructure.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AiAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AiAnalysisService.class);

    private final ClaudeApiClient claude;
    private final ObjectMapper objectMapper;

    public AiAnalysisService(ClaudeApiClient claude, ObjectMapper objectMapper) {
        this.claude = claude;
        this.objectMapper = objectMapper;
    }

    // generates 5 quiz questions tailored to this specific repo
    public List<Map<String, Object>> generateQuizQuestions(
            String repoName, String language, List<Issue> issues, List<PullRequest> mergedPrs) {

        if (!claude.isAvailable()) return null;

        String issueList = issues.stream().limit(10)
                .map(i -> "- #" + i.getNumber() + ": " + i.getTitle()
                        + (i.getLabels() != null && !i.getLabels().isEmpty() ? " [" + String.join(", ", i.getLabels()) + "]" : ""))
                .reduce("", (a, b) -> a + "\n" + b);

        String prList = mergedPrs.stream().limit(12)
                .map(pr -> "- " + pr.getTitle()
                        + (pr.getMergeTimeHours() != null ? " (merged in " + pr.getMergeTimeHours() + "h)" : ""))
                .reduce("", (a, b) -> a + "\n" + b);

        String system = """
                You are generating quiz questions for OpenLens, a tool that helps developers make their first open source PR.
                You must respond ONLY with a valid JSON array. No explanation, no markdown, no backticks.
                Each question object must have exactly: context (string), text (string), sub (string), options (array of objects with title and sub).
                Generate exactly 5 questions assessing whether a developer can contribute to this specific repo.
                
                Question requirements:
                1. Language comfort — ask specifically about the repo's primary language (e.g. TypeScript, Python, Go), not generic "programming"
                2. Framework/tooling — ask about specific frameworks or tools visible in the PR titles and issue labels (e.g. React, Vitest, FastAPI, Docker)
                3. Code reading — how comfortable reading unfamiliar codebases of this language
                4. Git and PR workflow — experience opening PRs, writing tests
                5. Commit style — based on PR title patterns in this repo (e.g. feat:, fix:, conventional commits)
                
                Each question must have exactly 4 options ordered from most to least experienced.
                Make every question feel tailored to THIS specific repo — use the actual language name, actual framework names, actual patterns you see.
                """;

        String user = String.format("""
                Repo: %s
                Primary language: %s
                
                Open issues (with labels):
                %s
                
                Recent merged PRs (analyze these for tech stack, commit style, and framework patterns):
                %s
                
                Based on the PR titles and issue labels above, identify:
                - What frameworks/libraries are used (e.g. React, Vitest, FastAPI, etc.)
                - What commit message style is used (e.g. conventional commits with feat:/fix:)
                - What areas the codebase covers (frontend, backend, testing, etc.)
                
                Then generate 5 quiz questions that are SPECIFIC to this repo's stack.
                Return ONLY a valid JSON array, nothing else.
                """, repoName, language, issueList, prList);

        try {
            String response = claude.complete(system, user);
            if (response == null) return null;
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("failed to parse quiz questions from Claude response", e);
            return null;
        }
    }

    // generates a full contribution guide for a specific issue
    public Map<String, Object> generateContributionGuide(
            String repoName, String language, Issue issue, List<PullRequest> mergedPrs) {

        if (!claude.isAvailable()) return null;

        String prPatterns = mergedPrs.stream().limit(5)
                .map(pr -> "- PR #" + pr.getNumber() + ": " + pr.getTitle()
                        + (pr.getMergeTimeHours() != null ? ", merged in " + pr.getMergeTimeHours() + "h" : "")
                        + (pr.getLinkedIssueNumber() != null ? ", closed #" + pr.getLinkedIssueNumber() : ""))
                .reduce("", (a, b) -> a + "\n" + b);

        String system = """
                You are generating a contribution guide for OpenLens.
                You must respond ONLY with valid JSON. No explanation, no markdown, no backticks.
                The JSON must have: matchReason (string), estimatedHours (string), steps (array).
                Each step must have: title, subtitle, body, and optionally code (string), tip (string), warn (string), checklist (array of strings).
                Generate exactly 8 steps: understand repo, set up locally, find files, make the change, write tests, commit and push, open the PR, handle review.
                Make the content specific to the issue and repo — include real file path guesses, commit message format, PR title format.
                """;

        String user = String.format("""
                Repo: %s
                Language: %s
                
                Issue #%d: %s
                Issue body: %s
                
                Merged PR patterns in this repo:
                %s
                
                Generate a complete 8-step contribution guide for issue #%d.
                Make it practical and specific — include likely file paths, exact commit message format, PR description template.
                The matchReason should explain in 1-2 sentences why this issue is a good fit.
                estimatedHours should be like "2-4 hours" based on similar PRs.
                Return ONLY valid JSON.
                """,
                repoName, language,
                issue.getNumber(), issue.getTitle(),
                issue.getBody() != null ? issue.getBody().substring(0, Math.min(500, issue.getBody().length())) : "no description",
                prPatterns,
                issue.getNumber());

        try {
            String response = claude.complete(system, user);
            if (response == null) return null;
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("failed to parse contribution guide from Claude response", e);
            return null;
        }
    }
}
