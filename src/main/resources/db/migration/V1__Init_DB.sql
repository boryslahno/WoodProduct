create table company
    (id int8 generated by default as identity,
    about varchar(5000) not null,
    address varchar(300) not null,
    name varchar(255) not null,
    phone_number varchar(255) not null,
    user_id int8 not null, primary key (id));
create table personal_information
    (id int8 generated by default as identity,
    address varchar(300),
    name varchar(255) not null,
    phone_number varchar(255),
    surname varchar(255) not null,
    user_id int8 not null, primary key (id));
create table user_role
    user_id int8 not null,
    roles varchar(255));
create table users
(id int8 generated by default as identity,
    password varchar(255),
    username varchar(255), primary key (id));
alter table if exists company add constraint UK_g82ixrst2tc542u5s214ggpdf unique (user_id);
alter table if exists personal_information add constraint UK_9p89uwdvfbsvji2xm9qg1oor unique (user_id);
alter table if exists company add constraint FKsxe9t9istcdt2mtdbvgh83a9g foreign key (user_id) references users;
alter table if exists personal_information add constraint FK78lfntv394ity4nb30ca7rdbg foreign key (user_id) references users;
alter table if exists user_role add constraint FKj345gk1bovqvfame88rcx7yyx foreign key (user_id) references users;