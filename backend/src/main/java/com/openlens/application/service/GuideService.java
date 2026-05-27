package com.openlens.application.service;

import com.openlens.domain.exception.ResourceNotFoundException;
import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;
import com.openlens.domain.model.Repository;
import com.openlens.domain.port.input.GetContributionGuideUseCase;
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
public class GuideService implements GetContributionGuideUseCase {

    private static final Logger log = LoggerFactory.getLogger(GuideService.class);

    private final RepositoryPort repositoryPort;
    private final IssuePort issuePort;
    private final PullRequestPort pullRequestPort;
    private final AiGenerationPort aiGenerationPort;

    public GuideService(RepositoryPort repositoryPort, IssuePort issuePort,
                        PullRequestPort pullRequestPort, AiGenerationPort aiGenerationPort) {
        this.repositoryPort = repositoryPort;
        this.issuePort = issuePort;
        this.pullRequestPort = pullRequestPort;
        this.aiGenerationPort = aiGenerationPort;
    }

    @Override
    public GuideOutput getGuide(Long repoId, Long issueId) {
        Repository repo = repositoryPort.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("repository not found: " + repoId));

        Issue issue = issuePort.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("issue not found: " + issueId));

        List<PullRequest> mergedPrs = pullRequestPort.findByRepoId(repoId);
        String repoName = repo.getOwner() + "/" + repo.getName();
        String language = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage() : "unknown";

        RepoInfo repoInfo = new RepoInfo(
                repoName,
                repo.getDescription() != null ? repo.getDescription() : "",
                language,
                repo.getOpenIssuesCount(),
                mergedPrs.size()
        );

        IssueInfo issueInfo = new IssueInfo(
                issue.getId(),
                issue.getNumber(),
                issue.getTitle(),
                issue.getLabels() != null ? issue.getLabels() : List.of(),
                issue.getBody()
        );

        GuideOutput aiGuide = tryAiGuide(repoName, language, issue, mergedPrs, repoInfo, issueInfo);
        if (aiGuide != null) {
            return aiGuide;
        }

        log.info("AI unavailable, using rule-based guide for repo={} issue=#{}", repoName, issue.getNumber());
        return new GuideOutput(
                repoInfo,
                issueInfo,
                "Matched to your skill level based on issue complexity and your quiz answers.",
                "2-4 hours",
                buildFallbackSteps(issue, repo.getName(), mergedPrs)
        );
    }

    @SuppressWarnings("unchecked")
    private GuideOutput tryAiGuide(String repoName, String language, Issue issue,
                                    List<PullRequest> mergedPrs, RepoInfo repoInfo, IssueInfo issueInfo) {
        Map<String, Object> raw = aiGenerationPort.generateContributionGuide(
                repoName, language, issue, mergedPrs);

        if (raw == null) return null;

        try {
            String matchReason = (String) raw.getOrDefault("matchReason", "");
            String estimatedHours = (String) raw.getOrDefault("estimatedHours", "2-4 hours");

            List<Map<String, Object>> rawSteps = (List<Map<String, Object>>) raw.getOrDefault("steps", List.of());
            List<StepItem> steps = rawSteps.stream()
                    .map(s -> new StepItem(
                            (String) s.get("title"),
                            (String) s.get("subtitle"),
                            (String) s.get("body"),
                            (String) s.getOrDefault("code", null),
                            (String) s.getOrDefault("tip", null),
                            (String) s.getOrDefault("warn", null),
                            s.containsKey("checklist") ? (List<String>) s.get("checklist") : null
                    ))
                    .toList();

            log.info("parsed AI guide with {} steps for issue #{}", steps.size(), issue.getNumber());
            return new GuideOutput(repoInfo, issueInfo, matchReason, estimatedHours, steps);
        } catch (Exception e) {
            log.warn("failed to map AI guide to typed response — {}", e.getMessage());
            return null;
        }
    }

    private List<StepItem> buildFallbackSteps(Issue issue, String repoName, List<PullRequest> mergedPrs) {
        int num = issue.getNumber();
        List<StepItem> steps = new ArrayList<>();

        steps.add(new StepItem(
                "Understand the repo",
                "Read the structure before touching anything",
                "Before writing any code, read through the repo to understand how it's organized.",
                null, null, null,
                List.of("Read the README",
                        "Understand what issue #" + num + " is asking for",
                        "Find the files most relevant to this issue")));

        steps.add(new StepItem(
                "Set up locally",
                "Fork, clone, and get the dev environment running",
                "Fork the repo on GitHub, clone your fork, and follow the setup instructions in the README.",
                "git clone https://github.com/YOUR_USERNAME/" + repoName + "\ncd " + repoName + "\n# follow README setup instructions",
                "Check the README for setup instructions — most repos have a getting started section.",
                null,
                List.of("Forked the repo", "Cloned locally", "Dev environment running", "Existing tests pass")));

        steps.add(new StepItem(
                "Find your files",
                "Locate exactly what to change",
                "Read issue #" + num + " carefully, then find the files that need to change.",
                null, null,
                "Don't start coding until you fully understand what the issue asks for.",
                List.of("Read issue #" + num + " top to bottom",
                        "Identified files to change",
                        "Understand current vs expected behavior")));

        steps.add(new StepItem(
                "Make the change",
                "Write the actual code — small and focused",
                "Keep your change minimal. Don't refactor unrelated code in the same PR.",
                null,
                "Most maintainers prefer small focused PRs. Note other issues separately.",
                null,
                List.of("Made the change", "Tested locally", "Didn't touch unrelated code")));

        steps.add(new StepItem(
                "Write your tests",
                "Every good PR includes tests",
                "Add tests covering the change you made — new behavior and edge cases.",
                null, null, null,
                List.of("Added tests for new behavior", "Added edge case tests", "All tests pass")));

        steps.add(new StepItem(
                "Commit and push",
                "Write a commit message the maintainer expects",
                "Keep it short and imperative. Reference the issue number.",
                "git checkout -b fix/issue-" + num + "\ngit add .\ngit commit -m \"your change (#" + num + ")\"\ngit push origin fix/issue-" + num,
                null, null,
                List.of("Created a new branch", "Committed with descriptive message", "Pushed to my fork")));

        steps.add(new StepItem(
                "Open the PR",
                "Title, description, what this maintainer wants to see",
                "Short description — what changed, why, how to test. Reference the issue.",
                "Title: your change (#" + num + ")\n\nWhat changed:\nHow to test:\n\nCloses #" + num,
                null, null,
                List.of("Opened PR with correct title", "Description explains change",
                        "Closes #" + num + " included")));

        steps.add(new StepItem(
                "Handle review feedback",
                "What to do when the maintainer responds",
                "Push new commits to the same branch — don't open a new PR. Respond to every comment.",
                null,
                !mergedPrs.isEmpty() ? "Based on PR history in this repo, expect 1-2 rounds of feedback." : null,
                null,
                List.of("PR submitted and waiting for review")));

        return steps;
    }
}
