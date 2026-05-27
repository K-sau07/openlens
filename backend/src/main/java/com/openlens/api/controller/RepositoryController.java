package com.openlens.api.controller;

import com.openlens.api.dto.request.AnalyzeRepoRequest;
import com.openlens.api.dto.response.AnalyzeRepoResponse;
import com.openlens.api.dto.response.RepoProfileResponse;
import com.openlens.api.dto.response.RepoStatusResponse;
import com.openlens.domain.model.SkillLevel;
import com.openlens.domain.port.input.AnalyzeRepositoryUseCase;
import com.openlens.domain.port.input.GetRepoProfileUseCase;
import com.openlens.domain.port.input.GetRepoStatusUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repos")
public class RepositoryController {

    private final AnalyzeRepositoryUseCase analyzeRepositoryUseCase;
    private final GetRepoProfileUseCase getRepoProfileUseCase;
    private final GetRepoStatusUseCase getRepoStatusUseCase;

    public RepositoryController(AnalyzeRepositoryUseCase analyzeRepositoryUseCase,
                                 GetRepoProfileUseCase getRepoProfileUseCase,
                                 GetRepoStatusUseCase getRepoStatusUseCase) {
        this.analyzeRepositoryUseCase = analyzeRepositoryUseCase;
        this.getRepoProfileUseCase = getRepoProfileUseCase;
        this.getRepoStatusUseCase = getRepoStatusUseCase;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeRepoResponse> analyze(@Valid @RequestBody AnalyzeRepoRequest request) {
        SkillLevel level = parseSkillLevel(request.skillLevel());

        AnalyzeRepositoryUseCase.AnalysisResponse result = analyzeRepositoryUseCase.analyze(
                request.repoUrl(), level);

        return ResponseEntity.accepted().body(new AnalyzeRepoResponse(
                result.repoUrl(), result.jobId(), result.status()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepoProfileResponse> getRepoProfile(@PathVariable Long id) {
        GetRepoProfileUseCase.RepoProfileOutput output = getRepoProfileUseCase.getProfile(id);
        return ResponseEntity.ok(toProfileResponse(output));
    }

    @GetMapping("/status")
    public ResponseEntity<RepoStatusResponse> status(@RequestParam String url) {
        GetRepoStatusUseCase.RepoStatusOutput output = getRepoStatusUseCase.getStatus(url);
        return ResponseEntity.ok(new RepoStatusResponse(output.status(), output.repoId()));
    }

    private SkillLevel parseSkillLevel(String raw) {
        if (raw == null || raw.isBlank()) return SkillLevel.BEGINNER;
        try {
            return SkillLevel.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SkillLevel.BEGINNER;
        }
    }

    private RepoProfileResponse toProfileResponse(GetRepoProfileUseCase.RepoProfileOutput o) {
        return new RepoProfileResponse(
                o.id(), o.url(), o.owner(), o.name(),
                o.description(), o.primaryLanguage(), o.stars(),
                o.forkCount(), o.watchersCount(), o.openIssuesCount(),
                o.license(), o.defaultBranch(), o.topics(), o.languages(),
                o.hasWiki(), o.hasDiscussions(), o.createdAtGitHub(),
                o.lastPushedAt(), o.status(), o.lastAnalyzedAt());
    }
}
