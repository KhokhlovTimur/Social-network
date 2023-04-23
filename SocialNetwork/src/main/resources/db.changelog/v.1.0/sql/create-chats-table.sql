create type chat_type as enum ('PRIVATE', 'PUBLIC');

create table if not exists chats(
    id bigserial primary key ,
    creator_id bigint references users(id),
    name varchar(100),
    chat_type chat_type
);

create table chat_user(
    user_id bigint references users(id),
    chat_id bigint references chats(id)
);