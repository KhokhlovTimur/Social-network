create type message_type_enum as enum ('MESSAGE', 'LEAVE', 'JOIN');

create table if not exists messages
(
    id           bigserial primary key,
    type         message_type_enum,
    content      varchar(1000),
    sender_id    bigint references users (id),
    sending_time timestamp,
    global_chat_id bigint references chats_global_ids(id)
);