package com.openlens.api.controller;

import com.openlens.api.dto.response.ContributionGuideResponse;
import com.openlens.domain.port.input.GetContributionGuideUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guide")
public class GuideController {

    private final GetContributionGuideUseCase getContributionGuideUseCase;

    public GuideController(GetContributionGuideUseCase getContributionGuideUseCase) {
        this.getContributionGuideUseCase = getContributionGuideUseCase;
    }

    @GetMapping("/{repoId}/issues/{issueId}")
    public ResponseEntity<ContributionGuideResponse> getGuide(@PathVariable Long repoId,
                                                               @PathVariable Long issueId) {
        GetContributionGuideUseCase.GuideOutput output = getContributionGuideUseCase.getGuide(repoId, issueId);
        return ResponseEntity.ok(toResponse(output));
    }

    private ContributionGuideResponse toResponse(GetContributionGuideUseCase.GuideOutput output) {
        ContributionGuideResponse.RepoInfo repo = new ContributionGuideResponse.RepoInfo(
                output.repo().name(),
                output.repo().description(),
                output.repo().language(),
                output.repo().openIssues(),
                output.repo().mergedPrs(),
                output.repo().avgResponseHours(),
                output.repo().ciPassing(),
                output.repo().hasTests()
        );

        ContributionGuideResponse.IssueInfo issue = new ContributionGuideResponse.IssueInfo(
                output.issue().id(),
                output.issue().number(),
                output.issue().title(),
                output.issue().labels(),
                output.issue().description()
        );

        List<ContributionGuideResponse.Step> steps = output.steps().stream()
                .map(s -> new ContributionGuideResponse.Step(
                        s.title(), s.subtitle(), s.body(),
                        s.code(), s.tip(), s.warn(), s.checklist()
                ))
                .toList();

        return new ContributionGuideResponse(repo, issue, output.matchReason(),
                output.estimatedHours(), steps);
    }
}
