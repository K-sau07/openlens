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

    private PullRequest(Builder builder) {
        this.id = builder.id;
        this.repoId = builder.repoId;
        this.number = builder.number;
        this.title = builder.title;
        this.filesChanged = builder.filesChanged;
        this.linesAdded = builder.linesAdded;
        this.linesRemoved = builder.linesRemoved;
        this.mergeTimeHours = builder.mergeTimeHours;
        this.linkedIssueNumber = builder.linkedIssueNumber;
        this.author = builder.author;
        this.mergedAt = builder.mergedAt;
    }

    public boolean isSmallChange() {
        return filesChanged <= 3 && (linesAdded + linesRemoved) <= 50;
    }

    public boolean isMediumChange() {
        return filesChanged <= 8 && (linesAdded + linesRemoved) <= 200;
    }

    public Builder toBuilder() {
        return new Builder()
                .id(id).repoId(repoId).number(number).title(title)
                .filesChanged(filesChanged).linesAdded(linesAdded).linesRemoved(linesRemoved)
                .mergeTimeHours(mergeTimeHours).linkedIssueNumber(linkedIssueNumber)
                .author(author).mergedAt(mergedAt);
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {
        private Long id;
        private Long repoId;
        private int number;
        private String title;
        private int filesChanged;
        private int linesAdded;
        private int linesRemoved;
        private Integer mergeTimeHours;
        private Integer linkedIssueNumber;
        private String author;
        private LocalDateTime mergedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder repoId(Long repoId) { this.repoId = repoId; return this; }
        public Builder number(int number) { this.number = number; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder filesChanged(int filesChanged) { this.filesChanged = filesChanged; return this; }
        public Builder linesAdded(int linesAdded) { this.linesAdded = linesAdded; return this; }
        public Builder linesRemoved(int linesRemoved) { this.linesRemoved = linesRemoved; return this; }
        public Builder mergeTimeHours(Integer mergeTimeHours) { this.mergeTimeHours = mergeTimeHours; return this; }
        public Builder linkedIssueNumber(Integer linkedIssueNumber) { this.linkedIssueNumber = linkedIssueNumber; return this; }
        public Builder author(String author) { this.author = author; return this; }
        public Builder mergedAt(LocalDateTime mergedAt) { this.mergedAt = mergedAt; return this; }

        public PullRequest build() {
            return new PullRequest(this);
        }
    }
}
