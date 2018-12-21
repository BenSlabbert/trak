-- liquibase formatted sql

-- changeset trak:1

create schema if not exists trak;

-- changeset trak:2

create table trak.seller (
  id      bigint auto_increment not null,
  created datetime              not null,
  updated datetime              not null,
  version integer,
  name    varchar(255) unique,
  primary key (id),
  index (id)
)
  engine = InnoDB;

-- changeset trak:3

create table trak.brand (
  id      bigint auto_increment not null,
  created datetime              not null,
  updated datetime              not null,
  version integer,
  name    varchar(255) unique,
  primary key (id),
  index (id)
)
  engine = InnoDB;

-- changeset trak:4

create table trak.category (
  id      bigint auto_increment not null,
  created datetime              not null,
  updated datetime              not null,
  version integer,
  name    varchar(255) unique,
  primary key (id),
  index (id)
)
  engine = InnoDB;

-- changeset trak:5

create table trak.product (
  id           bigint auto_increment not null,
  created      datetime              not null,
  updated      datetime              not null,
  name         text,
  url          text,
  sku          text,
  api_endpoint text,
  version      integer,
  brand_id     bigint                not null,
  seller_id    bigint                not null,
  primary key (id),
  foreign key (brand_id) references trak.brand (id),
  foreign key (seller_id) references trak.seller (id),
  index (id),
  index (brand_id),
  index (seller_id)
)
  engine = InnoDB;

-- changeset trak:6

create table trak.link_product_category (
  product_id  bigint,
  category_id bigint,
  foreign key (product_id) references trak.product (id),
  foreign key (category_id) references trak.category (id)
)
  engine = InnoDB;

-- changeset trak:7

create table trak.price (
  id            bigint auto_increment not null,
  created       datetime              not null,
  current_price bigint,
  listed_price  bigint,
  product_id    bigint,
  primary key (id),
  foreign key (product_id) references trak.product (id),
  index (id),
  index (product_id)
)
  engine = InnoDB;

-- changeset trak:8

insert into trak.seller (created, updated, version, name)
values (now(), now(), 0, 'Takealot');

-- changeset trak:9

insert into trak.brand (created, updated, version, name)
values (now(), now(), 0, 'Unknown');

-- changeset trak:10

create table trak.seller_crawler (
  id        bigint auto_increment not null,
  created   datetime              not null,
  updated   datetime              not null,
  version   integer,
  seller_id bigint unique,
  last_id   bigint,
  primary key (id),
  index (id),
  index (seller_id),
  foreign key (seller_id) references trak.seller (id)
)
  engine = InnoDB;

-- changeset trak:11

insert into trak.seller_crawler (created, updated, version, seller_id, last_id)
values (now(), now(), 0, 1, 41469985);

-- rollback delete from trak.seller_crawler;
