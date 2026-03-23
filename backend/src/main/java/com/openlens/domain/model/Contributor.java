package com.openlens.domain.model;

public class Contributor {

    private final Long id;
    private final Long repoId;
    private final String username;
    private final int totalReviews;
    private final Integer avgResponseHours;
    private final String reviewStyle;

    public Contributor(Long id, Long repoId, String username, int totalReviews,
                       Integer avgResponseHours, String reviewStyle) {
        this.id = id;
        this.repoId = repoId;
        this.username = username;
        this.totalReviews = totalReviews;
        this.avgResponseHours = avgResponseHours;
        this.reviewStyle = reviewStyle;
    }

    public boolean isActiveMaintainer() {
        return totalReviews >= 10;
    }

    public Long getId() { return id; }
    public Long getRepoId() { return repoId; }
    public String getUsername() { return username; }
    public int getTotalReviews() { return totalReviews; }
    public Integer getAvgResponseHours() { return avgResponseHours; }
    public String getReviewStyle() { return reviewStyle; }
}
