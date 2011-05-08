SET character_set_client=utf8;
SET character_set_connection=utf8;

INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)  VALUES (1, 'accessories' , 'Accessories' , 'Product accessories');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)  VALUES (2, 'up' , 'Up sell' , 'Up sell');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)  VALUES (3, 'cross' , 'Cross sell' , 'Cross sell');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)  VALUES (4, 'buywiththis' , 'Buy with' , 'Buy with');


INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1000, 'java.lang.String', 'String');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1001, 'java.lang.String', 'URI');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1002, 'java.lang.String', 'URL');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1003, 'java.lang.String', 'Image');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1004, 'java.lang.String', 'CommaSeparatedList');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1005, 'java.lang.Float', 'Float');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1006, 'java.lang.Integer', 'Integer');


INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1000, 'SYSTEM', 'System settings.', 'System wide settings');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1001, 'SHOP', 'Shop settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1002, 'CATEGORY', 'Category settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1003, 'PRODUCT', 'Product settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1004, 'SKU', 'Product sku settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1005, 'BRAND', 'Brand settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1006, 'CUSTOMER', 'Customer settings.', '');


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1000,  'SYSTEM_DEFAULT_SHOP',  1,  NULL,  'Магазин по умолчанию',  'Этот адрес будет использован, если пользователь зашел по ip адресу', 1002,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1001,  'BRAND_IMAGE',  1,  NULL,  'Изображение бренда',  null,  1003, 1005);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1002,  'CATEGORY_ITEMS_PER_PAGE',  0,  NULL,  'Отображать кол-во элементов в категории',   'Отображать кол-во элементов в категории',  1004, 1002);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1026,  'CATEGORY_SUBCATEGORIES_COLUMNS',  0,  NULL,  'Отображать кол-во подкатегорий',   'Отображать кол-во  подкатегорий',            1006, 1002);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1004,  'CATEGORY_IMAGE',  0,  NULL,  'Изображение категории',   'Изображение категории',  1003, 1002);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1005,  'CATEGORY_IMAGE_RETREIVE_STRATEGY',  0,  NULL,  'Стратегия для получения изображения категории',   'Стратегия для получения изображение категории: ATTRIBUTE - из атрибута CATEGORY_IMAGE, RANDOM_PRODUCT - изображение случайного продукта в данной категории',  1000, 1002);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1006,  'CURRENCY',  0,  NULL,  'Валюта',  'Поддерживаемы валюты, первая является основной',  1004, 1001);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1007,  'SYSTEM_IMAGE_VAULT',  1,  NULL,  'Репозиторий изображений продуктов',  'Репозиторий изображений продуктов', 1000,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1021,  'SYSTEM_IMPORT_DESCRIPTORS',  1,  NULL,  'Путь к папке с дескрипторами для импорта',  'Путь к папке с дескрипторами для импорта', 1000,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1022,  'SYSTEM_IMPORT_ARCHIVE',  1,  NULL,  'Путь к архивной папке',  'Путь к архивной папке', 1000,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1023,  'SYSTEM_IMPORT',  1,  NULL,  'Путь к папке для импорта',  'Путь к папке для импорта', 1000,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1024,  'SYSTEM_ETAG_CACHE_IMAGES_TIME',  1,  NULL,  'Время экспирации etag для изображений в минутах',  'Время экспирации etag для изображений в минутах. При изменении необходи рестарт системы', 1006,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1025,  'SYSTEM_ETAG_CACHE_PAGES_TIME',  1,  NULL,  'Время экспирации etag для страниц в минутах',  'Время экспирации etag для страниц в минутах. При изменении необходи рестарт системы', 1006,  1000);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1003,  'Материал',  0,  NULL,  'Материал',  'Материал',   1000, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  7000,  'Проба',  0,  NULL,  'Проба',  'Проба металла',           1006, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  7001,  'Цвет_металла',  0,  NULL,  'Цвет металла',  'Цвет металла',  1006, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  7002,  'Вставка',  0,  NULL,  'Вставка',  'Драгоценные вставки',      1000, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  7003,  'Цвет_вставки',  0,  NULL,  'Цвет вставки',  'Цвет вставки',   1000, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  7004,  'Размер',  0,  NULL,  'Размер',  'Размер',   1005, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1020,  'Вес',  0,  NULL,  'Вес',  'Вес',  1005, 1003);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1008,  'IMAGE0',  1,  NULL,  'Изображение продукта',  'Изображение продукта',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1009,  'IMAGE1',  0,  NULL,  'Первое альтернативное изображение продукта',  'Первое альтернативное изображение',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1010,  'IMAGE2',  0,  NULL,  'Второе альтернативное изображение',  'Второе альтернативное изображение',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1011,  'IMAGE3',  0,  NULL,  'Третье альтернативное изображение продукта',  'Третье  альтернативное изображение продукта',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1012,  'IMAGE4',  0,  NULL,  'Четвертое  альтернативное изображение продукта',  'Четвертое  альтернативное изображение продукта',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1013,  'IMAGE5',  0,  NULL,  'Пятое  альтернативное изображение продукта',  'Пятое  альтернативное изображение продукта',  1003, 1003);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1014,  'SKUIMAGE0',  0,  NULL,  'Изображение SKU продукта',  'Изображение SKU продукта',  1003, 1004);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1015,  'SKUIMAGE1',  0,  NULL,  'Первое альтернативное изображение SKU',  'Первое альтернативное изображение SKU',  1003, 1004);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1016,  'SKUIMAGE2',  0,  NULL,  'Второе альтернативное изображение SKU',  'Второе альтернативное изображение SKU',  1003, 1004);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1017,  'SKUIMAGE3',  0,  NULL,  'Третье альтернативное изображение SKU',  'Третье альтернативное изображение SKU',  1003, 1004);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1018,  'SKUIMAGE4',  0,  NULL,  'Четвертое альтернативное изображение SKU',  'Четвертое альтернативное изображение SKU',  1003, 1004);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1019,  'SKUIMAGE5',  0,  NULL,  'Пятое альтернативное изображение SKU',  'Пятое альтернативное изображение SKU',  1003, 1004);


