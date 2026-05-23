package com.openlens.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Repository {

    private final Long id;
    private final String url;
    private final String owner;
    private final String name;
    private final String primaryLanguage;
    private final int stars;
    private final String description;
    private final List<String> topics;
    private final int forkCount;
    private final int watchersCount;
    private final int openIssuesCount;
    private final String license;
    private final String defaultBranch;
    private final Map<String, Long> languages;
    private final boolean hasWiki;
    private final boolean hasDiscussions;
    private final LocalDateTime createdAtGitHub;
    private final LocalDateTime lastPushedAt;
    private RepositoryStatus status;
    private LocalDateTime lastAnalyzedAt;

    public Repository(Long id, String url, String owner, String name,
                      String primaryLanguage, int stars, RepositoryStatus status,
                      LocalDateTime lastAnalyzedAt) {
        this(id, url, owner, name, primaryLanguage, stars,
                null, List.of(), 0, 0, 0, null, null, Map.of(),
                false, false, null, null, status, lastAnalyzedAt);
    }

    public Repository(Long id, String url, String owner, String name,
                      String primaryLanguage, int stars,
                      String description, List<String> topics,
                      int forkCount, int watchersCount, int openIssuesCount,
                      String license, String defaultBranch, Map<String, Long> languages,
                      boolean hasWiki, boolean hasDiscussions,
                      LocalDateTime createdAtGitHub, LocalDateTime lastPushedAt,
                      RepositoryStatus status, LocalDateTime lastAnalyzedAt) {
        this.id = id;
        this.url = url;
        this.owner = owner;
        this.name = name;
        this.primaryLanguage = primaryLanguage;
        this.stars = stars;
        this.description = description;
        this.topics = topics != null ? topics : List.of();
        this.forkCount = forkCount;
        this.watchersCount = watchersCount;
        this.openIssuesCount = openIssuesCount;
        this.license = license;
        this.defaultBranch = defaultBranch;
        this.languages = languages != null ? languages : Map.of();
        this.hasWiki = hasWiki;
        this.hasDiscussions = hasDiscussions;
        this.createdAtGitHub = createdAtGitHub;
        this.lastPushedAt = lastPushedAt;
        this.status = status;
        this.lastAnalyzedAt = lastAnalyzedAt;
    }

    public boolean isReady() {
        return status == RepositoryStatus.READY;
    }

    public boolean needsAnalysis() {
        if (lastAnalyzedAt == null) return true;
        return lastAnalyzedAt.isBefore(LocalDateTime.now().minusHours(24));
    }

    public void markIngesting() {
        this.status = RepositoryStatus.INGESTING;
    }

    public void markAnalyzing() {
        this.status = RepositoryStatus.ANALYZING;
    }

    public void markReady() {
        this.status = RepositoryStatus.READY;
        this.lastAnalyzedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = RepositoryStatus.FAILED;
    }

    public Repository withLanguage(String language) {
        return new Repository(id, url, owner, name, language, stars,
                description, topics, forkCount, watchersCount, openIssuesCount,
                license, defaultBranch, languages, hasWiki, hasDiscussions,
                createdAtGitHub, lastPushedAt, status, lastAnalyzedAt);
    }

    public Repository withMetadata(String description, List<String> topics,
                                    int forkCount, int watchersCount, int openIssuesCount,
                                    String license, String defaultBranch,
                                    boolean hasWiki, boolean hasDiscussions,
                                    LocalDateTime createdAtGitHub, LocalDateTime lastPushedAt,
                                    int stars) {
        return new Repository(id, url, owner, name, primaryLanguage, stars,
                description, topics, forkCount, watchersCount, openIssuesCount,
                license, defaultBranch, languages, hasWiki, hasDiscussions,
                createdAtGitHub, lastPushedAt, status, lastAnalyzedAt);
    }

    public Repository withLanguages(Map<String, Long> languages) {
        return new Repository(id, url, owner, name, primaryLanguage, stars,
                description, topics, forkCount, watchersCount, openIssuesCount,
                license, defaultBranch, languages, hasWiki, hasDiscussions,
                createdAtGitHub, lastPushedAt, status, lastAnalyzedAt);
    }

    public Long getId() { return id; }
    public String getUrl() { return url; }
    public String getOwner() { return owner; }
    public String getName() { return name; }
    public String getPrimaryLanguage() { return primaryLanguage; }
    public int getStars() { return stars; }
    public String getDescription() { return description; }
    public List<String> getTopics() { return topics; }
    public int getForkCount() { return forkCount; }
    public int getWatchersCount() { return watchersCount; }
    public int getOpenIssuesCount() { return openIssuesCount; }
    public String getLicense() { return license; }
    public String getDefaultBranch() { return defaultBranch; }
    public Map<String, Long> getLanguages() { return languages; }
    public boolean isHasWiki() { return hasWiki; }
    public boolean isHasDiscussions() { return hasDiscussions; }
    public LocalDateTime getCreatedAtGitHub() { return createdAtGitHub; }
    public LocalDateTime getLastPushedAt() { return lastPushedAt; }
    public RepositoryStatus getStatus() { return status; }
    public LocalDateTime getLastAnalyzedAt() { return lastAnalyzedAt; }
}
