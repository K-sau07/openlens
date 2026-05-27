package com.openlens.infrastructure.persistence.adapter;

import com.openlens.domain.model.Issue;
import com.openlens.domain.port.output.IssuePort;
import com.openlens.infrastructure.persistence.entity.IssueEntity;
import com.openlens.infrastructure.persistence.repository.IssueJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class IssuePersistenceAdapter implements IssuePort {

    private final IssueJpaRepository issueJpaRepository;

    public IssuePersistenceAdapter(IssueJpaRepository issueJpaRepository) {
        this.issueJpaRepository = issueJpaRepository;
    }

    @Override
    public void saveAll(List<Issue> issues) {
        List<IssueEntity> entities = issues.stream()
                .filter(issue -> !issueJpaRepository.existsByRepoIdAndNumber(issue.getRepoId(), issue.getNumber()))
                .map(this::toEntity)
                .toList();
        issueJpaRepository.saveAll(entities);
    }

    @Override
    public List<Issue> findOpenByRepoId(Long repoId) {
        return issueJpaRepository.findByRepoIdAndState(repoId, "open")
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Issue> findById(Long issueId) {
        return issueJpaRepository.findById(issueId).map(this::toDomain);
    }

    private IssueEntity toEntity(Issue issue) {
        IssueEntity entity = new IssueEntity();
        entity.setRepoId(issue.getRepoId());
        entity.setNumber(issue.getNumber());
        entity.setTitle(issue.getTitle());
        entity.setBody(issue.getBody());
        entity.setLabels(issue.getLabels());
        entity.setState(issue.getState());
        entity.setComplexityScore(issue.getComplexityScore());
        entity.setCommentCount(issue.getCommentCount());
        entity.setAuthor(issue.getAuthor());
        entity.setAssignee(issue.getAssignee());
        entity.setReactionsCount(issue.getReactionsCount());
        entity.setGithubCreatedAt(issue.getGithubCreatedAt());
        entity.setGithubUpdatedAt(issue.getGithubUpdatedAt());
        return entity;
    }

    private Issue toDomain(IssueEntity entity) {
        return Issue.builder()
                .id(entity.getId())
                .repoId(entity.getRepoId())
                .number(entity.getNumber())
                .title(entity.getTitle())
                .body(entity.getBody())
                .labels(entity.getLabels() != null ? entity.getLabels() : List.of())
                .state(entity.getState())
                .complexityScore(entity.getComplexityScore())
                .commentCount(entity.getCommentCount())
                .author(entity.getAuthor())
                .assignee(entity.getAssignee())
                .reactionsCount(entity.getReactionsCount())
                .githubCreatedAt(entity.getGithubCreatedAt())
                .githubUpdatedAt(entity.getGithubUpdatedAt())
                .build();
    }
}
