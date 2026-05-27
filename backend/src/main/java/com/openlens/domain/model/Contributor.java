package com.openlens.domain.model;

public class Contributor {

    private final Long id;
    private final Long repoId;
    private final String username;
    private final int totalReviews;
    private final Integer avgResponseHours;
    private final String reviewStyle;

    private Contributor(Builder builder) {
        this.id = builder.id;
        this.repoId = builder.repoId;
        this.username = builder.username;
        this.totalReviews = builder.totalReviews;
        this.avgResponseHours = builder.avgResponseHours;
        this.reviewStyle = builder.reviewStyle;
    }

    public boolean isActiveMaintainer() {
        return totalReviews >= 10;
    }

    public Builder toBuilder() {
        return new Builder()
                .id(id).repoId(repoId).username(username)
                .totalReviews(totalReviews).avgResponseHours(avgResponseHours)
                .reviewStyle(reviewStyle);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() { return id; }
    public Long getRepoId() { return repoId; }
    public String getUsername() { return username; }
    public int getTotalReviews() { return totalReviews; }
    public Integer getAvgResponseHours() { return avgResponseHours; }
    public String getReviewStyle() { return reviewStyle; }

    public static class Builder {
        private Long id;
        private Long repoId;
        private String username;
        private int totalReviews;
        private Integer avgResponseHours;
        private String reviewStyle;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder repoId(Long repoId) { this.repoId = repoId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder totalReviews(int totalReviews) { this.totalReviews = totalReviews; return this; }
        public Builder avgResponseHours(Integer avgResponseHours) { this.avgResponseHours = avgResponseHours; return this; }
        public Builder reviewStyle(String reviewStyle) { this.reviewStyle = reviewStyle; return this; }

        public Contributor build() {
            return new Contributor(this);
        }
    }
}
