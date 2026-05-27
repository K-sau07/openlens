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

    private Issue(Builder builder) {
        this.id = builder.id;
        this.repoId = builder.repoId;
        this.number = builder.number;
        this.title = builder.title;
        this.body = builder.body;
        this.labels = builder.labels != null ? builder.labels : List.of();
        this.state = builder.state;
        this.complexityScore = builder.complexityScore;
        this.commentCount = builder.commentCount;
        this.author = builder.author;
        this.assignee = builder.assignee;
        this.reactionsCount = builder.reactionsCount;
        this.githubCreatedAt = builder.githubCreatedAt;
        this.githubUpdatedAt = builder.githubUpdatedAt;
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

    public Builder toBuilder() {
        return new Builder()
                .id(id).repoId(repoId).number(number).title(title).body(body)
                .labels(labels).state(state).complexityScore(complexityScore)
                .commentCount(commentCount).author(author).assignee(assignee)
                .reactionsCount(reactionsCount).githubCreatedAt(githubCreatedAt)
                .githubUpdatedAt(githubUpdatedAt);
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {
        private Long id;
        private Long repoId;
        private int number;
        private String title;
        private String body;
        private List<String> labels;
        private String state;
        private Integer complexityScore;
        private int commentCount;
        private String author;
        private String assignee;
        private int reactionsCount;
        private LocalDateTime githubCreatedAt;
        private LocalDateTime githubUpdatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder repoId(Long repoId) { this.repoId = repoId; return this; }
        public Builder number(int number) { this.number = number; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder body(String body) { this.body = body; return this; }
        public Builder labels(List<String> labels) { this.labels = labels; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder complexityScore(Integer complexityScore) { this.complexityScore = complexityScore; return this; }
        public Builder commentCount(int commentCount) { this.commentCount = commentCount; return this; }
        public Builder author(String author) { this.author = author; return this; }
        public Builder assignee(String assignee) { this.assignee = assignee; return this; }
        public Builder reactionsCount(int reactionsCount) { this.reactionsCount = reactionsCount; return this; }
        public Builder githubCreatedAt(LocalDateTime githubCreatedAt) { this.githubCreatedAt = githubCreatedAt; return this; }
        public Builder githubUpdatedAt(LocalDateTime githubUpdatedAt) { this.githubUpdatedAt = githubUpdatedAt; return this; }

        public Issue build() {
            return new Issue(this);
        }
    }
}
