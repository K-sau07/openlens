package com.openlens.infrastructure.persistence.adapter;

import com.openlens.domain.model.Repository;
import com.openlens.domain.model.RepositoryStatus;
import com.openlens.domain.port.output.RepositoryPort;
import com.openlens.infrastructure.persistence.entity.RepositoryEntity;
import com.openlens.infrastructure.persistence.repository.RepositoryJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class RepositoryPersistenceAdapter implements RepositoryPort {

    private final RepositoryJpaRepository jpaRepository;

    public RepositoryPersistenceAdapter(RepositoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Repository save(Repository repository) {
        RepositoryEntity entity = toEntity(repository);
        RepositoryEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Repository> findByUrl(String url) {
        return jpaRepository.findByUrl(url).map(this::toDomain);
    }

    @Override
    public Optional<Repository> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, RepositoryStatus status) {
        jpaRepository.updateStatus(id, status.name());
    }

    private RepositoryEntity toEntity(Repository repo) {
        RepositoryEntity entity = new RepositoryEntity();
        entity.setId(repo.getId());
        entity.setUrl(repo.getUrl());
        entity.setOwner(repo.getOwner());
        entity.setName(repo.getName());
        entity.setPrimaryLanguage(repo.getPrimaryLanguage());
        entity.setStars(repo.getStars());
        entity.setDescription(repo.getDescription());
        entity.setTopics(repo.getTopics());
        entity.setForkCount(repo.getForkCount());
        entity.setWatchersCount(repo.getWatchersCount());
        entity.setOpenIssuesCount(repo.getOpenIssuesCount());
        entity.setLicense(repo.getLicense());
        entity.setDefaultBranch(repo.getDefaultBranch());
        entity.setLanguages(repo.getLanguages());
        entity.setHasWiki(repo.isHasWiki());
        entity.setHasDiscussions(repo.isHasDiscussions());
        entity.setCreatedAtGitHub(repo.getCreatedAtGitHub());
        entity.setLastPushedAt(repo.getLastPushedAt());
        entity.setStatus(repo.getStatus().name());
        entity.setLastAnalyzedAt(repo.getLastAnalyzedAt());
        return entity;
    }

    private Repository toDomain(RepositoryEntity entity) {
        return Repository.builder()
                .id(entity.getId())
                .url(entity.getUrl())
                .owner(entity.getOwner())
                .name(entity.getName())
                .primaryLanguage(entity.getPrimaryLanguage())
                .stars(entity.getStars())
                .description(entity.getDescription())
                .topics(entity.getTopics() != null ? entity.getTopics() : List.of())
                .forkCount(entity.getForkCount())
                .watchersCount(entity.getWatchersCount())
                .openIssuesCount(entity.getOpenIssuesCount())
                .license(entity.getLicense())
                .defaultBranch(entity.getDefaultBranch())
                .languages(entity.getLanguages() != null ? entity.getLanguages() : Map.of())
                .hasWiki(entity.isHasWiki())
                .hasDiscussions(entity.isHasDiscussions())
                .createdAtGitHub(entity.getCreatedAtGitHub())
                .lastPushedAt(entity.getLastPushedAt())
                .status(RepositoryStatus.valueOf(entity.getStatus()))
                .lastAnalyzedAt(entity.getLastAnalyzedAt())
                .build();
    }
}
