package com.openlens.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "issues")
public class IssueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repo_id", nullable = false)
    private Long repoId;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false, length = 1024)
    private String title;

    @Column(columnDefinition = "text")
    private String body;

    @Column(columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> labels;

    @Column(nullable = false, length = 20)
    private String state;

    @Column(name = "complexity_score")
    private Integer complexityScore;

    @Column(name = "comment_count")
    private int commentCount;

    private String author;

    private String assignee;

    @Column(name = "reactions_count")
    private int reactionsCount;

    @Column(name = "github_created_at")
    private LocalDateTime githubCreatedAt;

    @Column(name = "github_updated_at")
    private LocalDateTime githubUpdatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRepoId() { return repoId; }
    public void setRepoId(Long repoId) { this.repoId = repoId; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public Integer getComplexityScore() { return complexityScore; }
    public void setComplexityScore(Integer complexityScore) { this.complexityScore = complexityScore; }
    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public int getReactionsCount() { return reactionsCount; }
    public void setReactionsCount(int reactionsCount) { this.reactionsCount = reactionsCount; }
    public LocalDateTime getGithubCreatedAt() { return githubCreatedAt; }
    public void setGithubCreatedAt(LocalDateTime githubCreatedAt) { this.githubCreatedAt = githubCreatedAt; }
    public LocalDateTime getGithubUpdatedAt() { return githubUpdatedAt; }
    public void setGithubUpdatedAt(LocalDateTime githubUpdatedAt) { this.githubUpdatedAt = githubUpdatedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
