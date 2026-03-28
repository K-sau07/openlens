create table if not exists issues (
    id               bigserial primary key,
    repo_id          bigint       not null references repositories(id) on delete cascade,
    number           int          not null,
    title            varchar(512) not null,
    body             text,
    labels           text,
    state            varchar(20)  not null default 'open',
    complexity_score int,
    created_at       timestamp    not null default now(),
    constraint uq_issue_repo_number unique (repo_id, number)
);

create index if not exists idx_issues_repo_id on issues(repo_id);
create index if not exists idx_issues_state   on issues(state);
