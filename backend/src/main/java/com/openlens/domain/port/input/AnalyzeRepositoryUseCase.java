package com.openlens.domain.port.input;

import com.openlens.domain.model.SkillLevel;

// driven by the REST controller when a user submits a repo
public interface AnalyzeRepositoryUseCase {

    AnalysisResponse analyze(String repoUrl, SkillLevel skillLevel);

    record AnalysisResponse(String repoUrl, String jobId, String status) {}
}
