package com.openlens.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pull_requests")
public class PullRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repo_id", nullable = false)
    private Long repoId;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(name = "files_changed", nullable = false)
    private int filesChanged;

    @Column(name = "lines_added", nullable = false)
    private int linesAdded;

    @Column(name = "lines_removed", nullable = false)
    private int linesRemoved;

    @Column(name = "merge_time_hours")
    private Integer mergeTimeHours;

    @Column(name = "linked_issue_number")
    private Integer linkedIssueNumber;

    @Column(length = 128)
    private String author;

    @Column(name = "merged_at")
    private LocalDateTime mergedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRepoId() { return repoId; }
    public void setRepoId(Long repoId) { this.repoId = repoId; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getFilesChanged() { return filesChanged; }
    public void setFilesChanged(int filesChanged) { this.filesChanged = filesChanged; }
    public int getLinesAdded() { return linesAdded; }
    public void setLinesAdded(int linesAdded) { this.linesAdded = linesAdded; }
    public int getLinesRemoved() { return linesRemoved; }
    public void setLinesRemoved(int linesRemoved) { this.linesRemoved = linesRemoved; }
    public Integer getMergeTimeHours() { return mergeTimeHours; }
    public void setMergeTimeHours(Integer mergeTimeHours) { this.mergeTimeHours = mergeTimeHours; }
    public Integer getLinkedIssueNumber() { return linkedIssueNumber; }
    public void setLinkedIssueNumber(Integer linkedIssueNumber) { this.linkedIssueNumber = linkedIssueNumber; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDateTime getMergedAt() { return mergedAt; }
    public void setMergedAt(LocalDateTime mergedAt) { this.mergedAt = mergedAt; }
}
