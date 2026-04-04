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

        String prPatterns = mergedPrs.stream().limit(10)
                .map(pr -> "- " + pr.getTitle()
                        + (pr.getMergeTimeHours() != null ? " (merged in " + pr.getMergeTimeHours() + "h)" : "")
                        + (pr.getLinkedIssueNumber() != null ? ", closed #" + pr.getLinkedIssueNumber() : ""))
                .reduce("", (a, b) -> a + "\n" + b);

        String issueBody = issue.getBody() != null
                ? issue.getBody().substring(0, Math.min(2000, issue.getBody().length()))
                : "no description";

        String system = """
                You are generating a contribution guide for OpenLens.
                You must respond ONLY with valid JSON. No explanation, no markdown, no backticks.
                The JSON must have: matchReason (string), estimatedHours (string), steps (array).
                Each step must have: title, subtitle, body, and optionally code (string), tip (string), warn (string), checklist (array of strings).
                Generate exactly 8 steps: understand repo, set up locally, find files, make the change, write tests, commit and push, open the PR, handle review.
                
                CRITICAL RULES:
                - Read the issue body thoroughly. It usually contains exact file paths, function names, dependencies to install, and acceptance criteria.
                - Extract every concrete technical detail from the issue: file names, function names, API endpoints, dependencies, config changes needed.
                - The "find files" step must list the actual files mentioned or implied in the issue — not generic placeholders.
                - The "make the change" step must describe the exact code change the issue requires, based on what the issue body says.
                - The "write tests" step must reference the actual test framework used in this repo (check PR titles for hints like vitest, jest, pytest).
                - The commit message must follow the exact format used in this repo's PR history.
                - Never use placeholder text like "your_file.ts" or "relevant file" — use real names from the issue.
                """;

        String user = String.format("""
                Repo: %s
                Language: %s
                
                Issue #%d: %s
                
                Full issue body (read this carefully — it contains the technical requirements):
                %s
                
                Merged PR patterns (use for commit message style and merge time estimates):
                %s
                
                Instructions:
                1. Read the issue body above and extract: exact files to change, what the change should do, any dependencies mentioned, acceptance criteria
                2. Generate a practical 8-step guide where every step references the actual technical details from the issue
                3. Step 3 (find files) must list the real files this issue requires touching based on the issue description
                4. Step 4 (make the change) must describe the exact code change required — not generic advice
                5. matchReason: 1-2 sentences on why a developer at this skill level should attempt this issue
                6. estimatedHours: estimate based on PR merge time patterns above
                
                Return ONLY valid JSON, nothing else.
                """,
                repoName, language,
                issue.getNumber(), issue.getTitle(),
                issueBody,
                prPatterns);

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
