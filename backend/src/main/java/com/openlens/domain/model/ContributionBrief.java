package com.openlens.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class ContributionBrief {

    private final Long id;
    private final Long repoId;
    private final Long issueId;
    private final SkillLevel skillLevel;
    private final List<String> filesToTouch;
    private final Integer similarPrNumber;
    private final String maintainerNotes;
    private final String rawBrief;
    private final LocalDateTime generatedAt;

    private ContributionBrief(Builder builder) {
        this.id = builder.id;
        this.repoId = builder.repoId;
        this.issueId = builder.issueId;
        this.skillLevel = builder.skillLevel;
        this.filesToTouch = builder.filesToTouch;
        this.similarPrNumber = builder.similarPrNumber;
        this.maintainerNotes = builder.maintainerNotes;
        this.rawBrief = builder.rawBrief;
        this.generatedAt = builder.generatedAt;
    }

    public Long getId() { return id; }
    public Long getRepoId() { return repoId; }
    public Long getIssueId() { return issueId; }
    public SkillLevel getSkillLevel() { return skillLevel; }
    public List<String> getFilesToTouch() { return filesToTouch; }
    public Integer getSimilarPrNumber() { return similarPrNumber; }
    public String getMaintainerNotes() { return maintainerNotes; }
    public String getRawBrief() { return rawBrief; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long repoId;
        private Long issueId;
        private SkillLevel skillLevel;
        private List<String> filesToTouch;
        private Integer similarPrNumber;
        private String maintainerNotes;
        private String rawBrief;
        private LocalDateTime generatedAt = LocalDateTime.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder repoId(Long repoId) { this.repoId = repoId; return this; }
        public Builder issueId(Long issueId) { this.issueId = issueId; return this; }
        public Builder skillLevel(SkillLevel skillLevel) { this.skillLevel = skillLevel; return this; }
        public Builder filesToTouch(List<String> filesToTouch) { this.filesToTouch = filesToTouch; return this; }
        public Builder similarPrNumber(Integer similarPrNumber) { this.similarPrNumber = similarPrNumber; return this; }
        public Builder maintainerNotes(String maintainerNotes) { this.maintainerNotes = maintainerNotes; return this; }
        public Builder rawBrief(String rawBrief) { this.rawBrief = rawBrief; return this; }
        public Builder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }

        public ContributionBrief build() {
            return new ContributionBrief(this);
        }
    }
}
