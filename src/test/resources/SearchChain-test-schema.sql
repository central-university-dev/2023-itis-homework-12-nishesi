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
values (1, 101, 1001, 'name_1', 'description_1', '/item_url1', 'type_1', 'some_image', 'brand_1', 'catalogue_1'),
       (2, 101, 1001, '200002', 'description_1', '/item_url2', 'type_1', 'some_image', 'brand_1', 'catalogue_1'),
       (3, 101, 1001, 'Something 200003', 'description_1', '/item_url2', '300001', 'some_image', 'brand_1', 'catalogue_1'),
       (4, 102, 1001, '200003 Another', 'description_1', '/item_url2', '300001', 'some_image', 'brand_1', 'catalogue_2'),
       (5, 102, 1001, 'Something 200003 Another', 'description_1', '/item_url2', '300001', 'some_image', 'brand_1', 'catalogue_2'),
       (6, 102, 1001, 'Laptop', 'description_1', '/item_url2', '300001', 'some_image', 'brand_1', 'catalogue_2'),
       (7, 102, 1001, 'Gaming Laptop', 'description_1', '/item_url2', '300001', 'some_image', 'brand_1', 'catalogue_2'),
       (8, 102, 1002, 'name_2', 'description_1', '/item_url2', 'type_2', 'some_image', 'Xiaomi', 'catalogue_2'),
       (9, 111, 1011, 'redmi note 8 pro', 'description_1', '/item_url2', 'smartphone', 'some_image', 'Xiaomi', 'China'),
       (10, 112, 1012, 'galaxy 10', 'description_1', '/item_url2', 'smartphone', 'some_image', 'Samsung', 'Corey'),
       (11, 111, 1011, 'mi 14', 'desc_word', '/item_url2', 'vacuum cleaner', 'some_image', 'Xiaomi', 'China'),

       (12, 113, 1013, 'Iphone 13', 'description_1', '/item_url2', 'smartphone', 'some_image', 'Apple', 'USA'),
       (13, 113, 1013, 'Iphone 14', 'description_1', '/item_url2', 'smartphone', 'some_image', 'Apple', 'USA'),
       (15, 113, 1013, 'Mac Air', 'description_1', '/item_url2', 'notebook', 'some_image', 'Apple', 'USA'),
       (16, 112, 1014, 'MagicBook', 'description_1', '/item_url2', 'notebook', 'some_image', 'Honor', 'Corey'),
       (14, 101, 1013, 'something 13 Air', 'description_1', '/item_url2', 'type_1', 'some_image', 'brand_1', 'catalogue_1'),
       (17, 113, 1014, 'MagicBook', 'description_1', '/item_url2', 'notebook', 'some_image', 'Honor', 'USA'),
-- id used in another test        (18, 113, 1014, 'MagicBook', 'description_1', '/item_url2', 'notebook', 'some_image', 'Honor', 'USA'),

       (20, 120, 1020, 'Rav 4 2018 новый', 'new urban comfortable large', '/item_url18', 'Кроссовер', 'some_new_image', 'Toyota', 'Автомобили'),
       (24, 120, 1020, 'Rav 4 2018 БУ', 'new urban comfortable small', '/item_url18', 'Кроссовер', 'some_new_image', 'Toyota', 'Автомобили'),
       (29, 120, 1020, 'Rav 10 2018 БУ', 'standard desc', '/item_url18', 'Хэтчбек', 'some_new_image', 'Toyota', 'Автомобили'),
       (21, 120, 1021, 'Cruze 2019 новый', 'standard desc', '/item_url18', 'Кроссовер', 'some_new_image', 'Chevrolet', 'Автомобили'),
       (22, 120, 1021, 'Cruze 2019 БУ', 'standard desc', '/item_url18', 'Кроссовер', 'some_new_image', 'Chevrolet', 'Автомобили'),
       (23, 120, 1021, 'Cruze 2019 Кроссовер', 'standard desc', '/item_url18', 'автомобиль', 'some_new_image', 'Chevrolet', 'Автомобили'),
       (30, 121, 1021, 'Cruze 2019 новый', 'standard desc', '/item_url18', 'Кроссовер', 'some_new_image', 'Chevrolet', 'Электромобили'),
       (25, 121, 1021, 'Cruze 2020 БУ', 'standard desc', '/item_url18', 'Кроссовер', 'some_new_image', 'Chevrolet', 'Электромобили'),
       (26, 121, 1021, 'Rav 4 2120', 'standard desc', '/item_url18', 'Кроссовер', 'some_new_image', 'Toyota', 'Электромобили'),
       (27, 120, 1021, 'Aveo 2010 БУ', 'standard desc', '/item_url18', 'Хэтчбек', 'some_new_image', 'Chevrolet', 'Автомобили'),
       (28, 120, 1021, 'Corolla 2013 БУ', 'standard desc', '/item_url18', 'Хэтчбек', 'some_new_image', 'Toyota', 'Автомобили'),

       (31, 131, 1030, 'Trasher', 'description_1', '/item_url_1', 'Оверсайз', 'some_image', 'brand', 'Футболки'),
       (32, 131, 1030, 'Черная с черепом', 'description_1', '/item_url_1', 'Оверсайз', 'some_image', 'brand', 'Футболки'),
       (33, 131, 1030, 'Trasher', 'description_1', '/item_url_1', 'Обычный',  'some_image', 'brand', 'Футболки'),
       (34, 131, 1030, 'Белая стандартная', 'description_1', '/item_url_1', 'Обычный',  'some_image', 'brand', 'Футболки'),
       (35, 132, 1030, 'Trasher', 'description_1', '/item_url_1', 'Обычный',  'some_image', 'brand', 'Шорты'),
       (36, 132, 1030, 'Черная с черепом', 'description_1', '/item_url_1', 'Обычный',  'some_image', 'brand', 'Шорты'),
       (37, 132, 1030, 'Trasher', 'description_1', '/item_url_1', 'Оверсайз', 'some_image', 'brand', 'Шорты'),
       (38, 132, 1030, 'Белая стандартная', 'description_1', '/item_url_1', 'Оверсайз', 'some_image', 'brand', 'Шорты');

insert into item_sku (item_id, sku)
values (1, '100001');
-- used in another test        (18, '100002')

insert into remain (item_id, region_id, price)
values (1, 1, 1),
       (2, 1, 1),
       (3, 1, 1),
       (4, 1, 1),
       (5, 1, 1),
       (6, 1, 1),
       (7, 1, 1),
       (8, 1, 1),
       (9, 1, 1),
       (10, 1, 1),
       (11, 1, 1),
       (12, 1, 1),
       (13, 1, 1),
       (14, 1, 1),
       (15, 1, 1),
       (16, 1, 1),
       (17, 1, 1);

insert into catalogue (catalogue_id, realcatname, image, parent_id, name)
values (101, 'catalogue_1', 'image', 101, 'catalogue_1'),
       (102, 'catalogue_1', 'image', 101, 'catalogue_2'),
       (111, 'catalogue_1', 'image', 101, 'China'),
       (112, 'catalogue_1', 'image', 101, 'Corey'),
       (113, 'catalogue_1', 'image', 101, 'USA'),

       (120, 'catalogue_20', 'image', 120, 'Автомобили'),
       (121, 'catalogue_20', 'image', 120, 'Электромобили')