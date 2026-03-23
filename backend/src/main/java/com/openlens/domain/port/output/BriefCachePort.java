package com.openlens.domain.port.output;

import com.openlens.domain.model.ContributionBrief;
import com.openlens.domain.model.SkillLevel;

import java.util.List;
import java.util.Optional;

// implemented by Redis adapter — cache-aside pattern
public interface BriefCachePort {

    Optional<List<ContributionBrief>> get(String repoUrl, SkillLevel skillLevel);

    void put(String repoUrl, SkillLevel skillLevel, List<ContributionBrief> briefs);

    void evict(String repoUrl);
}
