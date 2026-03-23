package com.openlens.domain.port.input;

import com.openlens.domain.model.ContributionBrief;
import com.openlens.domain.model.SkillLevel;

import java.util.List;

// driven by the REST controller when a user requests their brief
public interface GetContributionBriefUseCase {

    List<ContributionBrief> getBriefs(String repoUrl, SkillLevel skillLevel);
}
