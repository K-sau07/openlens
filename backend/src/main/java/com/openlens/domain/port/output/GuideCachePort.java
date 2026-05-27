package com.openlens.domain.port.output;

import java.util.Map;
import java.util.Optional;

public interface GuideCachePort {

    Optional<Map<String, Object>> get(Long repoId, Long issueId);

    void put(Long repoId, Long issueId, Map<String, Object> guide);

    void evictByRepo(Long repoId);
}
