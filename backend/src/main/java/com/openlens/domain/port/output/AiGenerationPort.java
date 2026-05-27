package com.openlens.domain.port.output;

import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;

import java.util.List;
import java.util.Map;

public interface AiGenerationPort {

    List<Map<String, Object>> generateQuizQuestions(String repoName, String language,
                                                    List<Issue> issues, List<PullRequest> mergedPrs);

    Map<String, Object> generateContributionGuide(String repoName, String language,
                                                   Issue issue, List<PullRequest> mergedPrs);
}
