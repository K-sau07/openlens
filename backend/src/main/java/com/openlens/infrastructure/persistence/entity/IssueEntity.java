package com.openlens.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    @Column(nullable = false, length = 512)
    private String title;

    @Column(columnDefinition = "text")
    private String body;

    // stored as comma-separated string — simple and queryable enough for now
    @Column
    private String labels;

    @Column(nullable = false, length = 20)
    private String state;

    @Column(name = "complexity_score")
    private Integer complexityScore;

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
    public String getLabels() { return labels; }
    public void setLabels(String labels) { this.labels = labels; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public Integer getComplexityScore() { return complexityScore; }
    public void setComplexityScore(Integer complexityScore) { this.complexityScore = complexityScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
