package com.openlens.infrastructure.persistence.adapter;

import com.openlens.domain.model.Issue;
import com.openlens.domain.port.output.IssuePort;
import com.openlens.infrastructure.persistence.entity.IssueEntity;
import com.openlens.infrastructure.persistence.repository.IssueJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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

    private IssueEntity toEntity(Issue issue) {
        IssueEntity entity = new IssueEntity();
        entity.setRepoId(issue.getRepoId());
        entity.setNumber(issue.getNumber());
        entity.setTitle(issue.getTitle());
        entity.setBody(issue.getBody());
        entity.setLabels(issue.getLabels() != null ? String.join(",", issue.getLabels()) : null);
        entity.setState(issue.getState());
        entity.setComplexityScore(issue.getComplexityScore());
        return entity;
    }

    private Issue toDomain(IssueEntity entity) {
        List<String> labels = entity.getLabels() != null
                ? Arrays.asList(entity.getLabels().split(","))
                : List.of();
        return new Issue(
                entity.getId(),
                entity.getRepoId(),
                entity.getNumber(),
                entity.getTitle(),
                entity.getBody(),
                labels,
                entity.getState(),
                entity.getComplexityScore()
        );
    }
}
