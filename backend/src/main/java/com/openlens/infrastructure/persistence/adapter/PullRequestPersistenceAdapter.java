package com.openlens.infrastructure.persistence.adapter;

import com.openlens.domain.model.PullRequest;
import com.openlens.domain.port.output.PullRequestPort;
import com.openlens.infrastructure.persistence.entity.PullRequestEntity;
import com.openlens.infrastructure.persistence.repository.PullRequestJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PullRequestPersistenceAdapter implements PullRequestPort {

    private final PullRequestJpaRepository pullRequestJpaRepository;

    public PullRequestPersistenceAdapter(PullRequestJpaRepository pullRequestJpaRepository) {
        this.pullRequestJpaRepository = pullRequestJpaRepository;
    }

    @Override
    public void saveAll(List<PullRequest> pullRequests) {
        List<PullRequestEntity> entities = pullRequests.stream()
                .filter(pr -> !pullRequestJpaRepository.existsByRepoIdAndNumber(pr.getRepoId(), pr.getNumber()))
                .map(this::toEntity)
                .toList();
        pullRequestJpaRepository.saveAll(entities);
    }

    @Override
    public List<PullRequest> findByRepoId(Long repoId) {
        return pullRequestJpaRepository.findByRepoId(repoId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private PullRequestEntity toEntity(PullRequest pr) {
        PullRequestEntity entity = new PullRequestEntity();
        entity.setRepoId(pr.getRepoId());
        entity.setNumber(pr.getNumber());
        entity.setTitle(pr.getTitle());
        entity.setFilesChanged(pr.getFilesChanged());
        entity.setLinesAdded(pr.getLinesAdded());
        entity.setLinesRemoved(pr.getLinesRemoved());
        entity.setMergeTimeHours(pr.getMergeTimeHours());
        entity.setLinkedIssueNumber(pr.getLinkedIssueNumber());
        entity.setAuthor(pr.getAuthor());
        entity.setMergedAt(pr.getMergedAt());
        return entity;
    }

    private PullRequest toDomain(PullRequestEntity entity) {
        return new PullRequest(
                entity.getId(),
                entity.getRepoId(),
                entity.getNumber(),
                entity.getTitle(),
                entity.getFilesChanged(),
                entity.getLinesAdded(),
                entity.getLinesRemoved(),
                entity.getMergeTimeHours(),
                entity.getLinkedIssueNumber(),
                entity.getAuthor(),
                entity.getMergedAt()
        );
    }
}
