package com.openlens.domain.port.output;

import com.openlens.domain.model.Issue;

import java.util.List;
import java.util.Optional;

public interface IssuePort {

    void saveAll(List<Issue> issues);

    List<Issue> findOpenByRepoId(Long repoId);

    Optional<Issue> findById(Long issueId);
}
