package com.openlens.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IssueTest {

    @Test
    void isOpen_returnsTrueForOpenState() {
        Issue issue = Issue.builder().state("open").number(1).title("test").build();
        assertTrue(issue.isOpen());
    }

    @Test
    void isOpen_returnsFalseForClosedState() {
        Issue issue = Issue.builder().state("closed").number(1).title("test").build();
        assertFalse(issue.isOpen());
    }

    @Test
    void isOpen_isCaseInsensitive() {
        Issue issue = Issue.builder().state("Open").number(1).title("test").build();
        assertTrue(issue.isOpen());
    }

    @Test
    void isHighDemand_returnsTrueWhenReactionsAtThreshold() {
        Issue issue = Issue.builder().reactionsCount(5).number(1).title("test").build();
        assertTrue(issue.isHighDemand());
    }

    @Test
    void isHighDemand_returnsFalseWhenReactionsBelowThreshold() {
        Issue issue = Issue.builder().reactionsCount(4).number(1).title("test").build();
        assertFalse(issue.isHighDemand());
    }

    @Test
    void isStale_returnsTrueWhenNotUpdatedInSixMonths() {
        Issue issue = Issue.builder()
                .number(1).title("test")
                .githubUpdatedAt(LocalDateTime.now().minusMonths(7))
                .build();
        assertTrue(issue.isStale());
    }

    @Test
    void isStale_returnsFalseWhenRecentlyUpdated() {
        Issue issue = Issue.builder()
                .number(1).title("test")
                .githubUpdatedAt(LocalDateTime.now().minusDays(10))
                .build();
        assertFalse(issue.isStale());
    }

    @Test
    void isStale_returnsFalseWhenNoUpdateTimestamp() {
        Issue issue = Issue.builder().number(1).title("test").build();
        assertFalse(issue.isStale());
    }

    @Test
    void isAssigned_returnsTrueWhenAssigneePresent() {
        Issue issue = Issue.builder().number(1).title("test").assignee("dev123").build();
        assertTrue(issue.isAssigned());
    }

    @Test
    void isAssigned_returnsFalseWhenAssigneeNull() {
        Issue issue = Issue.builder().number(1).title("test").build();
        assertFalse(issue.isAssigned());
    }

    @Test
    void isAssigned_returnsFalseWhenAssigneeBlank() {
        Issue issue = Issue.builder().number(1).title("test").assignee("  ").build();
        assertFalse(issue.isAssigned());
    }

    @Test
    void labelsDefaultToEmptyList() {
        Issue issue = Issue.builder().number(1).title("test").build();
        assertNotNull(issue.getLabels());
        assertTrue(issue.getLabels().isEmpty());
    }

    @Test
    void toBuilder_preservesAllFields() {
        Issue original = Issue.builder()
                .id(1L).repoId(2L).number(10).title("fix bug")
                .body("details").labels(List.of("bug")).state("open")
                .commentCount(3).author("dev").assignee("reviewer")
                .reactionsCount(5)
                .githubCreatedAt(LocalDateTime.of(2025, 1, 1, 0, 0))
                .githubUpdatedAt(LocalDateTime.of(2025, 6, 1, 0, 0))
                .build();

        Issue copy = original.toBuilder().build();

        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getRepoId(), copy.getRepoId());
        assertEquals(original.getNumber(), copy.getNumber());
        assertEquals(original.getTitle(), copy.getTitle());
        assertEquals(original.getBody(), copy.getBody());
        assertEquals(original.getLabels(), copy.getLabels());
        assertEquals(original.getState(), copy.getState());
        assertEquals(original.getAuthor(), copy.getAuthor());
    }
}
