create table repositories (
    id                bigserial primary key,
    url               varchar(512) not null unique,
    owner             varchar(255) not null,
    name              varchar(255) not null,
    primary_language  varchar(100),
    stars             integer default 0,
    status            varchar(50) not null default 'PENDING',
    last_analyzed_at  timestamp,
    created_at        timestamp not null default now(),
    updated_at        timestamp not null default now()
);

create table issues (
    id               bigserial primary key,
    repo_id          bigint not null references repositories(id),
    number           integer not null,
    title            varchar(1024) not null,
    body             text,
    labels           text[],
    state            varchar(20) not null default 'open',
    complexity_score integer,
    created_at       timestamp not null default now(),
    unique(repo_id, number)
);

create table pull_requests (
    id                   bigserial primary key,
    repo_id              bigint not null references repositories(id),
    number               integer not null,
    title                varchar(1024),
    files_changed        integer default 0,
    lines_added          integer default 0,
    lines_removed        integer default 0,
    merge_time_hours     integer,
    linked_issue_number  integer,
    author               varchar(255),
    merged_at            timestamp,
    created_at           timestamp not null default now(),
    unique(repo_id, number)
);

create table contributors (
    id                 bigserial primary key,
    repo_id            bigint not null references repositories(id),
    username           varchar(255) not null,
    total_reviews      integer default 0,
    avg_response_hours integer,
    review_style       text,
    created_at         timestamp not null default now(),
    unique(repo_id, username)
);

create table contribution_briefs (
    id                bigserial primary key,
    repo_id           bigint not null references repositories(id),
    issue_id          bigint references issues(id),
    skill_level       varchar(20) not null,
    files_to_touch    text[],
    similar_pr_number integer,
    maintainer_notes  text,
    raw_brief         text,
    generated_at      timestamp not null default now()
);

create index idx_repositories_url on repositories(url);
create index idx_issues_repo_id on issues(repo_id);
create index idx_issues_complexity on issues(repo_id, complexity_score);
create index idx_pull_requests_repo_id on pull_requests(repo_id);
create index idx_contributors_repo_id on contributors(repo_id);
create index idx_briefs_repo_skill on contribution_briefs(repo_id, skill_level);
