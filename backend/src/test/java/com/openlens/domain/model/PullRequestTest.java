package com.openlens.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PullRequestTest {

    @Test
    void isSmallChange_returnsTrueForSmallPr() {
        PullRequest pr = PullRequest.builder()
                .number(1).title("fix typo")
                .filesChanged(2).linesAdded(10).linesRemoved(5)
                .build();
        assertTrue(pr.isSmallChange());
    }

    @Test
    void isSmallChange_returnsFalseWhenTooManyFiles() {
        PullRequest pr = PullRequest.builder()
                .number(1).title("refactor")
                .filesChanged(4).linesAdded(10).linesRemoved(5)
                .build();
        assertFalse(pr.isSmallChange());
    }

    @Test
    void isSmallChange_returnsFalseWhenTooManyLines() {
        PullRequest pr = PullRequest.builder()
                .number(1).title("big change")
                .filesChanged(2).linesAdded(30).linesRemoved(25)
                .build();
        assertFalse(pr.isSmallChange());
    }

    @Test
    void isMediumChange_returnsTrueForMediumPr() {
        PullRequest pr = PullRequest.builder()
                .number(1).title("add feature")
                .filesChanged(6).linesAdded(80).linesRemoved(40)
                .build();
        assertTrue(pr.isMediumChange());
    }

    @Test
    void isMediumChange_returnsFalseForLargePr() {
        PullRequest pr = PullRequest.builder()
                .number(1).title("massive refactor")
                .filesChanged(15).linesAdded(500).linesRemoved(300)
                .build();
        assertFalse(pr.isMediumChange());
    }

    @Test
    void smallChangeIsAlsoMedium() {
        PullRequest pr = PullRequest.builder()
                .number(1).title("tiny fix")
                .filesChanged(1).linesAdded(5).linesRemoved(2)
                .build();
        assertTrue(pr.isSmallChange());
        assertTrue(pr.isMediumChange());
    }
}
