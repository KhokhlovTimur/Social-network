create table if not exists post_file
(
    post_id   bigint references posts (id),
    file_id bigint references files_info(id)
);