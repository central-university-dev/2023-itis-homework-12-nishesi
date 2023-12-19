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

insert into item (item_id, catalogue_id, brand_id, name, description, itemurl, type, i, brand, catalogue)
values
    (1, 101, 1001, 'default name 1', 'def desc 1', 'item_url', 'type', 'i', 'brand_1', 'catalogue_1'),
    (1, 101, 1001, 'default name 2', 'def desc 2', 'item_url', 'type', 'i', 'brand_2', 'catalogue_2')
