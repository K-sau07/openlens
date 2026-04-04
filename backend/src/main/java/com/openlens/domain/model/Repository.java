package com.openlens.domain.model;

import java.time.LocalDateTime;

public class Repository {

    private final Long id;
    private final String url;
    private final String owner;
    private final String name;
    private final String primaryLanguage;
    private final int stars;
    private RepositoryStatus status;
    private LocalDateTime lastAnalyzedAt;

    public Repository(Long id, String url, String owner, String name,
                      String primaryLanguage, int stars, RepositoryStatus status,
                      LocalDateTime lastAnalyzedAt) {
        this.id = id;
        this.url = url;
        this.owner = owner;
        this.name = name;
        this.primaryLanguage = primaryLanguage;
        this.stars = stars;
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
        return new Repository(id, url, owner, name, language, stars, status, lastAnalyzedAt);
    }

    public Long getId() { return id; }
    public String getUrl() { return url; }
    public String getOwner() { return owner; }
    public String getName() { return name; }
    public String getPrimaryLanguage() { return primaryLanguage; }
    public int getStars() { return stars; }
    public RepositoryStatus getStatus() { return status; }
    public LocalDateTime getLastAnalyzedAt() { return lastAnalyzedAt; }
}
