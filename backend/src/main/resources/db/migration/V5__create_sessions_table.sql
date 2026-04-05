create table if not exists user_sessions (
    id           bigserial primary key,
    user_id      bigint not null references users(id) on delete cascade,
    token_hash   varchar(512) not null unique,
    expires_at   timestamp not null,
    created_at   timestamp not null default now()
);

create index if not exists idx_sessions_token_hash on user_sessions(token_hash);
create index if not exists idx_sessions_user_id on user_sessions(user_id);
