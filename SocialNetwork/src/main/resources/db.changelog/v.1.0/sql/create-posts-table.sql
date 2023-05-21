create table if not exists posts(
    id bigserial primary key,
    group_id bigint references groups(id),
    text text,
    date_of_publication timestamp,
    author_id bigint references users(id)
);