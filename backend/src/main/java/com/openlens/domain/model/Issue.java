package com.openlens.domain.model;

import java.util.List;

public class Issue {

    private final Long id;
    private final Long repoId;
    private final int number;
    private final String title;
    private final String body;
    private final List<String> labels;
    private final String state;
    private Integer complexityScore;

    public Issue(Long id, Long repoId, int number, String title, String body,
                 List<String> labels, String state, Integer complexityScore) {
        this.id = id;
        this.repoId = repoId;
        this.number = number;
        this.title = title;
        this.body = body;
        this.labels = labels;
        this.state = state;
        this.complexityScore = complexityScore;
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

    public Long getId() { return id; }
    public Long getRepoId() { return repoId; }
    public int getNumber() { return number; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public List<String> getLabels() { return labels; }
    public String getState() { return state; }
    public Integer getComplexityScore() { return complexityScore; }
}
