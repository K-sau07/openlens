-- enrich issue records with metadata from the GitHub API
-- previously we only stored number, title, body, labels, state, complexity_score

alter table issues add column comment_count integer default 0;
alter table issues add column author varchar(255);
alter table issues add column assignee varchar(255);
alter table issues add column reactions_count integer default 0;
alter table issues add column github_created_at timestamp;
alter table issues add column github_updated_at timestamp;
