package com.openlens.domain.port.output;

import com.openlens.domain.model.Repository;

import java.util.Optional;

// implemented by the JPA adapter in infrastructure
public interface RepositoryPort {

    Repository save(Repository repository);

    Optional<Repository> findByUrl(String url);

    Optional<Repository> findById(Long id);

    void updateStatus(Long id, com.openlens.domain.model.RepositoryStatus status);
}
