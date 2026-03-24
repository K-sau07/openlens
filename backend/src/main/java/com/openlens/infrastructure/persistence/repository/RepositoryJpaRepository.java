package com.openlens.infrastructure.persistence.repository;

import com.openlens.infrastructure.persistence.entity.RepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RepositoryJpaRepository extends JpaRepository<RepositoryEntity, Long> {

    Optional<RepositoryEntity> findByUrl(String url);

    @Modifying
    @Query("update RepositoryEntity r set r.status = :status where r.id = :id")
    void updateStatus(Long id, String status);
}
