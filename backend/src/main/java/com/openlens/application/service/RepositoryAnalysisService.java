package com.openlens.application.service;

import com.openlens.domain.exception.ResourceNotFoundException;
import com.openlens.domain.model.Repository;
import com.openlens.domain.model.RepositoryStatus;
import com.openlens.domain.model.SkillLevel;
import com.openlens.domain.port.input.AnalyzeRepositoryUseCase;
import com.openlens.domain.port.input.GetRepoProfileUseCase;
import com.openlens.domain.port.input.GetRepoStatusUseCase;
import com.openlens.domain.port.output.IngestionJobPort;
import com.openlens.domain.port.output.RepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RepositoryAnalysisService implements AnalyzeRepositoryUseCase,
        GetRepoProfileUseCase, GetRepoStatusUseCase {

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

        persistIfNew(repoUrl, owner, repoName, existing);

        String jobId = UUID.randomUUID().toString();
        ingestionJobPort.publishIngestionRequest(repoUrl, owner, repoName);

        log.info("ingestion job {} published for {}", jobId, repoUrl);
        return new AnalysisResponse(repoUrl, jobId, "PROCESSING");
    }

    @Transactional
    protected void persistIfNew(String repoUrl, String owner, String repoName,
                                 Optional<Repository> existing) {
        if (existing.isEmpty()) {
            Repository repo = Repository.builder()
                    .url(repoUrl).owner(owner).name(repoName)
                    .status(RepositoryStatus.PENDING)
                    .build();
            repositoryPort.save(repo);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RepoProfileOutput getProfile(Long repoId) {
        Repository r = repositoryPort.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("repository not found: " + repoId));

        return new RepoProfileOutput(
                r.getId(), r.getUrl(), r.getOwner(), r.getName(),
                r.getDescription(), r.getPrimaryLanguage(), r.getStars(),
                r.getForkCount(), r.getWatchersCount(), r.getOpenIssuesCount(),
                r.getLicense(), r.getDefaultBranch(),
                r.getTopics() != null ? r.getTopics() : List.of(),
                r.getLanguages() != null ? r.getLanguages() : Map.of(),
                r.isHasWiki(), r.isHasDiscussions(),
                r.getCreatedAtGitHub(), r.getLastPushedAt(),
                r.getStatus().name(), r.getLastAnalyzedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RepoStatusOutput getStatus(String repoUrl) {
        Optional<Repository> repoOpt = repositoryPort.findByUrl(repoUrl);

        if (repoOpt.isEmpty()) {
            return new RepoStatusOutput("PENDING", null);
        }

        Repository r = repoOpt.get();
        String status = r.getStatus().name();

        if ("INGESTING".equals(status) || "ANALYZING".equals(status)) {
            status = "PROCESSING";
        }

        return new RepoStatusOutput(status, r.getId());
    }

    private String[] parseRepoUrl(String url) {
        String cleaned = url.replaceAll("https?://", "").replaceAll("github\\.com/", "");
        String[] parts = cleaned.split("/");
        if (parts.length < 2) {
            throw new IllegalArgumentException("invalid GitHub repo URL: " + url);
        }
        return new String[]{parts[0], parts[1].replace(".git", "")};
    }
}
