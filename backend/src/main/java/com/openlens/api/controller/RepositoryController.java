package com.openlens.api.controller;

import com.openlens.api.dto.request.AnalyzeRepoRequest;
import com.openlens.api.dto.response.AnalyzeRepoResponse;
import com.openlens.api.dto.response.RepoProfileResponse;
import com.openlens.domain.model.Repository;
import com.openlens.domain.model.RepositoryStatus;
import com.openlens.domain.model.SkillLevel;
import com.openlens.domain.port.input.AnalyzeRepositoryUseCase;
import com.openlens.domain.port.output.RepositoryPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/repos")
public class RepositoryController {

    private final AnalyzeRepositoryUseCase analyzeRepositoryUseCase;
    private final RepositoryPort repositoryPort;

    public RepositoryController(AnalyzeRepositoryUseCase analyzeRepositoryUseCase,
                                 RepositoryPort repositoryPort) {
        this.analyzeRepositoryUseCase = analyzeRepositoryUseCase;
        this.repositoryPort = repositoryPort;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeRepoResponse> analyze(@Valid @RequestBody AnalyzeRepoRequest request) {
        SkillLevel level = SkillLevel.BEGINNER;
        if (request.skillLevel() != null && !request.skillLevel().isBlank()) {
            try { level = SkillLevel.valueOf(request.skillLevel().toUpperCase()); } catch (Exception ignored) {}
        }

        AnalyzeRepositoryUseCase.AnalysisResponse result = analyzeRepositoryUseCase.analyze(
                request.repoUrl(), level
        );

        return ResponseEntity.accepted().body(new AnalyzeRepoResponse(
                result.repoUrl(), result.jobId(), result.status()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepoProfileResponse> getRepoProfile(@PathVariable Long id) {
        Optional<Repository> repoOpt = repositoryPort.findById(id);
        if (repoOpt.isEmpty()) return ResponseEntity.notFound().build();

        Repository r = repoOpt.get();
        return ResponseEntity.ok(new RepoProfileResponse(
                r.getId(), r.getUrl(), r.getOwner(), r.getName(),
                r.getDescription(), r.getPrimaryLanguage(), r.getStars(),
                r.getForkCount(), r.getWatchersCount(), r.getOpenIssuesCount(),
                r.getLicense(), r.getDefaultBranch(), r.getTopics(), r.getLanguages(),
                r.isHasWiki(), r.isHasDiscussions(), r.getCreatedAtGitHub(),
                r.getLastPushedAt(), r.getStatus().name(), r.getLastAnalyzedAt()
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status(@RequestParam String url) {
        Optional<Repository> repo = repositoryPort.findByUrl(url);

        if (repo.isEmpty()) {
            return ResponseEntity.ok(Map.of("status", "PENDING", "repoId", (Object) null));
        }

        Repository r = repo.get();
        String status = r.getStatus().name();

        if (status.equals("INGESTING") || status.equals("ANALYZING")) {
            status = "PROCESSING";
        }

        return ResponseEntity.ok(Map.of(
                "status", status,
                "repoId", r.getId() != null ? r.getId() : (Object) ""
        ));
    }
}
