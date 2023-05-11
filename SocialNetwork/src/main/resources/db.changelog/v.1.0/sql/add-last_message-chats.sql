alter table chats_global_ids
    add column last_msg_id bigint;
alter table chats_global_ids
    add constraint last_msg_fk foreign key (last_msg_id) references messages (id);