-- liqidbase formatted SQL
-- changeset Alexey:0001

create table user_data
(
    id        serial       NOT NULL primary key,
    username  VARCHAR(255) NOT NULL,
    password  VARCHAR(255) NOT NULL,
    data_path VARCHAR(255) NOT NULL,
    is_enable boolean
);

create table storage
(
    id        serial    NOT NULL primary key,
    filename VARCHAR(255),
    is_exist  boolean,
    date      timestamp not null default now(),
    user_id  integer NOT NULL,
    file_size integer,
    FOREIGN KEY (user_id) REFERENCES user_data(id)
);

create INDEX index_user
    on user_data (id);

