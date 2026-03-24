package com.openlens.infrastructure.persistence.repository;

import com.openlens.infrastructure.persistence.entity.IssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueJpaRepository extends JpaRepository<IssueEntity, Long> {

    List<IssueEntity> findByRepoId(Long repoId);

    List<IssueEntity> findByRepoIdAndState(Long repoId, String state);

    boolean existsByRepoIdAndNumber(Long repoId, int number);
}
