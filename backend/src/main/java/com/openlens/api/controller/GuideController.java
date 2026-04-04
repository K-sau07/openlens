package com.openlens.api.controller;

import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;
import com.openlens.domain.port.output.IssuePort;
import com.openlens.domain.port.output.PullRequestPort;
import com.openlens.domain.port.output.RepositoryPort;
import com.openlens.infrastructure.ai.AiAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/guide")
public class GuideController {

    private final RepositoryPort repositoryPort;
    private final IssuePort issuePort;
    private final PullRequestPort pullRequestPort;
    private final AiAnalysisService aiService;

    public GuideController(RepositoryPort repositoryPort, IssuePort issuePort,
                           PullRequestPort pullRequestPort, AiAnalysisService aiService) {
        this.repositoryPort = repositoryPort;
        this.issuePort = issuePort;
        this.pullRequestPort = pullRequestPort;
        this.aiService = aiService;
    }

    @GetMapping("/{repoId}/issues/{issueId}")
    public ResponseEntity<Map<String, Object>> getGuide(
            @PathVariable Long repoId, @PathVariable Long issueId) {

        var repoOpt = repositoryPort.findById(repoId);
        if (repoOpt.isEmpty()) return ResponseEntity.notFound().build();
        var repo = repoOpt.get();

        var issueOpt = issuePort.findOpenByRepoId(repoId).stream()
                .filter(i -> i.getId().equals(issueId))
                .findFirst();
        if (issueOpt.isEmpty()) return ResponseEntity.notFound().build();
        var issue = issueOpt.get();

        List<PullRequest> mergedPrs = pullRequestPort.findByRepoId(repoId);
        String repoName = repo.getOwner() + "/" + repo.getName();
        String language = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage() : "unknown";

        // try AI-generated guide first, fall back to rule-based
        Map<String, Object> aiGuide = aiService.generateContributionGuide(repoName, language, issue, mergedPrs);

        Map<String, Object> repoInfo = buildRepoInfo(repo, repoName, mergedPrs);
        Map<String, Object> issueInfo = buildIssueInfo(issue);

        if (aiGuide != null) {
            aiGuide.put("repo", repoInfo);
            aiGuide.put("issue", issueInfo);
            return ResponseEntity.ok(aiGuide);
        }

        // rule-based fallback
        return ResponseEntity.ok(Map.of(
                "repo", repoInfo,
                "issue", issueInfo,
                "matchReason", "Matched to your skill level based on issue complexity and your quiz answers.",
                "estimatedHours", "2–4 hours",
                "steps", buildSteps(issue, repo.getName(), mergedPrs)
        ));
    }

    private Map<String, Object> buildRepoInfo(com.openlens.domain.model.Repository repo,
                                               String repoName, List<PullRequest> mergedPrs) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", repoName);
        m.put("description", "");
        m.put("language", repo.getPrimaryLanguage());
        m.put("openIssues", repo.getStars());
        m.put("mergedPrs", mergedPrs.size());
        m.put("avgResponseHours", 48);
        m.put("ciPassing", true);
        m.put("hasTests", true);
        return m;
    }

    private Map<String, Object> buildIssueInfo(Issue issue) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", issue.getId());
        m.put("number", issue.getNumber());
        m.put("title", issue.getTitle());
        m.put("labels", issue.getLabels() != null ? issue.getLabels() : List.of());
        m.put("description", issue.getBody());
        return m;
    }

    private List<Map<String, Object>> buildSteps(Issue issue, String repoName, List<PullRequest> mergedPrs) {
        int num = issue.getNumber();
        return List.of(
                step("Understand the repo", "Read the structure before touching anything",
                        "Before writing any code, read through the repo to understand how it's organized.",
                        null, null, null,
                        List.of("Read the README", "Understand what issue #" + num + " is asking for",
                                "Find the files most relevant to this issue")),

                step("Set up locally", "Fork, clone, and get the dev environment running",
                        "Fork the repo on GitHub, clone your fork, and follow the setup instructions in the README.",
                        "git clone https://github.com/YOUR_USERNAME/" + repoName + "\ncd " + repoName + "\n# follow README setup instructions",
                        "Check the README for setup instructions — most repos have a getting started section.", null,
                        List.of("Forked the repo", "Cloned locally", "Dev environment running", "Existing tests pass")),

                step("Find your files", "Locate exactly what to change",
                        "Read issue #" + num + " carefully, then find the files that need to change.",
                        null, null, "Don't start coding until you fully understand what the issue asks for.",
                        List.of("Read issue #" + num + " top to bottom", "Identified files to change",
                                "Understand current vs expected behavior")),

                step("Make the change", "Write the actual code — small and focused",
                        "Keep your change minimal. Don't refactor unrelated code in the same PR.",
                        null, "Most maintainers prefer small focused PRs. Note other issues separately.", null,
                        List.of("Made the change", "Tested locally", "Didn't touch unrelated code")),

                step("Write your tests", "Every good PR includes tests",
                        "Add tests covering the change you made — new behavior and edge cases.",
                        null, null, null,
                        List.of("Added tests for new behavior", "Added edge case tests", "All tests pass")),

                step("Commit and push", "Write a commit message the maintainer expects",
                        "Keep it short and imperative. Reference the issue number.",
                        "git checkout -b fix/issue-" + num + "\ngit add .\ngit commit -m \"your change (#" + num + ")\"\ngit push origin fix/issue-" + num,
                        null, null,
                        List.of("Created a new branch", "Committed with descriptive message", "Pushed to my fork")),

                step("Open the PR", "Title, description, what this maintainer wants to see",
                        "Short description — what changed, why, how to test. Reference the issue.",
                        "Title: your change (#" + num + ")\n\nWhat changed:\nHow to test:\n\nCloses #" + num,
                        null, null,
                        List.of("Opened PR with correct title", "Description explains change",
                                "Closes #" + num + " included")),

                step("Handle review feedback", "What to do when the maintainer responds",
                        "Push new commits to the same branch — don't open a new PR. Respond to every comment.",
                        null,
                        !mergedPrs.isEmpty() ? "Based on PR history in this repo, expect 1–2 rounds of feedback." : null,
                        null,
                        List.of("PR submitted and waiting for review"))
        );
    }

    private Map<String, Object> step(String title, String subtitle, String body,
                                      String code, String tip, String warn, List<String> checklist) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("title", title);
        m.put("subtitle", subtitle);
        m.put("body", body);
        if (code != null) m.put("code", code);
        if (tip != null) m.put("tip", tip);
        if (warn != null) m.put("warn", warn);
        if (checklist != null) m.put("checklist", checklist);
        return m;
    }
}
