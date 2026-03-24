package com.openlens.domain.port.output;

import com.openlens.domain.model.PullRequest;

import java.util.List;

public interface PullRequestPort {

    void saveAll(List<PullRequest> pullRequests);

    List<PullRequest> findByRepoId(Long repoId);
}
