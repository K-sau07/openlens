# OpenLens — Agent Context

Open source contribution intelligence. Paste a GitHub repo URL, answer 5 repo-specific questions, get a personalized contribution guide — matched issues, files to touch, maintainer patterns, step-by-step PR path.

## Architecture

Hexagonal architecture internally. Event-driven between modules via Kafka.

```
User → POST /api/repos/analyze
     → Kafka: repo.ingestion.requested
     → IngestionJobConsumer fetches GitHub data (issues, PRs, language)
     → Persists to Postgres, marks repo READY
     → GET /api/repos/status polls until READY
     → GET /api/quiz/{repoId}/questions — Claude Haiku generates 5 questions
     → POST /api/quiz/{repoId}/submit — scores answers, returns matched issues
     → GET /api/guide/{repoId}/issues/{issueId} — Claude Haiku generates guide
```

## Module Structure

```
backend/src/main/java/com/openlens/
├── api/                          # Controllers, DTOs, CORS, exception handling
│   ├── controller/               # RepositoryController, QuizController, GuideController
│   └── CorsConfig.java
├── application/service/          # RepositoryAnalysisService — use case orchestration
├── auth/                         # Auth module (self-contained)
│   ├── config/SecurityConfig.java
│   ├── controller/AuthController.java
│   ├── domain/UserEntity.java, UserRepository.java
│   ├── filter/JwtAuthFilter.java
│   └── service/AuthService.java, JwtService.java
├── domain/
│   ├── model/                    # Repository, Issue, PullRequest, SkillLevel, etc.
│   └── port/
│       ├── input/                # AnalyzeRepositoryUseCase, GetContributionBriefUseCase
│       └── output/               # RepositoryPort, IssuePort, PullRequestPort, etc.
└── infrastructure/
    ├── ai/                       # ClaudeApiClient, AiAnalysisService
    ├── github/                   # GitHubApiClient — virtual thread HTTP client
    ├── kafka/                    # IngestionJobProducer, IngestionJobConsumer
    ├── persistence/              # JPA entities, adapters for all ports
    └── redis/                    # Cache adapter
```

## Kafka Topics

- `repo.ingestion.requested` — payload: `{ owner, repoName }`, key: repoUrl

## Database Schema

```sql
repositories  — id, url, owner, name, primary_language, stars, status, last_analyzed_at
issues        — id, repo_id, number, title, body, labels, state, complexity_score
pull_requests — id, repo_id, number, title, files_changed, lines_added, lines_removed,
                merge_time_hours, linked_issue_number, author, merged_at
users         — id, email, password_hash, name, created_at
user_sessions — id, user_id, token_hash, expires_at, created_at
```

Flyway owns schema — `ddl-auto: none`. Migrations in `backend/src/main/resources/db/migration/`.

## Auth

- JWT stored in httpOnly + SameSite=Lax cookie named `ol_token`
- BCrypt cost factor 12
- 7-day token expiry
- Public routes: `/api/auth/**`, `/api/repos/**`, `/api/quiz/**`, `/api/guide/**`, `/actuator/**`
- JWT secret: `${JWT_SECRET}` env var

## AI Layer

Claude Haiku via Anthropic API. Falls back to rule-based if key not configured or call fails.

- Quiz: sends repo language + issue titles/labels + 12 merged PR titles → 5 tailored questions
- Guide: sends full issue body (up to 2000 chars) + 10 merged PR patterns → 8-step guide
- Max tokens: 3000 per call
- Model: `claude-haiku-4-5-20251001`

## Environment Variables

See `.env.example` for all required variables. Copy to `.env` and fill in values.

Required:
- `GITHUB_TOKEN` — GitHub PAT with `public_repo` scope
- `ANTHROPIC_API_KEY` — Anthropic API key (sk-ant-...)
- `JWT_SECRET` — minimum 32 characters

Optional (defaults work for local dev):
- `POSTGRES_URL`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`
- `KAFKA_BOOTSTRAP_SERVERS`

## Local Dev

```bash
# Start infrastructure
docker compose up -d

# Start backend
cd backend
source ../.env && GITHUB_TOKEN=$GITHUB_TOKEN ANTHROPIC_API_KEY=$ANTHROPIC_API_KEY JWT_SECRET=$JWT_SECRET mvn spring-boot:run

# Start frontend (separate terminal)
cd frontend
npm run dev
```

Frontend: http://localhost:5173
Backend: http://localhost:8080
Vite proxies `/api/*` → `http://localhost:8080`

## Coding Standards

- No emojis in code, comments, commits, or UI
- No framework dependencies in domain layer
- DTO pattern on all API boundaries
- SLF4J logging with meaningful messages
- Commits: past tense, no prefixes — "added X", "fixed Y", "improved Z"
- One logical unit per commit

## Active Branches

- `main` — stable, everything merged
- `feature/ci-cd` — merged
- `feature/ai-layer` — merged
- `feature/auth` — merged
