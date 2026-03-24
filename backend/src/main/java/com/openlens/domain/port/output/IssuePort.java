package com.openlens.domain.port.output;

import com.openlens.domain.model.Issue;

import java.util.List;

public interface IssuePort {

    void saveAll(List<Issue> issues);

    List<Issue> findOpenByRepoId(Long repoId);
}
