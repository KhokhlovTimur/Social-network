create type friends_state_enum as enum ('0', '-1', '1');

create table if not exists friends
(
    id             bigserial primary key,
    first_user_id  bigint references users (id),
    second_user_id bigint references users (id),
    state          friends_state_enum
);