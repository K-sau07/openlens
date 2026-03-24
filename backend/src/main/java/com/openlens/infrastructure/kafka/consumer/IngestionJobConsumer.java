package com.openlens.infrastructure.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;
import com.openlens.domain.model.Repository;
import com.openlens.domain.port.output.GitHubDataPort;
import com.openlens.domain.port.output.RepositoryPort;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class IngestionJobConsumer {

    private static final Logger log = LoggerFactory.getLogger(IngestionJobConsumer.class);

    private final GitHubDataPort gitHubDataPort;
    private final RepositoryPort repositoryPort;
    private final ObjectMapper objectMapper;

    public IngestionJobConsumer(GitHubDataPort gitHubDataPort,
                                RepositoryPort repositoryPort,
                                ObjectMapper objectMapper) {
        this.gitHubDataPort = gitHubDataPort;
        this.repositoryPort = repositoryPort;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "repo.ingestion.requested", groupId = "openlens-ingestion")
    public void consume(ConsumerRecord<String, Object> record) {
        String repoUrl = record.key();
        log.info("picked up ingestion job for {}", repoUrl);

        try {
            Map<String, String> payload = objectMapper.convertValue(record.value(), Map.class);
            String owner = payload.get("owner");
            String repoName = payload.get("repoName");

            if (owner == null || repoName == null) {
                log.error("malformed ingestion payload — missing owner or repoName for {}", repoUrl);
                return;
            }

            markInProgress(repoUrl);
            runIngestion(repoUrl, owner, repoName);

        } catch (Exception e) {
            log.error("ingestion failed for {}", repoUrl, e);
            markFailed(repoUrl);
        }
    }

    private void runIngestion(String repoUrl, String owner, String repoName) {
        log.info("starting GitHub data fetch for {}/{}", owner, repoName);

        // fire all three calls — virtual threads handle concurrency
        String language = gitHubDataPort.fetchPrimaryLanguage(owner, repoName);
        List<Issue> issues = gitHubDataPort.fetchOpenIssues(owner, repoName);
        List<PullRequest> mergedPrs = gitHubDataPort.fetchMergedPullRequests(owner, repoName, 50);

        log.info("fetched {} issues and {} merged PRs for {}/{}",
                issues.size(), mergedPrs.size(), owner, repoName);

        Optional<Repository> existing = repositoryPort.findByUrl(repoUrl);
        if (existing.isEmpty()) {
            log.warn("no repo record found for {} — was it saved before publishing?", repoUrl);
            return;
        }

        Repository repo = existing.get();
        repo.markReady();
        repositoryPort.save(repo);
        log.info("ingestion complete for {} — {} issues, {} merged PRs, language: {}",
                repoUrl, issues.size(), mergedPrs.size(), language);
    }

    private void markInProgress(String repoUrl) {
        repositoryPort.findByUrl(repoUrl).ifPresent(repo -> {
            repo.markIngesting();
            repositoryPort.save(repo);
        });
    }

    private void markFailed(String repoUrl) {
        repositoryPort.findByUrl(repoUrl).ifPresent(repo -> {
            repo.markFailed();
            repositoryPort.save(repo);
        });
    }
}
