package com.openlens.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    void isReady_returnsTrueWhenStatusReady() {
        Repository repo = Repository.builder()
                .url("https://github.com/test/repo").owner("test").name("repo")
                .status(RepositoryStatus.READY)
                .build();
        assertTrue(repo.isReady());
    }

    @Test
    void isReady_returnsFalseWhenPending() {
        Repository repo = Repository.builder()
                .url("https://github.com/test/repo").owner("test").name("repo")
                .status(RepositoryStatus.PENDING)
                .build();
        assertFalse(repo.isReady());
    }

    @Test
    void needsAnalysis_returnsTrueWhenNeverAnalyzed() {
        Repository repo = Repository.builder()
                .url("https://github.com/test/repo").owner("test").name("repo")
                .status(RepositoryStatus.READY)
                .build();
        assertTrue(repo.needsAnalysis());
    }

    @Test
    void needsAnalysis_returnsTrueWhenAnalyzedOver24HoursAgo() {
        Repository repo = Repository.builder()
                .url("https://github.com/test/repo").owner("test").name("repo")
                .status(RepositoryStatus.READY)
                .lastAnalyzedAt(LocalDateTime.now().minusHours(25))
                .build();
        assertTrue(repo.needsAnalysis());
    }

    @Test
    void needsAnalysis_returnsFalseWhenRecentlyAnalyzed() {
        Repository repo = Repository.builder()
                .url("https://github.com/test/repo").owner("test").name("repo")
                .status(RepositoryStatus.READY)
                .lastAnalyzedAt(LocalDateTime.now().minusHours(1))
                .build();
        assertFalse(repo.needsAnalysis());
    }

    @Test
    void markReady_setsStatusAndTimestamp() {
        Repository repo = Repository.builder()
                .url("https://github.com/test/repo").owner("test").name("repo")
                .status(RepositoryStatus.PENDING)
                .build();

        repo.markReady();

        assertEquals(RepositoryStatus.READY, repo.getStatus());
        assertNotNull(repo.getLastAnalyzedAt());
    }

    @Test
    void markFailed_setsStatus() {
        Repository repo = Repository.builder()
                .url("https://github.com/test/repo").owner("test").name("repo")
                .status(RepositoryStatus.INGESTING)
                .build();

        repo.markFailed();

        assertEquals(RepositoryStatus.FAILED, repo.getStatus());
    }

    @Test
    void builder_defaultsToEmptyCollections() {
        Repository repo = Repository.builder()
                .url("https://github.com/test/repo").owner("test").name("repo")
                .status(RepositoryStatus.PENDING)
                .build();

        assertNotNull(repo.getTopics());
        assertTrue(repo.getTopics().isEmpty());
        assertNotNull(repo.getLanguages());
        assertTrue(repo.getLanguages().isEmpty());
    }
}
