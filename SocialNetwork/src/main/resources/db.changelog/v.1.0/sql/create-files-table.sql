create table if not exists files_info
(
    id                bigserial primary key,
    file_link         varchar,
    original_filename varchar,
    mime_type         varchar
);