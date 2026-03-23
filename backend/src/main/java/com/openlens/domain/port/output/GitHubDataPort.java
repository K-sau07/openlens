package com.openlens.domain.port.output;

import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;
import com.openlens.domain.model.Contributor;

import java.util.List;

// implemented by the GitHub API adapter in infrastructure
public interface GitHubDataPort {

    List<Issue> fetchOpenIssues(String owner, String repoName);

    List<PullRequest> fetchMergedPullRequests(String owner, String repoName, int limit);

    List<Contributor> fetchContributors(String owner, String repoName);

    String fetchPrimaryLanguage(String owner, String repoName);
}
