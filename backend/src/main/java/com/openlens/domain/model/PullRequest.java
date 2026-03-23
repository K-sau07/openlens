package com.openlens.domain.model;

import java.time.LocalDateTime;

public class PullRequest {

    private final Long id;
    private final Long repoId;
    private final int number;
    private final String title;
    private final int filesChanged;
    private final int linesAdded;
    private final int linesRemoved;
    private final Integer mergeTimeHours;
    private final Integer linkedIssueNumber;
    private final String author;
    private final LocalDateTime mergedAt;

    public PullRequest(Long id, Long repoId, int number, String title,
                       int filesChanged, int linesAdded, int linesRemoved,
                       Integer mergeTimeHours, Integer linkedIssueNumber,
                       String author, LocalDateTime mergedAt) {
        this.id = id;
        this.repoId = repoId;
        this.number = number;
        this.title = title;
        this.filesChanged = filesChanged;
        this.linesAdded = linesAdded;
        this.linesRemoved = linesRemoved;
        this.mergeTimeHours = mergeTimeHours;
        this.linkedIssueNumber = linkedIssueNumber;
        this.author = author;
        this.mergedAt = mergedAt;
    }

    // a simple heuristic — not perfect but good enough for complexity matching
    public boolean isSmallChange() {
        return filesChanged <= 3 && (linesAdded + linesRemoved) <= 50;
    }

    public boolean isMediumChange() {
        return filesChanged <= 8 && (linesAdded + linesRemoved) <= 200;
    }

    public Long getId() { return id; }
    public Long getRepoId() { return repoId; }
    public int getNumber() { return number; }
    public String getTitle() { return title; }
    public int getFilesChanged() { return filesChanged; }
    public int getLinesAdded() { return linesAdded; }
    public int getLinesRemoved() { return linesRemoved; }
    public Integer getMergeTimeHours() { return mergeTimeHours; }
    public Integer getLinkedIssueNumber() { return linkedIssueNumber; }
    public String getAuthor() { return author; }
    public LocalDateTime getMergedAt() { return mergedAt; }
}
