# OpenLens

Open source contribution intelligence platform. Paste a GitHub repo URL and your skill level, get a personalized contribution brief — matched issues, files to touch, maintainer patterns, similar merged PRs.

## The Problem

Everyone tells freshers to contribute to open source. Nobody tells them how to make their first PR. Existing tools stop at listing issues. OpenLens goes deeper — it reads the repo's contribution history and tells you exactly where to start.

## Architecture

Modular monolith with hexagonal architecture internally. Event-driven between modules via Kafka. CQRS separates the write-heavy ingestion path from the read-heavy query path.

```
Hexagonal Architecture    — structure within each module
Event-Driven Architecture — communication between modules
CQRS                      — write path vs read path separation
Modular Monolith          — single deployable, microservices-ready
```

### Request Flow

```
User (repo URL + skill level)
        |
Spring Boot API
        |
Redis cache check — hit? return immediately
        |
Kafka: repo.ingestion.requested
        |
Ingestion Service (Java virtual threads — concurrent GitHub API calls)
  fetches: open issues, merged PRs, review comments, contributors, languages
        |
Kafka: repo.ingestion.completed
        |
Intelligence Service (async LLM calls)
  produces: issue complexity scores, maintainer profile, skill-matched briefs
        |
PostgreSQL — persist structured intelligence
        |
Redis — cache for fast repeat queries
        |
WebSocket — push live progress + completion to user
```

## Design Patterns

- Producer-Consumer via Kafka between every stage boundary
- Cache-Aside with Redis — app manages cache explicitly
- Repository pattern — all DB access through interfaces
- Strategy pattern — skill matching has three implementations (beginner, intermediate, advanced)
- Facade pattern — all GitHub API complexity behind one interface
- Builder pattern — contribution briefs constructed with builder

## Tech Stack

- Java 21, Spring Boot 3
- Virtual Threads for concurrent GitHub API ingestion
- Kafka for async job processing between stages
- Redis for caching and rate limit quota tracking
- PostgreSQL for persistent storage
- GitHub REST API as primary data source
- LLM API for contribution brief generation
- React + Vite + Tailwind for frontend

## Module Structure

```
backend/
  src/main/java/com/openlens/
    domain/          — pure Java, zero framework dependencies
    application/     — use cases, orchestration, port interfaces
    infrastructure/  — Kafka, Redis, GitHub API, AI client, JPA
    api/             — Spring controllers, DTOs, WebSocket handlers
```

## Kafka Topics

- repo.ingestion.requested  — API layer produces when user submits repo
- repo.ingestion.completed  — ingestion service produces when raw data is ready
- repo.intelligence.completed — intelligence service produces when brief is ready

## Database Tables

- repositories    — url, owner, name, language, stars, status, last_analyzed_at
- issues          — repo_id, number, title, labels, complexity_score, state
- pull_requests   — repo_id, number, files_changed, lines_changed, merge_time_hours, linked_issue_number
- contributors    — repo_id, username, review_style, avg_response_hours, total_reviews
- contribution_briefs — repo_id, issue_id, skill_level, files_to_touch, similar_pr_number, maintainer_notes, generated_at

## GitHub API Strategy

Rate limit: 5000 requests/hour authenticated. Strategy:
- All calls go through RateLimitAwareGitHubClient — tracks quota in Redis
- Popular repos cached aggressively — repeat requests served from cache
- Incremental updates via GitHub webhooks — only re-fetch changed data
- Nightly scheduled jobs re-analyze stale repos during off-peak hours

## Code Standards

- No emojis anywhere — not in code, comments, commits, or docs
- Comments are sparse and natural — written like a human who knows the codebase
- No over-explained or AI-style comments
- Architecture enforced by package structure — domain never imports infrastructure
- Every public method has a clear single responsibility
- DTOs at the API boundary, domain objects inside

## Git Commits

Natural past-tense messages. No prefixes, no emojis, no conventional commit format.
Examples: "added repo ingestion service", "wired kafka producer for ingestion jobs", "fixed rate limit tracking in redis"

## Demo Repo

https://github.com/OpenCodeIntel/opencodeintel — friend's project, Python/FastAPI codebase intelligence tool. 50 open issues, 0 merged PRs. Perfect demo case — lots of open issues, no successful contributions yet.

## Local Development

Ports: backend 8080, frontend 5173
Infrastructure: Postgres 5432, Redis 6379, Kafka 9092, Zookeeper 2181

Start infrastructure:
```bash
/usr/local/bin/docker start openlens-postgres openlens-redis openlens-zookeeper openlens-kafka
```

Start backend:
```bash
/opt/homebrew/bin/mvn spring-boot:run
```

Start frontend:
```bash
PATH="/opt/homebrew/Cellar/node/25.1.0_1/bin:$PATH" npm run dev
```
