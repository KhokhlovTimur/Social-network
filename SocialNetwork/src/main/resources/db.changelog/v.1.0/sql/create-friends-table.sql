create table if not exists friends
(
    id             bigserial primary key,
    first_user_id  bigint references users (id),
    second_user_id bigint references users (id),
    state          varchar(2)
);