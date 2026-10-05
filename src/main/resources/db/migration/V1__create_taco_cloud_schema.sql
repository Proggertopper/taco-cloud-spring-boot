create table ingredient (
    id varchar(255) not null,
    name varchar(255),
    type varchar(32),
    primary key (id)
);

create table taco (
    id bigint not null auto_increment,
    created_at timestamp(6),
    name varchar(255),
    primary key (id)
);

create table users (
    id bigint not null auto_increment,
    username varchar(255),
    password varchar(255),
    fullname varchar(255),
    street varchar(255),
    city varchar(255),
    state varchar(255),
    zip varchar(255),
    phone_number varchar(255),
    role varchar(255),
    primary key (id)
);

create table taco_order (
    id varchar(36) not null,
    delivery_name varchar(255),
    delivery_street varchar(255),
    delivery_city varchar(255),
    delivery_state varchar(255),
    delivery_zip varchar(255),
    cc_number varchar(255),
    cc_expiration varchar(255),
    cc_cvv varchar(255),
    placed_at timestamp(6),
    user_id bigint,
    primary key (id)
);

create table taco_ingredients (
    taco_id bigint not null,
    ingredient_id varchar(255) not null
);

create table taco_order_tacos (
    order_id varchar(36) not null,
    taco_id bigint not null
);

alter table taco_order
    add constraint fk_taco_order_user
    foreign key (user_id) references users (id);

alter table taco_ingredients
    add constraint fk_taco_ingredients_taco
    foreign key (taco_id) references taco (id);

alter table taco_ingredients
    add constraint fk_taco_ingredients_ingredient
    foreign key (ingredient_id) references ingredient (id);

alter table taco_order_tacos
    add constraint fk_taco_order_tacos_order
    foreign key (order_id) references taco_order (id);

alter table taco_order_tacos
    add constraint fk_taco_order_tacos_taco
    foreign key (taco_id) references taco (id);

create index idx_taco_order_user_placed_at on taco_order (user_id, placed_at);
