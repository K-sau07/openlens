package com.openlens.application.service;

import com.openlens.domain.model.Repository;
import com.openlens.domain.model.RepositoryStatus;
import com.openlens.domain.model.SkillLevel;
import com.openlens.domain.port.input.AnalyzeRepositoryUseCase;
import com.openlens.domain.port.output.IngestionJobPort;
import com.openlens.domain.port.output.RepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RepositoryAnalysisService implements AnalyzeRepositoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(RepositoryAnalysisService.class);

    private final RepositoryPort repositoryPort;
    private final IngestionJobPort ingestionJobPort;

    public RepositoryAnalysisService(RepositoryPort repositoryPort, IngestionJobPort ingestionJobPort) {
        this.repositoryPort = repositoryPort;
        this.ingestionJobPort = ingestionJobPort;
    }

    @Override
    public AnalysisResponse analyze(String repoUrl, SkillLevel skillLevel) {
        log.info("analysis requested for {} at skill level {}", repoUrl, skillLevel);

        Optional<Repository> existing = repositoryPort.findByUrl(repoUrl);

        if (existing.isPresent() && existing.get().isReady() && !existing.get().needsAnalysis()) {
            log.debug("repo {} already analyzed and fresh, skipping ingestion", repoUrl);
            return new AnalysisResponse(repoUrl, null, "READY");
        }

        String[] parts = parseRepoUrl(repoUrl);
        String owner = parts[0];
        String repoName = parts[1];

        if (existing.isEmpty()) {
            Repository repo = new Repository(null, repoUrl, owner, repoName, null, 0, RepositoryStatus.PENDING, null);
            repositoryPort.save(repo);
        }

        String jobId = UUID.randomUUID().toString();
        ingestionJobPort.publishIngestionRequest(repoUrl, owner, repoName);

        log.info("ingestion job {} published for {}", jobId, repoUrl);
        return new AnalysisResponse(repoUrl, jobId, "PROCESSING");
    }

    // expects https://github.com/owner/repo or github.com/owner/repo
    private String[] parseRepoUrl(String url) {
        String cleaned = url.replaceAll("https?://", "").replaceAll("github\\.com/", "");
        String[] parts = cleaned.split("/");
        if (parts.length < 2) {
            throw new IllegalArgumentException("invalid GitHub repo URL: " + url);
        }
        return new String[]{parts[0], parts[1].replace(".git", "")};
    }
}
