package com.openlens.infrastructure.kafka.producer;

import com.openlens.domain.port.output.IngestionJobPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IngestionJobProducer implements IngestionJobPort {

    private static final Logger log = LoggerFactory.getLogger(IngestionJobProducer.class);
    private static final String TOPIC = "repo.ingestion.requested";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public IngestionJobProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishIngestionRequest(String repoUrl, String owner, String repoName) {
        Map<String, String> payload = Map.of(
                "repoUrl", repoUrl,
                "owner", owner,
                "repoName", repoName
        );

        kafkaTemplate.send(TOPIC, repoUrl, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("failed to publish ingestion request for {}", repoUrl, ex);
                    } else {
                        log.debug("published ingestion request for {} to partition {}",
                                repoUrl, result.getRecordMetadata().partition());
                    }
                });
    }
}
