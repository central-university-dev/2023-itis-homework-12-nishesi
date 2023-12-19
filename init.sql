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