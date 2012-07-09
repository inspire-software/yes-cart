SET character_set_client=utf8;
SET character_set_connection=utf8;

INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)  VALUES (1, 'accessories' , 'Аксессуары' , 'Продуктовые аксессуары');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)  VALUES (2, 'up' , 'Более дорогой продукт (Up sell)' , 'Более дорогой продукт(Up sell)');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)  VALUES (3, 'cross' , 'Подходящий продукт другого типа (Cross sell)' , 'Подходящий продукт другого типа (Cross sell)');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)  VALUES (4, 'buywiththis' , 'Обычно покупают вместе' , 'Обычно покупают вместе');


INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1000, 'java.lang.String', 'String');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1001, 'java.lang.String', 'URI');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1002, 'java.lang.String', 'URL');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1003, 'java.lang.String', 'Image');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1004, 'java.lang.String', 'CommaSeparatedList');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1005, 'java.lang.Float', 'Float');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1006, 'java.lang.Integer', 'Integer');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE) VALUES (1007, 'java.lang.String', 'Phone');


INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1000, 'SYSTEM', 'Атрибуты системы', 'Настройки системы в целом');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1001, 'SHOP', 'Атрибуты магазинов', 'Настройки магазинов');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1002, 'CATEGORY', 'Атрибуты  категорий', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1003, 'PRODUCT', 'Атрибуты  продуктов', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1004, 'SKU', 'Атрибуты SKU', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1005, 'BRAND', 'Атрибуты брендов', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1006, 'CUSTOMER', 'Атрибуты покупателей', '');


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
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1027,  'SHOP_MAIL_FROM',  0,  NULL,  'Обратный почтовый адрес',  'Обратный почтовый адрес магазина',  1000, 1001);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1028,  'SYSTEM_MAILTEMPLATES_FSPOINTER',  1,  NULL,  'Путь к папке с почтовыми шаблонами',  'Путь к папке с почтовыми шаблонами', 1000,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1031,  'SYSTEM_PAYMENT_MODULES_URLS',  0,  NULL,  'Список URL платежных модулей',  'Список URL платежных модулей. Каждый модуль может иметь несколько платежных шлюзов', 1004,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1032,  'SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABELS',  0,  NULL,  'Активные шлюзы через которые можно производить оплату',  'Активные шлюзы через которые можно производить оплату', 1004,  1000);



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

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1030,  'CUSTOMER_PHONE',  1,  NULL,  'Телефон покупателя',  'Телефон покупателя', 1007,  1006);


INSERT INTO TATTRVIEWGROUP (ATTRVIEWGROUP_ID, NAME, DESCRIPTION) VALUES (1,'Материал','Материал, проба, цвет');
INSERT INTO TATTRVIEWGROUP (ATTRVIEWGROUP_ID, NAME, DESCRIPTION) VALUES (2,'Вставка','Вставка и цвет вставки');
INSERT INTO TATTRVIEWGROUP (ATTRVIEWGROUP_ID, NAME, DESCRIPTION) VALUES (3,'Размер','Размер и Вес');


INSERT INTO TNPASYSTEM (NPASYSTEM_ID, CODE, NAME, DESCRIPTION) VALUES (100,'NPASYSTEM','Npa ecommerce system', 'System table');

INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1000,'http://shop2.enigma.biz.ua/webshopwicket','SYSTEM_DEFAULT_SHOP',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1001,'/npa/default','SYSTEM_DEFAULT_FSPOINTER',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1002,'9,12,18','SEARCH_ITEMS_PER_PAGE',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1003,'/npa/imagevault','SYSTEM_IMAGE_VAULT',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1004,'/npa/import/descriptors','SYSTEM_IMPORT_DESCRIPTORS',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1005,'/npa/import/archive','SYSTEM_IMPORT_ARCHIVE',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1006,'/npa/import','SYSTEM_IMPORT',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1007,'60','SYSTEM_ETAG_CACHE_IMAGES_TIME',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1008,'60','SYSTEM_ETAG_CACHE_PAGES_TIME',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1010,'/npa/mailtemplates/','SYSTEM_MAILTEMPLATES_FSPOINTER',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1011,'basePaymentModule,cappPaymentModule','SYSTEM_PAYMENT_MODULES_URLS',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)  VALUES (1012,'testPaymentGatewayLabel,courierPaymentGatewayLabel,cyberSourcePaymentGatewayLabel,authorizeNetAimPaymentGatewayLabel,authorizeNetSimPaymentGatewayLabel,payflowPaymentGatewayLabel,payPalNvpPaymentGatewayLabel','SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABELS',100);





