package com.openlens.infrastructure.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;
import com.openlens.domain.model.Repository;
import com.openlens.domain.port.output.GitHubDataPort;
import com.openlens.domain.port.output.GitHubDataPort.RepoMetadata;
import com.openlens.domain.port.output.IssuePort;
import com.openlens.domain.port.output.PullRequestPort;
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
    private final IssuePort issuePort;
    private final PullRequestPort pullRequestPort;
    private final ObjectMapper objectMapper;

    public IngestionJobConsumer(GitHubDataPort gitHubDataPort,
                                RepositoryPort repositoryPort,
                                IssuePort issuePort,
                                PullRequestPort pullRequestPort,
                                ObjectMapper objectMapper) {
        this.gitHubDataPort = gitHubDataPort;
        this.repositoryPort = repositoryPort;
        this.issuePort = issuePort;
        this.pullRequestPort = pullRequestPort;
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

            markIngesting(repoUrl);
            runIngestion(repoUrl, owner, repoName);

        } catch (Exception e) {
            log.error("ingestion failed for {}", repoUrl, e);
            markFailed(repoUrl);
        }
    }

    private void runIngestion(String repoUrl, String owner, String repoName) {
        log.info("starting GitHub data fetch for {}/{}", owner, repoName);

        // single call gets everything from the repo endpoint — replaces the old fetchPrimaryLanguage
        RepoMetadata metadata = gitHubDataPort.fetchRepoMetadata(owner, repoName);
        Map<String, Long> languages = gitHubDataPort.fetchLanguages(owner, repoName);
        List<Issue> issues = gitHubDataPort.fetchOpenIssues(owner, repoName);
        List<PullRequest> mergedPrs = gitHubDataPort.fetchMergedPullRequests(owner, repoName, 50);

        log.info("fetched metadata, {} languages, {} issues, {} merged PRs for {}/{}",
                languages.size(), issues.size(), mergedPrs.size(), owner, repoName);

        Optional<Repository> existing = repositoryPort.findByUrl(repoUrl);
        if (existing.isEmpty()) {
            log.warn("no repo record found for {} — skipping persistence", repoUrl);
            return;
        }

        Repository repo = existing.get()
                .withLanguage(metadata.primaryLanguage())
                .withMetadata(
                        metadata.description(),
                        metadata.topics(),
                        metadata.forkCount(),
                        metadata.watchersCount(),
                        metadata.openIssuesCount(),
                        metadata.license(),
                        metadata.defaultBranch(),
                        metadata.hasWiki(),
                        metadata.hasDiscussions(),
                        metadata.createdAt(),
                        metadata.lastPushedAt(),
                        metadata.stars()
                )
                .withLanguages(languages);

        repo.markReady();
        Repository saved = repositoryPort.save(repo);
        Long repoId = saved.getId();

        List<Issue> stamped = issues.stream()
                .map(i -> new Issue(i.getId(), repoId, i.getNumber(), i.getTitle(),
                        i.getBody(), i.getLabels(), i.getState(), i.getComplexityScore()))
                .toList();

        List<PullRequest> stampedPrs = mergedPrs.stream()
                .map(pr -> new PullRequest(pr.getId(), repoId, pr.getNumber(), pr.getTitle(),
                        pr.getFilesChanged(), pr.getLinesAdded(), pr.getLinesRemoved(),
                        pr.getMergeTimeHours(), pr.getLinkedIssueNumber(),
                        pr.getAuthor(), pr.getMergedAt()))
                .toList();

        issuePort.saveAll(stamped);
        pullRequestPort.saveAll(stampedPrs);

        log.info("ingestion complete for {} — {} issues, {} PRs, {} languages, license: {}, stars: {}",
                repoUrl, stamped.size(), stampedPrs.size(), languages.size(),
                metadata.license(), metadata.stars());
    }

    private void markIngesting(String repoUrl) {
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
