package com.openlens.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Issue {

    private final Long id;
    private final Long repoId;
    private final int number;
    private final String title;
    private final String body;
    private final List<String> labels;
    private final String state;
    private final int commentCount;
    private final String author;
    private final String assignee;
    private final int reactionsCount;
    private final LocalDateTime githubCreatedAt;
    private final LocalDateTime githubUpdatedAt;
    private Integer complexityScore;

    public Issue(Long id, Long repoId, int number, String title, String body,
                 List<String> labels, String state, Integer complexityScore) {
        this(id, repoId, number, title, body, labels, state, complexityScore,
                0, null, null, 0, null, null);
    }

    public Issue(Long id, Long repoId, int number, String title, String body,
                 List<String> labels, String state, Integer complexityScore,
                 int commentCount, String author, String assignee,
                 int reactionsCount, LocalDateTime githubCreatedAt,
                 LocalDateTime githubUpdatedAt) {
        this.id = id;
        this.repoId = repoId;
        this.number = number;
        this.title = title;
        this.body = body;
        this.labels = labels;
        this.state = state;
        this.complexityScore = complexityScore;
        this.commentCount = commentCount;
        this.author = author;
        this.assignee = assignee;
        this.reactionsCount = reactionsCount;
        this.githubCreatedAt = githubCreatedAt;
        this.githubUpdatedAt = githubUpdatedAt;
    }

    public boolean isOpen() {
        return "open".equalsIgnoreCase(state);
    }

    public boolean hasComplexityScore() {
        return complexityScore != null;
    }

    public void assignComplexityScore(int score) {
        this.complexityScore = score;
    }

    public boolean isAssigned() {
        return assignee != null && !assignee.isBlank();
    }

    public boolean isHighDemand() {
        return reactionsCount >= 5;
    }

    public boolean isStale() {
        if (githubUpdatedAt == null) return false;
        return githubUpdatedAt.isBefore(LocalDateTime.now().minusMonths(6));
    }

    public Long getId() { return id; }
    public Long getRepoId() { return repoId; }
    public int getNumber() { return number; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public List<String> getLabels() { return labels; }
    public String getState() { return state; }
    public Integer getComplexityScore() { return complexityScore; }
    public int getCommentCount() { return commentCount; }
    public String getAuthor() { return author; }
    public String getAssignee() { return assignee; }
    public int getReactionsCount() { return reactionsCount; }
    public LocalDateTime getGithubCreatedAt() { return githubCreatedAt; }
    public LocalDateTime getGithubUpdatedAt() { return githubUpdatedAt; }
}
