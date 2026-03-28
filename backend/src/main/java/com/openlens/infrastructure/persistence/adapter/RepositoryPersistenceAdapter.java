package com.openlens.infrastructure.persistence.adapter;

import com.openlens.domain.model.Repository;
import com.openlens.domain.model.RepositoryStatus;
import com.openlens.domain.port.output.RepositoryPort;
import com.openlens.infrastructure.persistence.entity.RepositoryEntity;
import com.openlens.infrastructure.persistence.repository.RepositoryJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

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
        entity.setStatus(repo.getStatus().name());
        entity.setLastAnalyzedAt(repo.getLastAnalyzedAt());
        return entity;
    }

    private Repository toDomain(RepositoryEntity entity) {
        return new Repository(
                entity.getId(),
                entity.getUrl(),
                entity.getOwner(),
                entity.getName(),
                entity.getPrimaryLanguage(),
                entity.getStars(),
                RepositoryStatus.valueOf(entity.getStatus()),
                entity.getLastAnalyzedAt()
        );
    }
}
