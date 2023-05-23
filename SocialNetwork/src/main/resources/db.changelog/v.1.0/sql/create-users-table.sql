create type user_role_enum as enum ('AUTHORIZED', 'ADMIN', 'SUPER_ADMIN');
create type user_state_enum as enum ('ACTIVE', 'BANNED');

create table if not exists users
(
    id           bigserial primary key,
    name         varchar(30)        not null check (length(name) > 0),
    surname      varchar(30)        not null check (length(name) > 0),
    password     varchar(255)       not null check (length(password) > 5),
    age          int               ,
    username     varchar(50) unique not null check (length(username) > 0),
    email        varchar(50) check (email ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
    phone_number varchar(15) check (phone_number ~ '^[0-9]+$'),
    role         user_role_enum     not null,
    state        user_state_enum    not null,
    avatar_link   varchar,
    bio          varchar(255),
    gender varchar(10),
    date_of_registration timestamp
);

create unique index nickname_unique_index on users (username);