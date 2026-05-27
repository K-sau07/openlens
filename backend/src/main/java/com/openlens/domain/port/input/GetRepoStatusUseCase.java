package com.openlens.domain.port.input;

public interface GetRepoStatusUseCase {

    RepoStatusOutput getStatus(String repoUrl);

    record RepoStatusOutput(
            String status,
            Long repoId
    ) {}
}
