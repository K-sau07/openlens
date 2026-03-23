package com.openlens.domain.port.output;

// implemented by the Kafka adapter in infrastructure
public interface IngestionJobPort {

    void publishIngestionRequest(String repoUrl, String owner, String repoName);
}
