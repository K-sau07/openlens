package com.openlens.infrastructure.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openlens.domain.port.output.GuideCachePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Component
public class GuideCacheAdapter implements GuideCachePort {

    private static final Logger log = LoggerFactory.getLogger(GuideCacheAdapter.class);
    private static final String KEY_PREFIX = "guide:";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final Duration ttl;

    public GuideCacheAdapter(StringRedisTemplate redis, ObjectMapper objectMapper,
                             @Value("${openlens.cache.brief-ttl-hours:6}") int ttlHours) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.ttl = Duration.ofHours(ttlHours);
    }

    @Override
    public Optional<Map<String, Object>> get(Long repoId, Long issueId) {
        String key = buildKey(repoId, issueId);
        try {
            String json = redis.opsForValue().get(key);
            if (json == null) {
                log.debug("guide cache miss for repo={} issue={}", repoId, issueId);
                return Optional.empty();
            }
            Map<String, Object> guide = objectMapper.readValue(json, new TypeReference<>() {});
            log.debug("guide cache hit for repo={} issue={}", repoId, issueId);
            return Optional.of(guide);
        } catch (Exception e) {
            log.warn("failed to read guide cache for repo={} issue={} — {}", repoId, issueId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void put(Long repoId, Long issueId, Map<String, Object> guide) {
        String key = buildKey(repoId, issueId);
        try {
            String json = objectMapper.writeValueAsString(guide);
            redis.opsForValue().set(key, json, ttl);
            log.debug("cached guide for repo={} issue={} with ttl={}h", repoId, issueId, ttl.toHours());
        } catch (Exception e) {
            log.warn("failed to cache guide for repo={} issue={} — {}", repoId, issueId, e.getMessage());
        }
    }

    @Override
    public void evictByRepo(Long repoId) {
        try {
            var keys = redis.keys(KEY_PREFIX + repoId + ":*");
            if (keys != null && !keys.isEmpty()) {
                redis.delete(keys);
                log.debug("evicted {} guide cache entries for repo={}", keys.size(), repoId);
            }
        } catch (Exception e) {
            log.warn("failed to evict guide cache for repo={} — {}", repoId, e.getMessage());
        }
    }

    private String buildKey(Long repoId, Long issueId) {
        return KEY_PREFIX + repoId + ":" + issueId;
    }
}
