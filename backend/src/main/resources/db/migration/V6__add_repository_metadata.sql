-- enrich repository records with full GitHub metadata
-- previously we only stored url, owner, name, primary_language, stars

alter table repositories add column description text;
alter table repositories add column topics text[];
alter table repositories add column fork_count integer default 0;
alter table repositories add column watchers_count integer default 0;
alter table repositories add column open_issues_count integer default 0;
alter table repositories add column license varchar(100);
alter table repositories add column default_branch varchar(100);
alter table repositories add column languages jsonb;
alter table repositories add column has_wiki boolean default false;
alter table repositories add column has_discussions boolean default false;
alter table repositories add column created_at_github timestamp;
alter table repositories add column last_pushed_at timestamp;
