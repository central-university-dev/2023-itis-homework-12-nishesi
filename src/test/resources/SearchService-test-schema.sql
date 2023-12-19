drop table if exists item;
drop table if exists item_sku;
drop table if exists remain;
drop table if exists catalogue;

create table item
(
    item_id      bigint,
    catalogue_id bigint,
    brand_id     bigint,
    name         varchar,
    description  varchar,
    itemurl      varchar,
    type         varchar,

    i            varchar,
    brand        varchar,
    catalogue    varchar
);

create table item_sku
(
    item_id bigint,
    sku     varchar
);

create table remain
(
    item_id   bigint,
    region_id bigint,
    price     bigint
);

create table catalogue
(
    catalogue_id bigint,
    realcatname  varchar,
    image        varchar,
    parent_id    bigint,
    name         varchar
);

insert into item (item_id, catalogue_id, brand_id, name, description, itemurl, type, i, brand, catalogue)
values (1, 101, 1001, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),
       (2, 101, 1001, 'name', 'desc', 'itemurl2', 'type', 'i', 'brand', 'catalogue'),
       (3, 102, 1001, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),

       (5, 103, 1001, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),
       (6, 103, 1001, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),

       (7, 103, 1001, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),
       (8, 103, 1001, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),

       (9, 104, 1002, 'Kit Kat', 'mega tasty', 'kit-kat-1', 'Chocolate Bar', 'kit-kat-image.jpg', 'Nestle', 'Sweets'),

       (10, 105, 1003, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),
       (11, 106, 1003, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),

       (12, 107, 1003, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),
       (13, 108, 1003, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),
       (14, 109, 1003, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),

       (15, 110, 1003, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),
       (16, 111, 1003, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),

       (17, 112, 1003, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue'),

       (18, 114, 1003, 'name', 'desc', 'itemurl', 'type', 'i', 'brand', 'catalogue');

insert into remain (item_id, region_id, price)
values (1, 1, 10),
       (2, 1, 10),
       (3, 1, 10),

       (4, 2, 20),
       (5, 2, 20),

       (7, 3, 101001001000),
       (7, 4, 40),
       (7, 5, 50),
       (8, 3, 30),
       (8, 4, 40),

       (9, 1, 10),

       (10, 1, 10),
       (11, 1, 10),

       (12, 1, 10),
       (13, 1, 10),
       (14, 1, 10),

       (15, 1, 10),
       (16, 1, 10),

       (17, 1, 10),
       (18, 1, 10);

insert into catalogue (catalogue_id, realcatname, image, parent_id, name)
values (101, 'catalogue_1', 'image', 101, 'catalogue'),
       (102, 'catalogue_2', 'image', 102, 'catalogue'),
       (103, 'catalogue_3', 'image', 103, 'catalogue'),
       (104, 'catalogue_4', 'image', 104, 'catalogue'),

       (105, 'catalogue_5', 'image', 105, 'cat_name_5'),
       (106, 'catalogue_6', 'image', 106, 'cat_name_6'),

       (107, 'catalogue_7', 'image', 107, 'cat_name_7'),
       (108, 'catalogue_8', 'image', 107, 'cat_name_8'),
       (109, 'catalogue_9', 'image', null, 'cat_name_9'),

       (110, 'non_uniq_url', 'image', 110, 'cat_name_10'),
       (111, 'non_uniq_url', 'image', 111, 'cat_name_11'),

       (112, 'smartphones', 'image', 113, 'cat_name_12'),
       (113, 'techniques', 'image', 113, 'cat_name_13');

