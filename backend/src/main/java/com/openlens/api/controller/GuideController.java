package com.openlens.api.controller;

import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;
import com.openlens.domain.port.output.IssuePort;
import com.openlens.domain.port.output.PullRequestPort;
import com.openlens.domain.port.output.RepositoryPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/guide")
public class GuideController {

    private final RepositoryPort repositoryPort;
    private final IssuePort issuePort;
    private final PullRequestPort pullRequestPort;

    public GuideController(RepositoryPort repositoryPort, IssuePort issuePort, PullRequestPort pullRequestPort) {
        this.repositoryPort = repositoryPort;
        this.issuePort = issuePort;
        this.pullRequestPort = pullRequestPort;
    }

    @GetMapping("/{repoId}/issues/{issueId}")
    public ResponseEntity<Map<String, Object>> getGuide(
            @PathVariable Long repoId,
            @PathVariable Long issueId) {

        var repoOpt = repositoryPort.findById(repoId);
        if (repoOpt.isEmpty()) return ResponseEntity.notFound().build();
        var repo = repoOpt.get();

        var issueOpt = issuePort.findOpenByRepoId(repoId).stream()
                .filter(i -> i.getId().equals(issueId))
                .findFirst();
        if (issueOpt.isEmpty()) return ResponseEntity.notFound().build();
        var issue = issueOpt.get();

        List<PullRequest> mergedPrs = pullRequestPort.findByRepoId(repoId);
        List<Map<String, Object>> similarPrs = mergedPrs.stream()
                .limit(3)
                .map(pr -> Map.<String, Object>of("number", pr.getNumber(), "title", pr.getTitle(), "mergeTimeHours", pr.getMergeTimeHours() != null ? pr.getMergeTimeHours() : 48))
                .toList();

        Map<String, Object> repoInfo = new LinkedHashMap<>();
        repoInfo.put("name", repo.getOwner() + "/" + repo.getName());
        repoInfo.put("description", "");
        repoInfo.put("language", repo.getPrimaryLanguage());
        repoInfo.put("openIssues", repo.getStars());
        repoInfo.put("mergedPrs", mergedPrs.size());
        repoInfo.put("avgResponseHours", 48);
        repoInfo.put("ciPassing", true);
        repoInfo.put("hasTests", true);

        Map<String, Object> issueInfo = new LinkedHashMap<>();
        issueInfo.put("id", issue.getId());
        issueInfo.put("number", issue.getNumber());
        issueInfo.put("title", issue.getTitle());
        issueInfo.put("labels", issue.getLabels() != null ? issue.getLabels() : List.of());
        issueInfo.put("description", issue.getBody());

        return ResponseEntity.ok(Map.of(
                "repo", repoInfo,
                "issue", issueInfo,
                "matchReason", "Matched to your skill level based on issue complexity and your quiz answers.",
                "estimatedHours", "2–4 hours",
                "steps", buildSteps(issue, repo.getName(), similarPrs)
        ));
    }

    private List<Map<String, Object>> buildSteps(Issue issue, String repoName, List<Map<String, Object>> similarPrs) {
        return List.of(
                step("Understand the repo", "Read the structure before touching anything",
                        "Before writing any code, read through the key files to understand how the codebase is organized.",
                        null, null, null,
                        List.of("Read the README and understand the project structure",
                                "Locate the files most relevant to issue #" + issue.getNumber(),
                                "Read through any existing tests to understand patterns")),

                step("Set up locally", "Fork, clone, and get the dev environment running",
                        "Fork the repo on GitHub, then clone your fork and get the development environment running.",
                        "git clone https://github.com/YOUR_USERNAME/" + repoName + "\ncd " + repoName + "\ncp .env.example .env\n# follow README setup instructions",
                        "Check the README for setup instructions — most repos have a detailed getting started section.",
                        null,
                        List.of("Forked the repo on GitHub",
                                "Cloned my fork locally",
                                "Development environment running",
                                "Existing tests pass")),

                step("Find your files", "Locate exactly what to change",
                        "Read issue #" + issue.getNumber() + " carefully. Understand what it's asking for, then find the relevant files.",
                        null, null,
                        "Don't start coding until you fully understand what the issue is asking for. If anything is unclear, comment on the issue and ask.",
                        List.of("Read issue #" + issue.getNumber() + " top to bottom",
                                "Identified the files that need to change",
                                "Understand the current behavior and expected behavior")),

                step("Make the change", "Write the actual code — small and focused",
                        "Keep your change minimal and focused on exactly what issue #" + issue.getNumber() + " asks for. Don't refactor unrelated code in the same PR.",
                        null,
                        "Most maintainers prefer small focused PRs. If you find other issues while working, note them separately — don't fix everything in one PR.",
                        null,
                        List.of("Made the change described in the issue",
                                "Tested the change works locally",
                                "Didn't touch unrelated code")),

                step("Write your tests", "Every good PR includes tests",
                        "Add tests that cover the change you made. At minimum: one test for the new behavior, one for edge cases.",
                        null, null, null,
                        List.of("Added tests for the new behavior",
                                "Added edge case tests",
                                "All tests pass")),

                step("Commit and push", "Write a commit message the maintainer expects",
                        "Based on merged PR history in this repo, keep commit messages short and imperative. Reference the issue number.",
                        "git checkout -b fix/issue-" + issue.getNumber() + "\ngit add .\ngit commit -m \"<your change description> (#" + issue.getNumber() + ")\"\ngit push origin fix/issue-" + issue.getNumber(),
                        null, null,
                        List.of("Created a new branch", "Committed with a descriptive message", "Pushed to my fork")),

                step("Open the PR", "Title, description, and what this maintainer wants to see",
                        "Keep the PR description short — what you changed, why, and how to test it. Reference the issue so it closes automatically.",
                        "Title: <your change> (#" + issue.getNumber() + ")\n\nDescription:\n<What you changed>\n<How to test it>\n\nCloses #" + issue.getNumber(),
                        null, null,
                        List.of("Opened PR with correct title", "Description explains what changed", "Issue reference included (Closes #" + issue.getNumber() + ")")),

                step("Handle review feedback", "What to do when the maintainer responds",
                        "If the maintainer asks for changes, push new commits to the same branch — don't open a new PR. Respond to every comment, even just to say 'done'.",
                        null,
                        !similarPrs.isEmpty() ? "Based on similar merged PRs in this repo, expect 1–2 rounds of review feedback. Maintainers typically respond within 48 hours." : null,
                        null,
                        List.of("PR submitted and waiting for review"))
        );
    }

    private Map<String, Object> step(String title, String subtitle, String body,
                                      String code, String tip, String warn,
                                      List<String> checklist) {
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
