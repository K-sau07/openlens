package com.openlens.infrastructure.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openlens.domain.model.ContributionBrief;
import com.openlens.domain.model.SkillLevel;
import com.openlens.domain.port.output.BriefCachePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
public class BriefCacheAdapter implements BriefCachePort {

    private static final Logger log = LoggerFactory.getLogger(BriefCacheAdapter.class);
    private static final String KEY_PREFIX = "brief:";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final Duration ttl;

    public BriefCacheAdapter(StringRedisTemplate redis, ObjectMapper objectMapper,
                             @Value("${openlens.cache.brief-ttl-hours:6}") int ttlHours) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.ttl = Duration.ofHours(ttlHours);
    }

    @Override
    public Optional<List<ContributionBrief>> get(String repoUrl, SkillLevel skillLevel) {
        String key = buildKey(repoUrl, skillLevel);
        try {
            String json = redis.opsForValue().get(key);
            if (json == null) {
                log.debug("cache miss for key={}", key);
                return Optional.empty();
            }
            List<ContributionBrief> briefs = objectMapper.readValue(json, new TypeReference<>() {});
            log.debug("cache hit for key={}, entries={}", key, briefs.size());
            return Optional.of(briefs);
        } catch (Exception e) {
            log.warn("failed to read from cache key={} — {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void put(String repoUrl, SkillLevel skillLevel, List<ContributionBrief> briefs) {
        String key = buildKey(repoUrl, skillLevel);
        try {
            String json = objectMapper.writeValueAsString(briefs);
            redis.opsForValue().set(key, json, ttl);
            log.debug("cached {} briefs at key={} with ttl={}h", briefs.size(), key, ttl.toHours());
        } catch (Exception e) {
            log.warn("failed to write to cache key={} — {}", key, e.getMessage());
        }
    }

    @Override
    public void evict(String repoUrl) {
        try {
            var keys = redis.keys(KEY_PREFIX + normalize(repoUrl) + ":*");
            if (keys != null && !keys.isEmpty()) {
                redis.delete(keys);
                log.debug("evicted {} cache entries for repo={}", keys.size(), repoUrl);
            }
        } catch (Exception e) {
            log.warn("failed to evict cache for repo={} — {}", repoUrl, e.getMessage());
        }
    }

    private String buildKey(String repoUrl, SkillLevel skillLevel) {
        return KEY_PREFIX + normalize(repoUrl) + ":" + skillLevel.name().toLowerCase();
    }

    // strips protocol and trailing slashes for consistent cache keys
    private String normalize(String repoUrl) {
        return repoUrl.replaceAll("https?://", "")
                .replaceAll("/$", "")
                .replace("/", ":");
    }
}
