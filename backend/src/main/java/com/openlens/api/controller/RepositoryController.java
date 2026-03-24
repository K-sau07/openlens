package com.openlens.api.controller;

import com.openlens.api.dto.request.AnalyzeRepoRequest;
import com.openlens.api.dto.response.AnalyzeRepoResponse;
import com.openlens.domain.model.SkillLevel;
import com.openlens.domain.port.input.AnalyzeRepositoryUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repos")
public class RepositoryController {

    private final AnalyzeRepositoryUseCase analyzeRepositoryUseCase;

    public RepositoryController(AnalyzeRepositoryUseCase analyzeRepositoryUseCase) {
        this.analyzeRepositoryUseCase = analyzeRepositoryUseCase;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeRepoResponse> analyze(@Valid @RequestBody AnalyzeRepoRequest request) {
        AnalyzeRepositoryUseCase.AnalysisResponse result = analyzeRepositoryUseCase.analyze(
                request.repoUrl(),
                SkillLevel.valueOf(request.skillLevel().toUpperCase())
        );

        return ResponseEntity.accepted().body(new AnalyzeRepoResponse(
                result.repoUrl(),
                result.jobId(),
                result.status()
        ));
    }
}