INSERT INTO TSHOP (SHOP_ID, NAME, DESCRIPTION, FSPOINTER, CODE)
  VALUES (40, 'Энигма. Магазин ювелирных изделий', 'Энигма. Магазин ювелирных изделий', '/npa/jewelry', 'JEWEL_SHOP');

INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID)   VALUES (40, 'USD,UAH', 'CURRENCY', 40);
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID)   VALUES (41, 'noreply@shop2.enigma.biz.ua', 'SHOP_MAIL_FROM', 40);



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



INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 1 ,'AF', '4' ,'Afghanistan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 2 ,'AL', '8' ,'Albania');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 3 ,'DZ', '12' ,'Algeria');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 4 ,'AS', '16' ,'American Samoa');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 5 ,'AD', '20' ,'Andorra');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 6 ,'AO', '24' ,'Angola');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 7 ,'AI', '660' ,'Anguilla');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 8 ,'AQ', '10' ,'Antarctica');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 9 ,'AG', '28' ,'Antigua And Barbuda');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 10 ,'AR', '32' ,'Argentina');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 11 ,'AM', '51' ,'Armenia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 12 ,'AW', '533' ,'Aruba');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 13 ,'AU', '36' ,'Australia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 14 ,'AT', '40' ,'Austria');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 15 ,'AZ', '31' ,'Azerbaijan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 16 ,'BS', '44' ,'Bahamas');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 17 ,'BH', '48' ,'Bahrain');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 18 ,'BD', '50' ,'Bangladesh');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 19 ,'BB', '52' ,'Barbados');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 20 ,'BY', '112' ,'Belarus');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 21 ,'BE', '56' ,'Belgium');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 22 ,'BZ', '84' ,'Belize');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 23 ,'BJ', '204' ,'Benin');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 24 ,'BM', '60' ,'Bermuda');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 25 ,'BT', '64' ,'Bhutan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 26 ,'BO', '68' ,'Bolivia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 27 ,'BA', '70' ,'Bosnia And Herzegovina');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 28 ,'BW', '72' ,'Botswana');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 29 ,'BV', '74' ,'Bouvet Island');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 30 ,'BR', '76' ,'Brazil');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 31 ,'IO', '86' ,'British Indian Ocean Territory');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 32 ,'BN', '96' ,'Brunei Darussalam');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 33 ,'BG', '100' ,'Bulgaria');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 34 ,'BF', '854' ,'Burkina Faso');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 35 ,'BI', '108' ,'Burundi');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 36 ,'KH', '116' ,'Cambodia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 37 ,'CM', '120' ,'Cameroon');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 38 ,'CA', '124' ,'Canada');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 39 ,'CV', '132' ,'Cape Verde');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 40 ,'KY', '136' ,'Cayman Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 41 ,'CF', '140' ,'Central African Republic');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 42 ,'TD', '148' ,'Chad');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 43 ,'CL', '152' ,'Chile');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 44 ,'CN', '156' ,'China');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 45 ,'CX', '162' ,'Christmas Island');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 46 ,'CC', '166' ,'Cocos (Keeling);Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 47 ,'CO', '170' ,'Colombia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 48 ,'KM', '174' ,'Comoros');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 49 ,'CG', '178' ,'Congo');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 50 ,'CK', '184' ,'Cook Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 51 ,'CR', '188' ,'Costa Rica');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 52 ,'CI', '384' ,'Cote DIvoire');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 53 ,'HR', '191' ,'Croatia (Local Name: Hrvatska)');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 54 ,'CU', '192' ,'Cuba');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 55 ,'CY', '196' ,'Cyprus');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 56 ,'CZ', '203' ,'Czech Republic');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 57 ,'DK', '208' ,'Denmark');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 58 ,'DJ', '262' ,'Djibouti');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 59 ,'DM', '212' ,'Dominica');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 60 ,'DO', '214' ,'Dominican Republic');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 61 ,'EC', '218' ,'Ecuador');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 62 ,'EG', '818' ,'Egypt');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 63 ,'SV', '222' ,'El Salvador');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 64 ,'GQ', '226' ,'Equatorial Guinea');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 65 ,'ER', '232' ,'Eritrea');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 66 ,'EE', '233' ,'Estonia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 67 ,'ET', '210' ,'Ethiopia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 68 ,'FK', '238' ,'Falkland Islands (Malvinas);');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 69 ,'FO', '234' ,'Faroe Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 70 ,'FJ', '242' ,'Fiji');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 71 ,'FI', '246' ,'Finland');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 72 ,'FR', '250' ,'France');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 73 ,'FX', '249' ,'France, Metropolitan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 74 ,'GF', '254' ,'French Guiana');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 75 ,'PF', '258' ,'French Polynesia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 76 ,'TF', '260' ,'French Southern Territories');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 77 ,'GA', '266' ,'Gabon');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 78 ,'GM', '270' ,'Gambia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 79 ,'GE', '268' ,'Georgia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 80 ,'DE', '276' ,'Germany');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 81 ,'GH', '288' ,'Ghana');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 82 ,'GI', '292' ,'Gibraltar');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 83 ,'GR', '300' ,'Greece');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 84 ,'GL', '304' ,'Greenland');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 85 ,'GD', '308' ,'Grenada');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 86 ,'GP', '312' ,'Guadeloupe');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 87 ,'GU', '316' ,'Guam');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 88 ,'GT', '320' ,'Guatemala');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 89 ,'GN', '324' ,'Guinea');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 90 ,'GW', '624' ,'Guinea-Bissau');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 91 ,'GY', '328' ,'Guyana');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 92 ,'HT', '332' ,'Haiti');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 93 ,'HM', '334' ,'Heard Island and Mcdonald Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 94 ,'HN', '340' ,'Honduras');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 95 ,'HK', '344' ,'Hong Kong');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 96 ,'HU', '348' ,'Hungary');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 97 ,'IS', '352' ,'Iceland');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 98 ,'IN', '356' ,'India');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 99 ,'ID', '360' ,'Indonesia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 100 ,'IR', '364' ,'Iran, Islamic Republic Of');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 101 ,'IQ', '368' ,'Iraq');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 102 ,'IE', '372' ,'Ireland');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 103 ,'IL', '376' ,'Israel');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 104 ,'IT', '380' ,'Italy');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 105 ,'JM', '388' ,'Jamaica');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 106 ,'JP', '392' ,'Japan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 107 ,'JO', '400' ,'Jordan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 108 ,'KZ', '398' ,'Kazakhstan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 109 ,'KE', '404' ,'Kenya');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 110 ,'KI', '296' ,'Kiribati');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 111 ,'KP', '408' ,'Korea, Democratic People Republic Of');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 112 ,'KR', '410' ,'Korea, Republic Of');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 113 ,'KW', '414' ,'Kuwait');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 114 ,'KG', '417' ,'Kyrgyzstan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 115 ,'LA', '418' ,'Lao People Democratic Republic');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 116 ,'LV', '428' ,'Latvia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 117 ,'LB', '422' ,'Lebanon');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 118 ,'LS', '426' ,'Lesotho');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 119 ,'LR', '430' ,'Liberia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 120 ,'LY', '434' ,'Libyan Arab Jamahiriya');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 121 ,'LI', '438' ,'Liechtenstein');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 122 ,'LT', '440' ,'Lithuania');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 123 ,'LU', '442' ,'Luxembourg');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 124 ,'MO', '446' ,'Macau');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 125 ,'MK', '807' ,'Macedonia, The Former Yugoslav Republic Of');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 126 ,'MG', '450' ,'Madagascar');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 127 ,'MW', '454' ,'Malawi');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 128 ,'MY', '458' ,'Malaysia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 129 ,'MV', '462' ,'Maldives');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 130 ,'ML', '466' ,'Mali');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 131 ,'MT', '470' ,'Malta');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 132 ,'MH', '584' ,'Marshall Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 133 ,'MQ', '474' ,'Martinique');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 134 ,'MR', '478' ,'Mauritania');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 135 ,'MU', '480' ,'Mauritius');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 136 ,'YT', '175' ,'Mayotte');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 137 ,'MX', '484' ,'Mexico');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 138 ,'FM', '583' ,'Micronesia, Federated States Of');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 139 ,'MD', '498' ,'Moldova, Republic Of');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 140 ,'MC', '492' ,'Monaco');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 141 ,'MN', '496' ,'Mongolia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 142 ,'MS', '500' ,'Montserrat');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 143 ,'MA', '504' ,'Morocco');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 144 ,'MZ', '508' ,'Mozambique');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 145 ,'MM', '104' ,'Myanmar');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 146 ,'NA', '516' ,'Namibia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 147 ,'NR', '520' ,'Nauru');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 148 ,'NP', '524' ,'Nepal');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 149 ,'NL', '528' ,'Netherlands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 150 ,'AN', '530' ,'Netherlands Antilles');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 151 ,'NC', '540' ,'New Caledonia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 152 ,'NZ', '554' ,'New Zealand');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 153 ,'NI', '558' ,'Nicaragua');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 154 ,'NE', '562' ,'Niger');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 155 ,'NG', '566' ,'Nigeria');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 156 ,'NU', '570' ,'Niue');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 157 ,'NF', '574' ,'Norfolk Island');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 158 ,'MP', '580' ,'Northern Mariana Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 159 ,'NO', '578' ,'Norway');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 160 ,'OM', '512' ,'Oman');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 161 ,'PK', '586' ,'Pakistan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 162 ,'PW', '585' ,'Palau');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 163 ,'PA', '591' ,'Panama');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 164 ,'PG', '598' ,'Papua New Guinea');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 165 ,'PY', '600' ,'Paraguay');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 166 ,'PE', '604' ,'Peru');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 167 ,'PH', '608' ,'Philippines');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 168 ,'PN', '612' ,'Pitcairn');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 169 ,'PL', '616' ,'Poland');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 170 ,'PT', '620' ,'Portugal');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 171 ,'PR', '630' ,'Puerto Rico');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 172 ,'QA', '634' ,'Qatar');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 173 ,'RE', '638' ,'Reunion');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 174 ,'RO', '642' ,'Romania');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 175 ,'RU', '643' ,'Russian Federation');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 176 ,'RW', '646' ,'Rwanda');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 177 ,'SH', '654' ,'Saint Helena');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 178 ,'KN', '659' ,'Saint Kitts And Nevis');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 179 ,'LC', '662' ,'Saint Lucia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 180 ,'PM', '666' ,'Saint Pierre And Miquelon');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 181 ,'VC', '670' ,'Saint Vincent And The Grenadines');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 182 ,'WS', '882' ,'Samoa');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 183 ,'SM', '674' ,'San Marino');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 184 ,'ST', '678' ,'Sao Tome And Principe');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 185 ,'SA', '682' ,'Saudi Arabia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 186 ,'SN', '686' ,'Senegal');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 187 ,'SC', '690' ,'Seychelles');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 188 ,'SL', '694' ,'Sierra Leone');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 189 ,'SG', '702' ,'Singapore');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 190 ,'SK', '703' ,'Slovakia (Slovak Republic);');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 191 ,'SI', '705' ,'Slovenia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 192 ,'SB', '90' ,'Solomon Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 193 ,'SO', '706' ,'Somalia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 194 ,'ZA', '710' ,'South Africa');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 195 ,'ES', '724' ,'Spain');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 196 ,'LK', '144' ,'Sri Lanka');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 197 ,'SD', '736' ,'Sudan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 198 ,'SR', '740' ,'Suriname');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 199 ,'SJ', '744' ,'Svalbard And Jan Mayen Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 200 ,'SZ', '748' ,'Swaziland');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 201 ,'SE', '752' ,'Sweden');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 202 ,'CH', '756' ,'Switzerland');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 203 ,'SY', '760' ,'Syrian Arab Republic');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 204 ,'TW', '158' ,'Taiwan, Province Of China');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 205 ,'TJ', '762' ,'Tajikistan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 206 ,'TZ', '834' ,'Tanzania, United Republic Of');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 207 ,'TH', '764' ,'Thailand');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 208 ,'TG', '768' ,'Togo');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 209 ,'TK', '772' ,'Tokelau');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 210 ,'TO', '776' ,'Tonga');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 211 ,'TT', '780' ,'Trinidad And Tobago');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 212 ,'TN', '788' ,'Tunisia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 213 ,'TR', '792' ,'Turkey');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 214 ,'TM', '795' ,'Turkmenistan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 215 ,'TC', '796' ,'Turks And Caicos Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 216 ,'TV', '798' ,'Tuvalu');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 217 ,'UG', '800' ,'Uganda');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 218 ,'UA', '804' ,'Ukraine');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 219 ,'AE', '784' ,'United Arab Emirates');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 220 ,'GB', '826' ,'United Kingdom');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 221 ,'US', '840' ,'United States');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 222 ,'UM', '581' ,'United States Minor Outlying Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 223 ,'UY', '858' ,'Uruguay');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 224 ,'UZ', '860' ,'Uzbekistan');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 225 ,'VU', '548' ,'Vanuatu');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 226 ,'VA', '336' ,'Vatican City State (Holy SeE)');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 227 ,'VE', '862' ,'Venezuela');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 228 ,'VN', '704' ,'Viet Nam');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 229 ,'VG', '92' ,'Virgin Islands (British);');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 230 ,'VI', '850' ,'Virgin Islands (U.S.);');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 231 ,'WF', '876' ,'Wallis And Futuna Islands');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 232 ,'EH', '732' ,'Western Sahara');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 233 ,'YE', '887' ,'Yemen');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 234 ,'YU', '891' ,'Yugoslavia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 235 ,'ZR', '180' ,'Zaire');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 236 ,'ZM', '894' ,'Zambia');
INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME) VALUES ( 237 ,'ZW', '716' ,'Zimbabwe');


INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 1 ,'ZW', 'ZP' ,'Zhopa');



INSERT INTO TCARRIER (CARRIER_ID, NAME, DESCRIPTION, WORLDWIDE, COUNTRY, STATE, LOCAL)    VALUES (1, 'Все услуги', 'Курьерская служба', 1, 1, 1, 1);
INSERT INTO TCARRIER (CARRIER_ID, NAME, DESCRIPTION, WORLDWIDE, COUNTRY, STATE, LOCAL)    VALUES (2, 'Только по стране', 'Курьерская служба', 0, 1, 1, 1);
INSERT INTO TCARRIER (CARRIER_ID, NAME, DESCRIPTION, WORLDWIDE, COUNTRY, STATE, LOCAL)    VALUES (3, 'Только по региону', 'Курьерская служба', 0, 0, 1, 1);
INSERT INTO TCARRIER (CARRIER_ID, NAME, DESCRIPTION, WORLDWIDE, COUNTRY, STATE, LOCAL)    VALUES (4, 'Только локально', 'Курьерская служба', 0, 0, 0, 1);

INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (1, 1, 'Доставка по миру',    'RUB', 5, 'F', 900);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (2, 1, 'Доставка по стране',  'RUB', 4, 'F', 700);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (3, 1, 'Доставка по региону', 'RUB', 3, 'F', 600);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (4, 1, 'Доставка по городу',  'RUB', 1, 'F', 300);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (5, 1, 'Доставка по миру',    'UAH', 5, 'F', 240);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (6, 1, 'Доставка по стране',  'UAH', 4, 'F', 160);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (7, 1, 'Доставка по региону', 'UAH', 3, 'F', 140);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (8, 1, 'Доставка по городу',  'UAH', 1, 'F', 25);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (9, 1, 'Доставка по миру',     'USD', 5, 'F', 30);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (10, 1, 'Доставка по стране',  'USD', 4, 'F', 25);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (11, 1, 'Доставка по региону', 'USD', 3, 'F', 20);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (12, 1, 'Доставка по городу',  'USD', 1, 'F', 10);

INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (13, 2, 'Доставка по стране',  'RUB', 4, 'F', 650);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (14, 2, 'Доставка по региону', 'RUB', 2, 'F', 550);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (15, 2, 'Доставка по городу',  'RUB', 1, 'F', 290);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (16, 2, 'Доставка по стране',  'UAH', 4, 'F', 150);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (17, 2, 'Доставка по региону', 'UAH', 2, 'F', 135);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (18, 2, 'Доставка по городу',  'UAH', 1, 'F', 25);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (19, 2, 'Доставка по стране',  'USD', 4, 'F', 20);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (20, 2, 'Доставка по региону', 'USD', 2, 'F', 15);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (21, 2, 'Доставка по городу',  'USD', 1, 'F', 10);

INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (22, 3, 'Доставка по региону', 'RUB', 3, 'F', 540);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (23, 3, 'Доставка по городу',  'RUB', 1, 'F', 280);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (24, 3, 'Доставка по региону', 'UAH', 3, 'F', 160);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (25, 3, 'Доставка по городу',  'UAH', 1, 'F', 40);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (26, 3, 'Доставка по региону', 'USD', 3, 'F', 10);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (27, 3, 'Доставка по городу',  'USD', 1, 'F', 8);

INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (28, 4, 'Доставка по городу',  'RUB', 2, 'F', 220);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (29, 4, 'Доставка по городу',  'UAH', 2, 'F', 35);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (30, 4, 'Доставка по городу',  'USD', 2, 'F', 7);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (31, 4, 'Доставка по городу',  'USD', 1, 'F', 10);





