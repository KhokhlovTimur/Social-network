create type chat_type_enum as enum ('PERSONAL', 'PUBLIC');

create table if not exists chats_global_ids
(
    id        bigserial primary key,
    chat_type chat_type_enum
);