create table if not exists personal_chats
(
    id             bigserial primary key,
    first_user_id  bigint references users (id),
    second_user_id bigint references users (id),
    global_id bigint references chats_global_ids(id)
);