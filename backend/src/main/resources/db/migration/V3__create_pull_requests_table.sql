create table if not exists pull_requests (
    id                  bigserial primary key,
    repo_id             bigint       not null references repositories(id) on delete cascade,
    number              int          not null,
    title               varchar(512) not null,
    files_changed       int          not null default 0,
    lines_added         int          not null default 0,
    lines_removed       int          not null default 0,
    merge_time_hours    int,
    linked_issue_number int,
    author              varchar(128),
    merged_at           timestamp,
    constraint uq_pr_repo_number unique (repo_id, number)
);

create index idx_pull_requests_repo_id on pull_requests(repo_id);
create index idx_pull_requests_author  on pull_requests(author);
