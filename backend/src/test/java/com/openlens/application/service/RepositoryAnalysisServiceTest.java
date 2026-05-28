package com.openlens.application.service;

import com.openlens.domain.exception.ResourceNotFoundException;
import com.openlens.domain.model.Repository;
import com.openlens.domain.model.RepositoryStatus;
import com.openlens.domain.model.SkillLevel;
import com.openlens.domain.port.input.AnalyzeRepositoryUseCase.AnalysisResponse;
import com.openlens.domain.port.input.GetRepoStatusUseCase.RepoStatusOutput;
import com.openlens.domain.port.output.IngestionJobPort;
import com.openlens.domain.port.output.RepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryAnalysisServiceTest {

    @Mock private RepositoryPort repositoryPort;
    @Mock private IngestionJobPort ingestionJobPort;

    @InjectMocks private RepositoryAnalysisService service;

    @Test
    void analyze_skipsIngestionWhenRepoFreshAndReady() {
        Repository repo = Repository.builder()
                .id(1L).url("https://github.com/test/repo").owner("test").name("repo")
                .status(RepositoryStatus.READY)
                .lastAnalyzedAt(LocalDateTime.now().minusHours(1))
                .build();

        when(repositoryPort.findByUrl("https://github.com/test/repo")).thenReturn(Optional.of(repo));

        AnalysisResponse result = service.analyze("https://github.com/test/repo", SkillLevel.BEGINNER);

        assertEquals("READY", result.status());
        verify(ingestionJobPort, never()).publishIngestionRequest(any(), any(), any());
    }

    @Test
    void analyze_publishesIngestionForNewRepo() {
        when(repositoryPort.findByUrl("https://github.com/test/repo")).thenReturn(Optional.empty());

        AnalysisResponse result = service.analyze("https://github.com/test/repo", SkillLevel.BEGINNER);

        assertEquals("PROCESSING", result.status());
        assertNotNull(result.jobId());
        verify(repositoryPort).save(any());
        verify(ingestionJobPort).publishIngestionRequest(
                eq("https://github.com/test/repo"), eq("test"), eq("repo"));
    }

    @Test
    void analyze_throwsForInvalidUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> service.analyze("not-a-url", SkillLevel.BEGINNER));
    }

    @Test
    void getStatus_returnsProcessingForIngestingRepo() {
        Repository repo = Repository.builder()
                .id(1L).url("https://github.com/test/repo").owner("test").name("repo")
                .status(RepositoryStatus.INGESTING)
                .build();

        when(repositoryPort.findByUrl("https://github.com/test/repo")).thenReturn(Optional.of(repo));

        RepoStatusOutput result = service.getStatus("https://github.com/test/repo");

        assertEquals("PROCESSING", result.status());
        assertEquals(1L, result.repoId());
    }

    @Test
    void getStatus_returnsPendingForUnknownRepo() {
        when(repositoryPort.findByUrl("https://github.com/test/repo")).thenReturn(Optional.empty());

        RepoStatusOutput result = service.getStatus("https://github.com/test/repo");

        assertEquals("PENDING", result.status());
        assertNull(result.repoId());
    }

    @Test
    void getProfile_throwsWhenRepoNotFound() {
        when(repositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getProfile(99L));
    }
}
