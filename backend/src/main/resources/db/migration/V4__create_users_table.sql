create table if not exists users (
    id           bigserial primary key,
    email        varchar(255) not null unique,
    password_hash varchar(255) not null,
    name         varchar(128) not null,
    created_at   timestamp not null default now()
);

create index if not exists idx_users_email on users(email);
