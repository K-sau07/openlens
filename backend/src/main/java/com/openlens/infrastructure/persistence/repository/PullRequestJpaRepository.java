package com.openlens.infrastructure.persistence.repository;

import com.openlens.infrastructure.persistence.entity.PullRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PullRequestJpaRepository extends JpaRepository<PullRequestEntity, Long> {

    List<PullRequestEntity> findByRepoId(Long repoId);

    boolean existsByRepoIdAndNumber(Long repoId, int number);
}
