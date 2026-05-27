package com.openlens.infrastructure.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openlens.domain.model.Contributor;
import com.openlens.domain.model.Issue;
import com.openlens.domain.model.PullRequest;
import com.openlens.domain.port.output.GitHubDataPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;

@Component
public class GitHubApiClient implements GitHubDataPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubApiClient.class);
    private static final String BASE_URL = "https://api.github.com";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String token;

    public GitHubApiClient(
            ObjectMapper objectMapper,
            @Value("${github.api.token}") String token) {
        this.objectMapper = objectMapper;
        this.token = token;
        this.httpClient = HttpClient.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();
    }

    @Override
    public RepoMetadata fetchRepoMetadata(String owner, String repoName) {
        log.debug("fetching repo metadata for {}/{}", owner, repoName);
        String url = BASE_URL + "/repos/" + owner + "/" + repoName;
        try {
            JsonNode node = get(url);

            String description = textOrNull(node, "description");

            List<String> topics = new ArrayList<>();
            if (node.has("topics") && node.get("topics").isArray()) {
                node.get("topics").forEach(t -> topics.add(t.asText()));
            }

            String language = textOrNull(node, "language");
            if (language == null) language = "unknown";

            int stars = node.path("stargazers_count").asInt(0);
            int forks = node.path("forks_count").asInt(0);
            int watchers = node.path("subscribers_count").asInt(0);
            int openIssues = node.path("open_issues_count").asInt(0);

            String license = null;
            JsonNode licenseNode = node.get("license");
            if (licenseNode != null && !licenseNode.isNull() && licenseNode.has("spdx_id")) {
                license = licenseNode.get("spdx_id").asText();
                if ("NOASSERTION".equals(license)) license = null;
            }

            String defaultBranch = node.path("default_branch").asText("main");
            boolean hasWiki = node.path("has_wiki").asBoolean(false);
            boolean hasDiscussions = node.path("has_discussions").asBoolean(false);

            LocalDateTime createdAt = parseTimestamp(node, "created_at");
            LocalDateTime pushedAt = parseTimestamp(node, "pushed_at");

            return new RepoMetadata(description, topics, language, stars, forks,
                    watchers, openIssues, license, defaultBranch, hasWiki,
                    hasDiscussions, createdAt, pushedAt);

        } catch (Exception e) {
            log.error("failed to fetch metadata for {}/{}", owner, repoName, e);
            return new RepoMetadata(null, List.of(), "unknown", 0, 0, 0, 0,
                    null, "main", false, false, null, null);
        }
    }

    @Override
    public Map<String, Long> fetchLanguages(String owner, String repoName) {
        log.debug("fetching languages breakdown for {}/{}", owner, repoName);
        String url = BASE_URL + "/repos/" + owner + "/" + repoName + "/languages";
        try {
            JsonNode node = get(url);
            Map<String, Long> languages = new LinkedHashMap<>();
            node.fields().forEachRemaining(entry ->
                    languages.put(entry.getKey(), entry.getValue().asLong(0)));
            return languages;
        } catch (Exception e) {
            log.error("failed to fetch languages for {}/{}", owner, repoName, e);
            return Map.of();
        }
    }

    @Override
    public List<Issue> fetchOpenIssues(String owner, String repoName) {
        log.debug("fetching open issues for {}/{}", owner, repoName);
        String url = BASE_URL + "/repos/" + owner + "/" + repoName + "/issues?state=open&per_page=100";
        try {
            JsonNode response = get(url);
            List<Issue> issues = new ArrayList<>();
            for (JsonNode node : response) {
                if (node.has("pull_request")) continue;
                issues.add(mapIssue(node));
            }
            return issues;
        } catch (Exception e) {
            log.error("failed to fetch issues for {}/{}", owner, repoName, e);
            return List.of();
        }
    }

    @Override
    public List<PullRequest> fetchMergedPullRequests(String owner, String repoName, int limit) {
        log.debug("fetching merged PRs for {}/{}", owner, repoName);
        String url = BASE_URL + "/repos/" + owner + "/" + repoName
                + "/pulls?state=closed&per_page=" + Math.min(limit, 100);
        try {
            JsonNode response = get(url);
            List<PullRequest> prs = new ArrayList<>();
            for (JsonNode node : response) {
                if (!node.has("merged_at") || node.get("merged_at").isNull()) continue;
                prs.add(mapPullRequest(node));
            }
            return prs;
        } catch (Exception e) {
            log.error("failed to fetch PRs for {}/{}", owner, repoName, e);
            return List.of();
        }
    }

    @Override
    public List<Contributor> fetchContributors(String owner, String repoName) {
        log.debug("fetching contributors for {}/{}", owner, repoName);
        String url = BASE_URL + "/repos/" + owner + "/" + repoName + "/contributors?per_page=20";
        try {
            JsonNode response = get(url);
            List<Contributor> contributors = new ArrayList<>();
            for (JsonNode node : response) {
                contributors.add(Contributor.builder()
                        .username(node.get("login").asText())
                        .totalReviews(node.get("contributions").asInt())
                        .build());
            }
            return contributors;
        } catch (Exception e) {
            log.error("failed to fetch contributors for {}/{}", owner, repoName, e);
            return List.of();
        }
    }

    @Override
    public String fetchPrimaryLanguage(String owner, String repoName) {
        log.debug("fetching language for {}/{}", owner, repoName);
        String url = BASE_URL + "/repos/" + owner + "/" + repoName;
        try {
            JsonNode response = get(url);
            JsonNode lang = response.get("language");
            return lang != null && !lang.isNull() ? lang.asText() : "unknown";
        } catch (Exception e) {
            log.error("failed to fetch language for {}/{}", owner, repoName, e);
            return "unknown";
        }
    }

    private JsonNode get(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("GitHub API returned " + response.statusCode() + " for " + url);
        }

        return objectMapper.readTree(response.body());
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) return null;
        return value.asText();
    }

    private LocalDateTime parseTimestamp(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) return null;
        try {
            return java.time.Instant.parse(value.asText())
                    .atZone(java.time.ZoneOffset.UTC)
                    .toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    private Issue mapIssue(JsonNode node) {
        List<String> labels = new ArrayList<>();
        node.get("labels").forEach(l -> labels.add(l.get("name").asText()));

        int commentCount = node.path("comments").asInt(0);
        String author = node.path("user").path("login").asText(null);

        String assignee = null;
        JsonNode assigneeNode = node.get("assignee");
        if (assigneeNode != null && !assigneeNode.isNull()) {
            assignee = assigneeNode.path("login").asText(null);
        }

        int reactionsCount = node.path("reactions").path("total_count").asInt(0);
        LocalDateTime createdAt = parseTimestamp(node, "created_at");
        LocalDateTime updatedAt = parseTimestamp(node, "updated_at");

        return Issue.builder()
                .id(node.get("number").asLong())
                .number(node.get("number").asInt())
                .title(node.get("title").asText())
                .body(node.has("body") && !node.get("body").isNull() ? node.get("body").asText() : "")
                .labels(labels)
                .state(node.get("state").asText())
                .commentCount(commentCount)
                .author(author)
                .assignee(assignee)
                .reactionsCount(reactionsCount)
                .githubCreatedAt(createdAt)
                .githubUpdatedAt(updatedAt)
                .build();
    }

    private PullRequest mapPullRequest(JsonNode node) {
        LocalDateTime merged = java.time.Instant.parse(node.get("merged_at").asText())
                .atZone(java.time.ZoneOffset.UTC).toLocalDateTime();
        LocalDateTime created = java.time.Instant.parse(node.get("created_at").asText())
                .atZone(java.time.ZoneOffset.UTC).toLocalDateTime();
        long hours = java.time.Duration.between(created, merged).toHours();

        Integer linkedIssue = null;
        JsonNode body = node.get("body");
        if (body != null && !body.isNull()) {
            String bodyText = body.asText();
            java.util.regex.Matcher matcher = java.util.regex.Pattern
                    .compile("(?:closes|fixes|resolves)\\s+#(\\d+)", java.util.regex.Pattern.CASE_INSENSITIVE)
                    .matcher(bodyText);
            if (matcher.find()) {
                linkedIssue = Integer.parseInt(matcher.group(1));
            }
        }

        return PullRequest.builder()
                .id(node.get("number").asLong())
                .number(node.get("number").asInt())
                .title(node.get("title").asText())
                .mergeTimeHours((int) hours)
                .linkedIssueNumber(linkedIssue)
                .author(node.get("user").get("login").asText())
                .mergedAt(merged)
                .build();
    }
}
