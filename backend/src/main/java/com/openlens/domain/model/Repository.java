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

    private Repository(Builder builder) {
        this.id = builder.id;
        this.url = builder.url;
        this.owner = builder.owner;
        this.name = builder.name;
        this.primaryLanguage = builder.primaryLanguage;
        this.stars = builder.stars;
        this.description = builder.description;
        this.topics = builder.topics != null ? builder.topics : List.of();
        this.forkCount = builder.forkCount;
        this.watchersCount = builder.watchersCount;
        this.openIssuesCount = builder.openIssuesCount;
        this.license = builder.license;
        this.defaultBranch = builder.defaultBranch;
        this.languages = builder.languages != null ? builder.languages : Map.of();
        this.hasWiki = builder.hasWiki;
        this.hasDiscussions = builder.hasDiscussions;
        this.createdAtGitHub = builder.createdAtGitHub;
        this.lastPushedAt = builder.lastPushedAt;
        this.status = builder.status;
        this.lastAnalyzedAt = builder.lastAnalyzedAt;
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

    public Builder toBuilder() {
        return new Builder()
                .id(id).url(url).owner(owner).name(name)
                .primaryLanguage(primaryLanguage).stars(stars)
                .description(description).topics(topics)
                .forkCount(forkCount).watchersCount(watchersCount)
                .openIssuesCount(openIssuesCount).license(license)
                .defaultBranch(defaultBranch).languages(languages)
                .hasWiki(hasWiki).hasDiscussions(hasDiscussions)
                .createdAtGitHub(createdAtGitHub).lastPushedAt(lastPushedAt)
                .status(status).lastAnalyzedAt(lastAnalyzedAt);
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {
        private Long id;
        private String url;
        private String owner;
        private String name;
        private String primaryLanguage;
        private int stars;
        private String description;
        private List<String> topics;
        private int forkCount;
        private int watchersCount;
        private int openIssuesCount;
        private String license;
        private String defaultBranch;
        private Map<String, Long> languages;
        private boolean hasWiki;
        private boolean hasDiscussions;
        private LocalDateTime createdAtGitHub;
        private LocalDateTime lastPushedAt;
        private RepositoryStatus status;
        private LocalDateTime lastAnalyzedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder url(String url) { this.url = url; return this; }
        public Builder owner(String owner) { this.owner = owner; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder primaryLanguage(String primaryLanguage) { this.primaryLanguage = primaryLanguage; return this; }
        public Builder stars(int stars) { this.stars = stars; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder topics(List<String> topics) { this.topics = topics; return this; }
        public Builder forkCount(int forkCount) { this.forkCount = forkCount; return this; }
        public Builder watchersCount(int watchersCount) { this.watchersCount = watchersCount; return this; }
        public Builder openIssuesCount(int openIssuesCount) { this.openIssuesCount = openIssuesCount; return this; }
        public Builder license(String license) { this.license = license; return this; }
        public Builder defaultBranch(String defaultBranch) { this.defaultBranch = defaultBranch; return this; }
        public Builder languages(Map<String, Long> languages) { this.languages = languages; return this; }
        public Builder hasWiki(boolean hasWiki) { this.hasWiki = hasWiki; return this; }
        public Builder hasDiscussions(boolean hasDiscussions) { this.hasDiscussions = hasDiscussions; return this; }
        public Builder createdAtGitHub(LocalDateTime createdAtGitHub) { this.createdAtGitHub = createdAtGitHub; return this; }
        public Builder lastPushedAt(LocalDateTime lastPushedAt) { this.lastPushedAt = lastPushedAt; return this; }
        public Builder status(RepositoryStatus status) { this.status = status; return this; }
        public Builder lastAnalyzedAt(LocalDateTime lastAnalyzedAt) { this.lastAnalyzedAt = lastAnalyzedAt; return this; }

        public Repository build() {
            return new Repository(this);
        }
    }
}
