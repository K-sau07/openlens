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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class GitHubApiClient implements GitHubDataPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubApiClient.class);
    private static final String BASE_URL = "https://api.github.com";
    private static final DateTimeFormatter GH_DATE = DateTimeFormatter.ISO_DATE_TIME;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String token;

    public GitHubApiClient(
            ObjectMapper objectMapper,
            @Value("${github.api.token}") String token) {
        this.objectMapper = objectMapper;
        this.token = token;
        // virtual thread per task — each API call gets its own lightweight thread
        this.httpClient = HttpClient.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();
    }

    @Override
    public List<Issue> fetchOpenIssues(String owner, String repoName) {
        log.debug("fetching open issues for {}/{}", owner, repoName);
        String url = BASE_URL + "/repos/" + owner + "/" + repoName + "/issues?state=open&per_page=100";
        try {
            JsonNode response = get(url);
            List<Issue> issues = new ArrayList<>();
            for (JsonNode node : response) {
                // skip pull requests — GitHub issues API returns both
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
                contributors.add(new Contributor(
                        null,
                        null,
                        node.get("login").asText(),
                        node.get("contributions").asInt(),
                        null,
                        null
                ));
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

    private Issue mapIssue(JsonNode node) {
        List<String> labels = new ArrayList<>();
        node.get("labels").forEach(l -> labels.add(l.get("name").asText()));

        return new Issue(
                node.get("number").asLong(),
                null,
                node.get("number").asInt(),
                node.get("title").asText(),
                node.has("body") && !node.get("body").isNull() ? node.get("body").asText() : "",
                labels,
                node.get("state").asText(),
                null
        );
    }

    private PullRequest mapPullRequest(JsonNode node) {
        String mergedAt = node.get("merged_at").asText();
        String createdAt = node.get("created_at").asText();

        LocalDateTime merged = LocalDateTime.parse(mergedAt.replace("Z", ""), GH_DATE);
        LocalDateTime created = LocalDateTime.parse(createdAt.replace("Z", ""), GH_DATE);
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

        return new PullRequest(
                node.get("number").asLong(),
                null,
                node.get("number").asInt(),
                node.get("title").asText(),
                0,
                0,
                0,
                (int) hours,
                linkedIssue,
                node.get("user").get("login").asText(),
                merged
        );
    }
}
