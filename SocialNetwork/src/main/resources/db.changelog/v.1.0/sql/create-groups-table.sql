create type group_status as enum ('ACTIVE', 'BLOCKED', 'ABANDONED');

create table if not exists groups
(
    id               bigserial primary key,
    name             varchar(40),
    creator_id bigint references users(id),
    description      varchar(1000),
    date_of_creation timestamp,
    status           group_status
);

create table if not exists user_group
(
    user_id bigint references users(id),
    group_id bigint references groups(id)
);
