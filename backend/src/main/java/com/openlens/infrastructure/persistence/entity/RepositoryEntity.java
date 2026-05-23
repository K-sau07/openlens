package com.openlens.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "repositories")
public class RepositoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false)
    private String name;

    @Column(name = "primary_language")
    private String primaryLanguage;

    private int stars;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> topics;

    @Column(name = "fork_count")
    private int forkCount;

    @Column(name = "watchers_count")
    private int watchersCount;

    @Column(name = "open_issues_count")
    private int openIssuesCount;

    private String license;

    @Column(name = "default_branch")
    private String defaultBranch;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Long> languages;

    @Column(name = "has_wiki")
    private boolean hasWiki;

    @Column(name = "has_discussions")
    private boolean hasDiscussions;

    @Column(name = "created_at_github")
    private LocalDateTime createdAtGitHub;

    @Column(name = "last_pushed_at")
    private LocalDateTime lastPushedAt;

    @Column(nullable = false)
    private String status;

    @Column(name = "last_analyzed_at")
    private LocalDateTime lastAnalyzedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPrimaryLanguage() { return primaryLanguage; }
    public void setPrimaryLanguage(String primaryLanguage) { this.primaryLanguage = primaryLanguage; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getTopics() { return topics; }
    public void setTopics(List<String> topics) { this.topics = topics; }
    public int getForkCount() { return forkCount; }
    public void setForkCount(int forkCount) { this.forkCount = forkCount; }
    public int getWatchersCount() { return watchersCount; }
    public void setWatchersCount(int watchersCount) { this.watchersCount = watchersCount; }
    public int getOpenIssuesCount() { return openIssuesCount; }
    public void setOpenIssuesCount(int openIssuesCount) { this.openIssuesCount = openIssuesCount; }
    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }
    public String getDefaultBranch() { return defaultBranch; }
    public void setDefaultBranch(String defaultBranch) { this.defaultBranch = defaultBranch; }
    public Map<String, Long> getLanguages() { return languages; }
    public void setLanguages(Map<String, Long> languages) { this.languages = languages; }
    public boolean isHasWiki() { return hasWiki; }
    public void setHasWiki(boolean hasWiki) { this.hasWiki = hasWiki; }
    public boolean isHasDiscussions() { return hasDiscussions; }
    public void setHasDiscussions(boolean hasDiscussions) { this.hasDiscussions = hasDiscussions; }
    public LocalDateTime getCreatedAtGitHub() { return createdAtGitHub; }
    public void setCreatedAtGitHub(LocalDateTime createdAtGitHub) { this.createdAtGitHub = createdAtGitHub; }
    public LocalDateTime getLastPushedAt() { return lastPushedAt; }
    public void setLastPushedAt(LocalDateTime lastPushedAt) { this.lastPushedAt = lastPushedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getLastAnalyzedAt() { return lastAnalyzedAt; }
    public void setLastAnalyzedAt(LocalDateTime lastAnalyzedAt) { this.lastAnalyzedAt = lastAnalyzedAt; }
}
