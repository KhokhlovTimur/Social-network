create table if not exists chats
(
    id        bigserial primary key,
    owner_id  bigint references users (id),
    name      varchar(100),
    global_id bigint references chats_global_ids(id),
    image_link varchar,
    date_of_creation timestamp
);

create table chat_user
(
    user_id bigint references users (id),
    chat_id bigint references chats (id)
);