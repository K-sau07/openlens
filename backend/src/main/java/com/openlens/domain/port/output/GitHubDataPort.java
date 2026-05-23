package com.openlens.domain.port.output;

import com.openlens.domain.model.Contributor;
import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;

import java.util.List;
import java.util.Map;

// implemented by the GitHub API adapter in infrastructure
public interface GitHubDataPort {

    List<Issue> fetchOpenIssues(String owner, String repoName);

    List<PullRequest> fetchMergedPullRequests(String owner, String repoName, int limit);

    List<Contributor> fetchContributors(String owner, String repoName);

    String fetchPrimaryLanguage(String owner, String repoName);

    RepoMetadata fetchRepoMetadata(String owner, String repoName);

    Map<String, Long> fetchLanguages(String owner, String repoName);

    record RepoMetadata(
            String description,
            List<String> topics,
            String primaryLanguage,
            int stars,
            int forkCount,
            int watchersCount,
            int openIssuesCount,
            String license,
            String defaultBranch,
            boolean hasWiki,
            boolean hasDiscussions,
            java.time.LocalDateTime createdAt,
            java.time.LocalDateTime lastPushedAt
    ) {}
}
