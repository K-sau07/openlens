# OpenLens

Paste a GitHub repo URL. Answer 5 questions about your experience. Get a contribution guide — matched issues, files to touch, maintainer patterns, and a step-by-step path to your first merged PR.

The problem this solves: everyone tells developers to contribute to open source. Nobody tells them how to actually do it for a specific repo.

Live demo repo: [OpenCodeIntel/opencodeintel](https://github.com/OpenCodeIntel/opencodeintel)

---

## How it works

1. You paste a GitHub repo URL
2. A Kafka consumer fetches the repo's open issues, merged PRs, and primary language from the GitHub API
3. Claude Haiku reads the PR history and issue labels to generate 5 questions specific to that repo's stack
4. Your answers determine your skill level and which issues get surfaced
5. Claude generates a full contribution guide for whichever issue you pick — files to touch, commit message format, PR description template, maintainer style based on merged PR history

---

## Stack

**Backend** — Java 21, Spring Boot 3.2, hexagonal architecture
- Virtual threads for GitHub API calls
- Kafka for async ingestion (repo analysis is decoupled from the HTTP request)
- PostgreSQL + Flyway for schema management
- Redis for caching
- Claude Haiku via Anthropic API for quiz and guide generation
- Ports and adapters pattern — domain layer has zero framework dependencies

**Frontend** — React 19, Vite 6, React Router 7, Tailwind 3
- Five pages: Landing, Analysis, Quiz, Issue Selection, Guide
- Vite proxy for local dev (no CORS config needed)
- Fallback data on every page so the UI works even if the backend returns nothing

**Infrastructure** — Docker Compose for local dev
- Postgres 16, Redis 7, Zookeeper + Kafka (Confluent 7.5)

---

## Running locally

You need Docker Desktop, Java 21, Node 20, and a GitHub personal access token.

**1. Clone and set up environment**

```bash
git clone https://github.com/K-sau07/openlens
cd openlens
```

Create a `.env` file in the root:

```
GITHUB_TOKEN=ghp_your_token_here
ANTHROPIC_API_KEY=sk-ant-your_key_here
POSTGRES_URL=jdbc:postgresql://localhost:5432/openlens
POSTGRES_USER=openlens
POSTGRES_PASSWORD=openlens
REDIS_HOST=localhost
REDIS_PORT=6379
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

Get a GitHub token at github.com → Settings → Developer settings → Personal access tokens → Classic. You need `public_repo` scope. Without it the GitHub API calls will fail silently and you'll get no issues or PRs.

Get a Claude API key at console.anthropic.com. Without it the app falls back to rule-based quiz and guide generation — it still works, just less tailored.

**2. Start infrastructure**

```bash
docker compose up -d
```

Wait for Postgres and Redis to show `healthy`:

```bash
docker ps
```

**3. Start the backend**

```bash
cd backend
source ../.env && GITHUB_TOKEN=$GITHUB_TOKEN ANTHROPIC_API_KEY=$ANTHROPIC_API_KEY mvn spring-boot:run
```

The backend starts on port 8080. Flyway runs migrations automatically on startup.

**4. Start the frontend**

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on port 5173. Vite proxies `/api/*` requests to the backend so there's no CORS configuration needed.

**5. Try it**

Open http://localhost:5173, paste `https://github.com/OpenCodeIntel/opencodeintel`, and hit Analyze. The first run takes 10-15 seconds while Kafka ingests the repo data from GitHub.

---

## Project structure

```
openlens/
├── backend/
│   └── src/main/java/com/openlens/
│       ├── api/                    # Controllers, DTOs, CORS config
│       ├── application/service/    # Use case orchestration
│       ├── domain/
│       │   ├── model/              # Repository, Issue, PullRequest, etc.
│       │   └── port/               # Input and output port interfaces
│       └── infrastructure/
│           ├── ai/                 # Claude API client and analysis service
│           ├── github/             # GitHub REST API client
│           ├── kafka/              # Producer and consumer
│           ├── persistence/        # JPA entities and adapters
│           └── redis/              # Cache adapter
├── frontend/
│   └── src/
│       ├── pages/                  # LandingPage, AnalysisPage, QuizPage, IssueSelectionPage, GuidePage
│       ├── components/             # Navbar, BriefCard, RepoGraph, Ticker
│       └── services/api.js         # All backend calls
└── docker-compose.yml
```

The domain layer (`domain/model/` and `domain/port/`) has zero dependencies on Spring, JPA, or Kafka. Everything infrastructure-specific lives in `infrastructure/` and implements the port interfaces.

---

## API endpoints

```
POST /api/repos/analyze          — submit a repo URL for ingestion
GET  /api/repos/status?url=...   — poll ingestion status (PROCESSING / READY / FAILED)
GET  /api/quiz/{repoId}/questions  — get 5 repo-specific quiz questions
POST /api/quiz/{repoId}/submit   — submit answers, get skill level + matched issues
GET  /api/guide/{repoId}/issues/{issueId}  — get full contribution guide for an issue
```

---

## Active branches

- `main` — stable
- `feature/ci-cd` — GitHub Actions CI (backend compile + test, frontend build) and Dockerfiles
- `feature/ai-layer` — Claude Haiku integration for quiz generation and guide generation

---

## What's next

- AWS deployment — ECS Fargate, MSK for Kafka, ElastiCache, RDS
- Redis caching for contribution briefs (avoid regenerating guides on repeat visits)
- Skill matching refinement — use issue body complexity, not just labels
- More repos in the demo dropdown

---

## Author

Saurabh Kashyap — [github.com/K-sau07](https://github.com/K-sau07)
