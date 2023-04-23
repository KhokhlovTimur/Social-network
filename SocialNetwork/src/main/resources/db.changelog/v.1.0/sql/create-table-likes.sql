create table if not exists likes
(
    user_id bigint references users (id),
    post_id bigint references posts (id)
);