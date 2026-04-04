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
                .map(i -> "- #" + i.getNumber() + ": " + i.getTitle())
                .reduce("", (a, b) -> a + "\n" + b);

        String prList = mergedPrs.stream().limit(8)
                .map(pr -> "- PR #" + pr.getNumber() + ": " + pr.getTitle()
                        + (pr.getMergeTimeHours() != null ? " (merged in " + pr.getMergeTimeHours() + "h)" : ""))
                .reduce("", (a, b) -> a + "\n" + b);

        String system = """
                You are generating quiz questions for OpenLens, a tool that helps developers make their first open source PR.
                You must respond ONLY with a valid JSON array. No explanation, no markdown, no backticks.
                Each question must have: context (string), text (string), sub (string), options (array of {title, sub}).
                Generate exactly 5 questions that assess whether a developer is ready to contribute to this specific repo.
                Focus on: language familiarity, code reading ability, git/PR experience, testing habits, and one repo-specific question.
                """;

        String user = String.format("""
                Repo: %s
                Primary language: %s
                
                Sample open issues:
                %s
                
                Recent merged PRs:
                %s
                
                Generate 5 quiz questions to assess if a developer can contribute to this repo.
                Each question should have 4 options ordered from most to least experienced.
                The last question must be specific to this repo's language/stack.
                Return ONLY a JSON array.
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