INSERT INTO TATTRVIEWGROUP (ATTRVIEWGROUP_ID, NAME, DESCRIPTION) VALUES (1,'Материал','Материал, проба, цвет');
INSERT INTO TATTRVIEWGROUP (ATTRVIEWGROUP_ID, NAME, DESCRIPTION) VALUES (2,'Вставка','Вставка и цвет вставки');
INSERT INTO TATTRVIEWGROUP (ATTRVIEWGROUP_ID, NAME, DESCRIPTION) VALUES (3,'Размер','Размер и Вес');


INSERT INTO TNPASYSTEM (NPASYSTEM_ID, CODE, NAME, DESCRIPTION) VALUES (100,'NPASYSTEM','Npa ecommerce system', 'System table');

INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1000,'http://shop.enigma.biz.ua/webshopwicket','SYSTEM_DEFAULT_SHOP',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1001,'/npa/default','SYSTEM_DEFAULT_FSPOINTER',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1002,'9,12,18','SEARCH_ITEMS_PER_PAGE',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1003,'/npa/imagevault','SYSTEM_IMAGE_VAULT',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1004,'/npa/import/descriptors','SYSTEM_IMPORT_DESCRIPTORS',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1005,'/npa/import/archive','SYSTEM_IMPORT_ARCHIVE',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1006,'/npa/import','SYSTEM_IMPORT',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1007,'60','SYSTEM_ETAG_CACHE_IMAGES_TIME',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1008,'60','SYSTEM_ETAG_CACHE_PAGES_TIME',100);




INSERT INTO TSHOP (SHOP_ID, NAME, DESCRIPTION, FSPOINTER, CODE)
  VALUES (40, 'Энигма. Магазин ювелирных изделий', 'Энигма. Магазин ювелирных изделий', '/npa/jewelry', 'JEWEL_SHOP');

INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID)   VALUES (40, 'USD,UAH', 'CURRENCY', 40);


INSERT INTO TSTOREEXCHANGERATE (SHOPEXCHANGERATE_ID, FROMCURRENCY, TOCURRENCY, SHOP_ID, RATE)   VALUES(10,'USD','UAH', 40, 8.05);



INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL )  VALUES (40, 40, 'shop.enigma.biz.ua');



INSERT INTO TAVAILABILITY ( AVAILABILITY_ID,NAME,DESCRIPTION) VALUES (1,'Стандарт','Показывать при наявности на складе');
INSERT INTO TAVAILABILITY ( AVAILABILITY_ID,NAME,DESCRIPTION) VALUES (2,'Предварительный заказ','Предварительный заказ' );
INSERT INTO TAVAILABILITY ( AVAILABILITY_ID,NAME,DESCRIPTION) VALUES (4,'Заказ без наявности','Заказ без наявности на складе' );
INSERT INTO TAVAILABILITY ( AVAILABILITY_ID,NAME,DESCRIPTION) VALUES (8,'Всегда','Всегда' );



INSERT INTO TBRAND(BRAND_ID, NAME, DESCRIPTION) VALUES(50,'Enigma','Enigma');



INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE) VALUES (100, 100, 0, 'root', 'The root category','default');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID) VALUES (20,'CATEGORY_ITEMS_PER_PAGE','9,12,18', 100);
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID) VALUES (19,'CATEGORY_SUBCATEGORIES_COLUMNS','2', 100);



INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (500,'Браслет','Браслет','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50001,          'Материал',      500,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50002,          'Проба',         500,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50003,          'Цвет_металла',  500,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50004,          'Вставка',       500,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50005,          'Цвет_вставки', 500,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50006,          'Размер',        500,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50007,          'Вес',          500,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>'
);

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50001, 500, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50002, 500, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50003, 500, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3000, 'Браслеты','Браслеты','Браслеты, украшения, золото, купить, киев, подарок','Купить золотые серебрянные браслеты в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3000,
  100,
  5,
  'Браслеты',
  'Браслеты',
  1,
  1,
  1,
  500,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3000);


INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (501,'Брелок','Брелок','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50101,          'Материал',      501,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50102,          'Проба',         501,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50103,          'Цвет_металла',  501,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50104,          'Вставка',       501,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50105,          'Цвет_вставки', 501,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50106,          'Размер',        501,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50107,          'Вес',          501,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50101, 501, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50102, 501, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50103, 501, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3001, 'Брелки','Брелки','Брелки, брелок, украшения, золото, купить, киев, подарок','Купить золотые серебрянные брелки в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3001,
  100,
  5,
  'Брелки',
  'Брелки',
  1,
  1,
  1,
  501,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3001);


INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (502,'Брошка','Брошка','default', 0,0,1);
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50201,          'Материал',      502,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50202,          'Проба',         502,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50203,          'Цвет_металла',  502,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50204,          'Вставка',       502,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50205,          'Цвет_вставки', 502,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50206,          'Размер',        502,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50207,          'Вес',          502,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50201, 502, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50202, 502, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50203, 502, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3003, 'Брошки','Брошки','Броши, украшения, золото, купить, киев, подарок','Купить золотые серебрянные броши в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3002,
  100,
  5,
  'Брошки',
  'Брошки',
  1,
  1,
  1,
  502,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3003);


INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (503,'Булавка','Булавка','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50301,          'Материал',      503,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50302,          'Проба',         503,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50303,          'Цвет_металла',  503,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50304,          'Вставка',       503,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50305,          'Цвет_вставки', 503,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50306,          'Размер',        503,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50307,          'Вес',          503,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50301, 503, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50302, 503, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50303, 503, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3004, 'Булавки','Булавки','Булавки, булавка, украшения, золото, купить, киев, подарок','Купить золотые серебрянные булавки в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3003,
  100,
  5,
  'Булавки',
  'Булавки',
  1,
  1,
  1,
  503,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3004);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (504,'Зажим для галстука','Зажим для галстука','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50401,          'Материал',      504,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50402,          'Проба',         504,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50403,          'Цвет_металла',  504,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50404,          'Вставка',       504,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50405,          'Цвет_вставки', 504,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50406,          'Размер',        504,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50407,          'Вес',          504,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50401, 504, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50402, 504, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50403, 504, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3005, 'Зажимы для галстука','Зажимы для галстука','Зажимы для галстука, зажим, галстук, брелок, украшения, золото, купить, киев, подарок','Купить золотые серебрянные зажимы для галстука в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3004,
  100,
  5,
  'Зажимы для галстука',
  'Зажимы для галстука',
  1,
  1,
  1,
  504,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3005);


INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (505,'Запонки','Запонки','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50501,          'Материал',      505,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50502,          'Проба',         505,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50503,          'Цвет_металла',  505,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50504,          'Вставка',       505,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50505,          'Цвет_вставки', 505,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50506,          'Размер',        505,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50507,          'Вес',          505,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50501, 505, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50502, 505, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50503, 505, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3006, 'Запонки','Запонки','Запонки, украшения, золото, купить, киев, подарок','Купить золотые серебрянные запонки в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3005,
  100,
  5,
  'Запонки',
  'Запонки',
  1,
  1,
  1,
  505,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3006);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (506,'Колье','Колье','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50601,          'Материал',      506,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50602,          'Проба',         506,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50603,          'Цвет_металла',  506,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50604,          'Вставка',       506,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50605,          'Цвет_вставки', 506,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50606,          'Размер',        506,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50607,          'Вес',          506,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50601, 506, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50602, 506, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50603, 506, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3007, 'Колье','Колье','Колье, украшения, золото, купить, киев, подарок','Купить золотые серебрянные колье в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3006,
  100,
  5,
  'Колье',
  'Колье',
  1,
  1,
  1,
  506,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3007);


INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (507,'Кольцо','Кольцо','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50701,          'Материал',      507,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50702,          'Проба',         507,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50703,          'Цвет_металла',  507,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50704,          'Вставка',       507,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50705,          'Цвет_вставки', 507,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50706,          'Размер',        507,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50707,          'Вес',          507,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50701, 507, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50702, 507, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50703, 507, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3008, 'Кольца','Кольца','Кольца, обручальные кольца, украшения, золото, купить, киев, подарок','Купить золотые серебрянные кольца в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3007,
  100,
  5,
  'Кольца',
  'Кольца',
  1,
  1,
  1,
  507,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3008);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (508,'Крестик','Крестик','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50801,          'Материал',      508,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50802,          'Проба',         508,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50803,          'Цвет_металла',  508,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50804,          'Вставка',       508,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50805,          'Цвет_вставки', 508,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50806,          'Размер',        508,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50807,          'Вес',          508,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50801, 508, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50802, 508, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50803, 508, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3009, 'Крестики','Крестики','Крестики, украшения, золото, купить, киев, подарок','Купить золотые серебрянные крестики в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3008,
  100,
  5,
  'Крестики',
  'Крестики',
  1,
  1,
  1,
  508,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3009);




INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (509,'Кулон','Кулон','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50901,          'Материал',      509,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50902,          'Проба',         509,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50903,          'Цвет_металла',  509,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50904,          'Вставка',       509,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50905,          'Цвет_вставки', 509,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50906,          'Размер',        509,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50907,          'Вес',          509,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50901, 509, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50902, 509, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (50903, 509, 3, 'Размер,Вес', 3);

INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3010, 'Кулоны','Кулоны','Кулоны, украшения, золото, купить, киев, подарок','Купить золотые серебрянные кулоны в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3009,
  100,
  5,
  'Кулоны',
  'Кулоны',
  1,
  1,
  1,
  509,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3010);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (510,'Перстень','Перстень','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51001,          'Материал',      510,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51002,          'Проба',         510,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51003,          'Цвет_металла',  510,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51004,          'Вставка',       510,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51005,          'Цвет_вставки', 510,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51006,          'Размер',        510,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51007,          'Вес',          510,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51001, 510, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51002, 510, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51003, 510, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3011, 'Перстни','Перстни','Перстни, украшения, золото, купить, киев, подарок','Купить золотые серебрянные перстни в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3010,
  100,
  5,
  'Перстни',
  'Перстни',
  1,
  1,
  1,
  510,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3011);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (511,'Пирсинг','Пирсинг','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51101,          'Материал',      511,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51102,          'Проба',         511,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51103,          'Цвет_металла',  511,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51104,          'Вставка',       511,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51105,          'Цвет_вставки', 511,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51106,          'Размер',        511,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51107,          'Вес',          511,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51101, 511, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51102, 511, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51103, 511, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3012, 'Пирсинг','Пирсинг','Пирсинг, украшения, золото, купить, киев, подарок','Купить золотой серебрянный пирсинг в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3011,
  100,
  5,
  'Пирсинг',
  'Пирсинг',
  1,
  1,
  1,
  511,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3012);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (512,'Серьги','Серьги','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51201,          'Материал',      512,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51202,          'Проба',         512,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51203,          'Цвет_металла',  512,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51204,          'Вставка',       512,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51205,          'Цвет_вставки', 512,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51206,          'Размер',        512,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51207,          'Вес',          512,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51201, 512, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51202, 512, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51203, 512, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3013, 'Серьги','Серьги','Серьги, украшения, золото, купить, киев, подарок','Купить золотые серебрянные серьги в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3012,
  100,
  5,
  'Серьги',
  'Серьги',
  1,
  1,
  1,
  512,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3013);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (513,'Цепочка','Цепочка','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51301,          'Материал',      513,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51302,          'Проба',         513,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51303,          'Цвет_металла',  513,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51304,          'Вставка',       513,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51305,          'Цвет_вставки', 513,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51306,          'Размер',        513,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51307,          'Вес',          513,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51301, 513, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51302, 513, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51303, 513, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3014, 'Цепочки','Цепочки','Цепочки, украшения, золото, купить, киев, подарок','Купить золотые серебрянные цепочки в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3013,
  100,
  5,
  'Цепочки',
  'Цепочки',
  1,
  1,
  1,
  513,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3014);



INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)  VALUES (514,'Подвеска','Подвеска','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51401,          'Материал',      514,      50 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51402,          'Проба',         514,      55 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51403,          'Цвет_металла',  514,      60 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51404,          'Вставка',       514,      65 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51405,          'Цвет_вставки', 514,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51406,          'Размер',        514,      70 ,   1,         1,        1, 'S' ,       '');
INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (51407,          'Вес',          514,      70 ,   1,         1,        1, 'R' ,
'<rangeList serialization="custom"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class="string">0.10</first><second class="string">1.00</second></range></range><range><range><first class="string">1.00</first><second class="string">2.00</second></range></range><range><range><first class="string">2.00</first><second class="string">3.00</second></range></range><range><range><first class="string">3.00</first><second class="string">4.00</second></range></range><range><range><first class="string">4.00</first><second class="string">5.00</second></range></range><range><range><first class="string">5.00</first><second class="string">6.00</second></range></range><range><range><first class="string">6.00</first><second class="string">7.00</second></range></range><range><range><first class="string">7.00</first><second class="string">8.00</second></range></range><range><range><first class="string">8.00</first><second class="string">10.00</second></range></range><range><range><first class="string">10.00</first><second class="string">20.00</second></range></range></list></rangeList>');

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51401, 514, 1, 'Материал,Проба,Цвет_металла', 1);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51402, 514, 2, 'Вставка,Цвет_вставки', 2);
INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)   VALUES (51403, 514, 3, 'Размер,Вес', 3);


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 3015, 'Подвески','Подвески','Подвески, украшения, золото, купить, киев, подарок','Купить золотые серебрянные подвески в интернет-магазине ювелирных изделий киев');
INSERT INTO TCATEGORY(
  CATEGORY_ID,
  PARENT_ID,
  RANK,
  NAME,
  DESCRIPTION,
  NAV_BY_ATTR,
  NAV_BY_BRAND,
  NAV_BY_PRICE,
  PRODUCTTYPE_ID,
  NAV_BY_PRICE_TIERS,
  SEO_ID )
VALUES (
  3014,
  100,
  5,
  'Подвески',
  'Подвески',
  1,
  1,
  1,
  514,
  '<pricetree><priceMap><entry><string>USD</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">30</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">31</first><second class="big-decimal">60</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">61</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">101</first><second class="big-decimal">150</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">151</first><second class="big-decimal">200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">201</first><second class="big-decimal">250</second></priceRange></pricenode></list></entry><entry><string>UAH</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">250</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">251</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">501</first><second class="big-decimal">800</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">801</first><second class="big-decimal">1200</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1201</first><second class="big-decimal">1600</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1601</first><second class="big-decimal">2000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
  3015);



INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3000,10,40,3000);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3001,10,40,3001);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3002,10,40,3002);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3003,10,40,3003);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3004,10,40,3004);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3005,10,40,3005);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3006,10,40,3006);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3007,10,40,3007);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3008,10,40,3008);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3009,10,40,3009);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3010,10,40,3010);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3011,10,40,3011);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3012,10,40,3012);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3013,10,40,3013);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES (3014,10,40,3014);


INSERT INTO TWAREHOUSE (WAREHOUSE_ID, CODE, NAME, DESCRIPTION) VALUES (1, 'Основной', 'Основной склад', null);

INSERT INTO TSHOPWAREHOUSE(SHOPWAREHOUSE_ID,SHOP_ID,WAREHOUSE_ID) VALUES (1, 40 ,1);


INSERT INTO TROLE (CODE, DESCRIPTION) VALUES ('WICKETSHOP_ADMIN', 'Доступ к администраторской панели в веб магазине');
INSERT INTO TROLE (CODE, DESCRIPTION) VALUES ('ROLE_SMADMIN', 'Администратор системы');


INSERT INTO TMANAGER (EMAIL, FIRST_NAME, LAST_NAME, PASSWORD) VALUES ('wowa17@gmail.com', 'Vladimir', 'Evdakov', '8f79796a9d839d15114abab966bbf4eb');

INSERT INTO TMANAGER (EMAIL, FIRST_NAME, LAST_NAME, PASSWORD) VALUES ('iazarny@yahoo.com', 'Igor', 'Azarny', '3ec52eb91369d1494ca4d9a4247fdc21');

INSERT INTO TMANAGERROLE (EMAIL, CODE) VALUES ('wowa17@gmail.com', 'WICKETSHOP_ADMIN');
INSERT INTO TMANAGERROLE (EMAIL, CODE) VALUES ('wowa17@gmail.com', 'ROLE_SMADMIN');
INSERT INTO TMANAGERROLE (EMAIL, CODE) VALUES ('iazarny@yahoo.com', 'WICKETSHOP_ADMIN');
INSERT INTO TMANAGERROLE (EMAIL, CODE) VALUES ('iazarny@yahoo.com', 'ROLE_SMADMIN');



