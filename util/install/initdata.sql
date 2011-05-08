-- Igor Azarny iazarny@yahoo.com.
-- Initial data.
--

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



INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1000,  'SYSTEM_DEFAULT_SHOP',  1,  NULL,  'System. Default shop',
  'This value will be used for redirections when shop can not be resolved by http request', 1002,  1000);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1001,  'BRAND_IMAGE',  1,  NULL,  'Brand image',  null,  1003, 1005);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1002,  'CATEGORY_ITEMS_PER_PAGE',  0,  NULL,  'Category item per page settings',
   'Category item per page settings with failover',  1004, 1002);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1003,  'MATERIAL',  0,  NULL,  'Material',  'Material',   1000, 1003);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1004,  'BATTERY_TYPE',  0,  NULL,  'Battery',   'Battery type',   1000, 1003);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1005,  'COLOR',  0,  NULL,  'Color',  'Color',  1000, 1003);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1006,  'CURRENCY',  0,  NULL,  'Currensies',  'Supported currensies by shop. First one is default',  1004, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1040,  'SHOP_B2B',  1,  NULL,  'Is B2B profile for this shop',  'Is B2B profile for this shop',  1000, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1007,  'SYSTEM_IMAGE_VAULT',  1,  NULL,  'Brand, product and skus image repository',
  'Brand, product and skus image repository', 1000,  1000);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1008,  'IMAGE0',  1,  NULL,  'Product default image',  'Product default image',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1009,  'IMAGE1',  0,  NULL,  'First product alternative image',  'First product alternative image',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1010,  'IMAGE2',  0,  NULL,  'Second product alternative image',  'Second product alternative image',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1011,  'IMAGE3',  0,  NULL,  'Third product alternative image',  'Third product alternative image',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1012,  'IMAGE4',  0,  NULL,  'Fourth product alternative image',  'Fourth product alternative image',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1013,  'IMAGE5',  0,  NULL,  'Fifth product alternative image',  'Fifth product alternative image',  1003, 1003);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1014,  'SKUIMAGE0',  0,  NULL,  'Product sku default image',  'Product sku default image',  1003, 1004);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1015,  'SKUIMAGE1',  0,  NULL,  'First sku alternative image',  'First sku alternative image',  1003, 1004);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1016,  'SKUIMAGE2',  0,  NULL,  'Second sku alternative image',  'Second sku alternative image',  1003, 1004);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1017,  'SKUIMAGE3',  0,  NULL,  'Third sku alternative image',  'Third sku alternative image',  1003, 1004);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1018,  'SKUIMAGE4',  0,  NULL,  'Fourth sku alternative image',  'Fourth sku alternative image',  1003, 1004);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1019,  'SKUIMAGE5',  0,  NULL,  'Fifth sku alternative image',  'Fifth sku alternative image',  1003, 1004);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1020,  'WEIGHT',  0,  NULL,  'Weight',  'Weight',  1005, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1021,  'LENGTH',  0,  NULL,  'Weight',  'Weight',  1005, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1022,  'WIDTH',  0,  NULL,  'Width',  'Width',  1005, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1023,  'HEIGHT',  0,  NULL,  'Height',  'Height',  1005, 1003);



INSERT INTO TNPASYSTEM (NPASYSTEM_ID, CODE, NAME, DESCRIPTION)
  VALUES (100,'NPASYSTEM','Npa ecommerce system', 'System table');

INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)
  VALUES (1000,'http://www.gadget.npa.com:8080/webshopwicket','SYSTEM_DEFAULT_SHOP',100);

INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)
  VALUES (1001,'/npa/default','SYSTEM_DEFAULT_FSPOINTER',100);

INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)
  VALUES (1002,'10,20,40','SEARCH_ITEMS_PER_PAGE',100);

INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, NPASYSTEM_ID)
  VALUES (1003,'/npa/imagevault','SYSTEM_IMAGE_VAULT',100);



INSERT INTO TSHOP (SHOP_ID, NAME, DESCRIPTION, FSPOINTER, CODE)
  VALUES (10, 'Gadget universe', 'Gadget universe shop', '/npa/gadget', 'SHOP1');

INSERT INTO TSHOP (SHOP_ID, NAME, DESCRIPTION, FSPOINTER, CODE)
  VALUES (20, 'Mobile universe', 'Mobile universe shop', '/npa/mobile', 'SHOP2');

INSERT INTO TSHOP (SHOP_ID, NAME, DESCRIPTION, FSPOINTER, CODE)
  VALUES (30, 'Game universe', 'Game universe shop', '/npa/game', 'SHOP3');

INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID)
   VALUES (10, 'EUR,UAH', 'CURRENCY', 10);

INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID, VAL, SHOP_ID, CODE)
   VALUES (39, 'true', 40, 'SHOP_B2B');



INSERT INTO TSTOREEXCHANGERATE (SHOPEXCHANGERATE_ID, FROMCURRENCY, TOCURRENCY, SHOP_ID, RATE) 
   VALUES(1,'EUR','UAH',10, 11.38);


INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL )  VALUES (10, 10, 'gadget.npa.com');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL )  VALUES (11, 10, 'www.gadget.npa.com');

INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL )  VALUES (20, 20, 'mobile.npa.com');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL )  VALUES (21, 20, 'www.mobile.npa.com');

INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL )  VALUES (30, 30, 'game.npa.com');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL )  VALUES (31, 30, 'www.game.npa.com');


-- product types
INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)
  VALUES (1,'Unknown','Unknown','default', 0,0,1);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)
  VALUES (2,'Robot','Robot','default', 0,0,1);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)
  VALUES (3,'Cable','Cable','default', 0,0,1);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)
  VALUES (4,'Battery','Battery','default', 0,0,1);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPABLE)
  VALUES (5,'Nuts and bolts','Nuts and bolts','default', 0,0,1);


INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)
  VALUES (30, 'MATERIAL', 2, 15 , 1, 1, 1, 'S' , '');

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)
  VALUES (31, 'BATTERY_TYPE', 2, 10 , 1, 1, 1, 'S' , '');

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMULARITY, NAV, NAV_TYPE, RANGE_NAV)
  VALUES (32, 'COLOR', 2, 5 , 1, 1, 1, 'S' , '');


-- attribute view group --
INSERT INTO TATTRVIEWGROUP (ATTRVIEWGROUP_ID, NAME, DESCRIPTION) VALUES (1,'Power supply','Power');
INSERT INTO TATTRVIEWGROUP (ATTRVIEWGROUP_ID, NAME, DESCRIPTION) VALUES (2,'Material and color','Material and color');
INSERT INTO TATTRVIEWGROUP (ATTRVIEWGROUP_ID, NAME, DESCRIPTION) VALUES (3,'Sizes','Sizes');


INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)
    VALUES (1, 2, 1, 'BATTERY_TYPE', 3);

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)
    VALUES (2, 2, 2, 'MATERIAL,COLOR', 1);

INSERT INTO TPRODTYPEATTRVIEWGROUP(PRODTYPEATTRIBUTEGROUP_ID, PRODUCTTYPE_ID, ATTRVIEWGROUP_ID, ATTRCODELIST, RANK)
    VALUES (3, 2, 3, 'WEIGHT,LENGTH,WIDTH,HEIGHT', 2);





-- gadget.npa.com;www.gadget.npa.com;mobile.npa.com;www.mobile.npa.com;game.npa.com;www.game.npa.com

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE) VALUES (100, 100, 0, 'root', 'The root category','default');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID) VALUES (20,'CATEGORY_ITEMS_PER_PAGE','6,12,24',100);



	
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, NAV_BY_BRAND, UITEMPLATE) VALUES (101,100,10,'Big Boys Gadgets','Big Boys Gadgets',1, 'boys');	
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (102,101,10,'Flying Machines','Flying Machines');	
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (103,101,20,'Paintballing','Paintballing');

INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 9000, 'robotics for big boys','Robotics for big boys','Robotics, Mechanical toy, Buy, Kiev, Gift','The best purchase for big boys, that like robots');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, NAV_BY_ATTR, NAV_BY_BRAND, NAV_BY_PRICE, PRODUCTTYPE_ID, NAV_BY_PRICE_TIERS, SEO_ID )
VALUES (104,101,5,'Robotics','Robotics', 1, 1, 1, 2,
'<pricetree><priceMap><entry><string>EUR</string><list><pricenode><priceRange><first class="big-decimal">0</first><second class="big-decimal">100</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">100</first><second class="big-decimal">300</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">300</first><second class="big-decimal">500</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">500</first><second class="big-decimal">1000</second></priceRange></pricenode><pricenode><priceRange><first class="big-decimal">1000</first><second class="big-decimal">200000</second></priceRange></pricenode></list></entry></priceMap></pricetree>',
9000);

INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 9001, 'Супер гаджеты','Супер Роботы','Роботы, механические игрушки, подарок','The best purchase for big boys, that like robots');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, SEO_ID) VALUES (105,101,30,'Ultimate Gadgets','Ultimate Gadgets', 9001);
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID) VALUES (21,'CATEGORY_ITEMS_PER_PAGE','10,20,50',105);
	
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE) VALUES (106,100,20,'Fun Gadgets','Fun Gadgets' ,'fun' );
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (107,106,10,'Party Gadgets','Party Gadgets');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (108,106,20,'Books','Books');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (109,106,30,'Retro Gadgets','Retro Gadgets');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE) VALUES (110,106,40,'Office Fun','Office Fun' ,'office');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (111,106,50,'TV and Film','TV and Film');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (112,106,60,'KnickKnacks','KnickKnacks');
	
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (113,100,30,'Lifestyle Gadgets','Lifestyle Gadgets');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (114,113,10,'Kitch and Home','Kitch and Home');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (115,113,20,'Urban Essentials','Urban Essentials');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (116,113,30,'On The Go','On The Go');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (117,113,40,'Eco Friendly','Eco Friendly');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (118,113,50,'Clocks and Radios','Clocks and Radios');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (119,113,60,'Lighting','Lighting');
	
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (120,100,40,'Techno Gadgets','Techno Gadgets');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (121,120,10,'USB Gadgets','USB Gadgets');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (122,120,20,'Robotics','Robotics');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (123,120,30,'Scientific Gadgets','Scientific Gadgets');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (124,120,40,'Remote Control Gadgets','Remote Control Gadgets');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (125,120,50,'USB Gadgets','USB Gadgets');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (126,120,60,'Mobile Accessories','Mobile Accessories');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (127,120,70,'Batteries','Batteries');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (128,120,80,'Accessories for iPods','Accessories for iPods');
	
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (129,100,50,'Outdoor Gadgets','Outdoor Gadgets');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (130,129,10,'Summer Time','Summer Time');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (131,129,20,'Outdoor Fun','Outdoor Fun');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (132,129,30,'Outdoor Survival','Outdoor Survival');
	
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE) VALUES (133,100,60,'Gadget Girl','Gadget Girl' ,'girls');
	
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (134,100,70,'Games and Gifts','Games and Gifts');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (135,134,10,'Games','Games');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (136,134,20,'Puzzlers','Puzzlers');	
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (137,134,30,'Electric Shocking Games','Electric Shocking Games');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (138,134,40,'Gift Boxes','Gift Boxes');	
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (139,134,50,'Games and Gifts','Games and Gifts');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (143,102,10,'Helicopters','Helicopters');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (144,102,20,'Aircraft','Aircraft');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE) VALUES (140,100,80,'Christmas Catalogue 2009','Christmas Catalogue 2009' ,'christmas');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION) VALUES (150,100,90,'Robo accesories','Robo accesories');


INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES ( 100,10,10,101);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES ( 101,20,10,106);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES ( 102,30,10,113);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES ( 103,40,10,120);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES ( 104,50,10,129);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES ( 105,60,10,133);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES ( 106,70,10,134);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES ( 107, 5,10,140);
INSERT INTO TSHOPCATEGORY (SHOPCATEGORY_ID, RANK, SHOP_ID, CATEGORY_ID) VALUES ( 108, 90,10,150);






INSERT INTO TAVAILABILITY ( AVAILABILITY_ID,NAME,DESCRIPTION) VALUES (1,'In stock','Show id available in stock');
INSERT INTO TAVAILABILITY ( AVAILABILITY_ID,NAME,DESCRIPTION) VALUES (2,'Preorder','Pre order' );
INSERT INTO TAVAILABILITY ( AVAILABILITY_ID,NAME,DESCRIPTION) VALUES (4,'Back order','Back order' );
INSERT INTO TAVAILABILITY ( AVAILABILITY_ID,NAME,DESCRIPTION) VALUES (8,'Always','Available at any time' );





-- brands --
INSERT INTO TBRAND(BRAND_ID, NAME, DESCRIPTION) VALUES(10,'Unknown','Unknown');
INSERT INTO TBRAND(BRAND_ID, NAME, DESCRIPTION) VALUES(1,'Retro Thing','Retro Thing manafacturer');
INSERT INTO TBRAND(BRAND_ID, NAME, DESCRIPTION) VALUES(2,'Sony','Sony');
INSERT INTO TBRAND(BRAND_ID, NAME, DESCRIPTION) VALUES(3,'Samsung','Samsung');
INSERT INTO TBRAND(BRAND_ID, NAME, DESCRIPTION) VALUES(4,'Vesna','Old soviet union brand.');
INSERT INTO TBRAND(BRAND_ID, NAME, DESCRIPTION) VALUES(5,'KZNB','Kiev plan of nuts abd bolts');
INSERT INTO TBRAND(BRAND_ID, NAME, DESCRIPTION) VALUES(6,'Honda','Honda');


-- products --
INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 10000, 'i-sobot','I-Sobot robot','Sobot, Robotics, Mechanical toy, Beer','Buy i-Sobot - the worlds smallest humanoid robot, 200 phrases, voice recognition and easy to use remote control; available at gadget.npa.com');

INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 10001, 'i-sobot-pink','I-Sobot pink robot','Sobot, Robotics, Mechanical toy, Vibrator','Buy i-Sobot - the worlds smallest humanoid robot, 200 phrases, built-in vibrator with voice recognition and easy to use remote control; available at gadget.npa.com');

INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 10002, 'star-trek-tricoder','Star trec tricoder','Retro,Star,Trek,Tricorder','Based on the classic 23rd century design, this 49.99 reproduction Star Trek Tricorder measures a compact 8 x 4 x 2. It requires four AA batteries (Dilithium cells are best, but no-name Alkaline batteries will work in a pinch). The...');

INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 10003, 'beer-bot','Beer bot','Beer, robot, open','The beer bot');

INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION)
  VALUES ( 10004, 'HALL 9000','HALL 9000','hall, mars','hall, mars');

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,   
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10000,
          'SOBOT',
          'I-Sobot',
          'Think small. BigSmall. Small is beautiful. Good things come in small packages. Small ones are more juicy. However you like to put it, sometimes theres a beauty and economy in reducing the scale of an object and cramming it full of cleverness. Now, the ever-so-clever chaps at Tomy have done just that with the creation of the I-Sobot. The what bot we hear you ask? The I-Sobot is the world smallest mass produced bi-pedal humanoid robot. But dont be fooled by his size - he has a big personality all of his own. Measuring only 16cm and weighing an amazing 300 grams, he is ready to play straight from the box. Choose from 4 modes where I-Sobot can perform fun and quirky scenes from Western battles to voice command mode that includes 10 words of phrases to say to I-Sobot where he will respond. With 17 Servo motors allowing human like articulation and 200 spoken phrases, you will never be lonesome again. I-Sobots distinctive design and ground-breaking functionality has us rather drooling at the mouth, in an ever-so manly way. Splendid.',
	   10,2,1,10000 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5000, 'BATTERY_TYPE' , 'Plutonium', 10000 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5001, 'MATERIAL' , 'Metal', 10000 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4001, 'COLOR' , 'Red', 10000 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4002, 'IMAGE0' , 'Sobot_SOBOT_a.jpg', 10000 );


INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,   
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10001,
          'SOBOT1', 'I-Sobot1',
          'Think small. BigSmall. Small is beautiful. Good things come in small packages. Small ones are more juicy. However you like to put it, sometimes theres a beauty and economy in reducing the scale of an object and cramming it full of cleverness. Now, the ever-so-clever chaps at Tomy have done just that with the creation of the I-Sobot. The what bot we hear you ask? The I-Sobot is the world smallest mass produced bi-pedal humanoid robot. But dont be fooled by his size - he has a big personality all of his own. Measuring only 16cm and weighing an amazing 300 grams, he is ready to play straight from the box. Choose from 4 modes where I-Sobot can perform fun and quirky scenes from Western battles to voice command mode that includes 10 words of phrases to say to I-Sobot where he will respond. With 17 Servo motors allowing human like articulation and 200 spoken phrases, you will never be lonesome again. I-Sobots distinctive design and ground-breaking functionality has us rather drooling at the mouth, in an ever-so manly way. Splendid.',
          10,2,1,10000 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5002, 'BATTERY_TYPE' , 'Plutonium', 10001 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5003, 'MATERIAL' , 'Plastic', 10001 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4011, 'COLOR' , 'White', 10001 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4012, 'IMAGE0' , 'Sobot_SOBOT1_a.jpg', 10001 );


INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,   
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10002,
          'SOBOT2', 'I-Sobot2',
          'Think small. BigSmall. Small is beautiful. Good things come in small packages. Small ones are more juicy. However you like to put it, sometimes theres a beauty and economy in reducing the scale of an object and cramming it full of cleverness. Now, the ever-so-clever chaps at Tomy have done just that with the creation of the I-Sobot. The what bot we hear you ask? The I-Sobot is the world smallest mass produced bi-pedal humanoid robot. But dont be fooled by his size - he has a big personality all of his own. Measuring only 16cm and weighing an amazing 300 grams, he is ready to play straight from the box. Choose from 4 modes where I-Sobot can perform fun and quirky scenes from Western battles to voice command mode that includes 10 words of phrases to say to I-Sobot where he will respond. With 17 Servo motors allowing human like articulation and 200 spoken phrases, you will never be lonesome again. I-Sobots distinctive design and ground-breaking functionality has us rather drooling at the mouth, in an ever-so manly way. Splendid.',
          10,2,1,10000 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5004, 'BATTERY_TYPE' , 'A3', 10002 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5005, 'MATERIAL' , 'Plastic', 10002 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4021, 'COLOR' , 'White', 10002 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4022, 'IMAGE0' , 'Sobot_SOBOT2_a.jpg', 10002 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4023, 'IMAGE1' , 'Sobot_SOBOT2_b.jpg', 10002 );

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,   
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10003,
          'SOBOT3', 'I-Sobot3',
          'Think small. BigSmall. Small is beautiful. Good things come in small packages. Small ones are more juicy. However you like to put it, sometimes theres a beauty and economy in reducing the scale of an object and cramming it full of cleverness. Now, the ever-so-clever chaps at Tomy have done just that with the creation of the I-Sobot. The what bot we hear you ask? The I-Sobot is the world smallest mass produced bi-pedal humanoid robot. But dont be fooled by his size - he has a big personality all of his own. Measuring only 16cm and weighing an amazing 300 grams, he is ready to play straight from the box. Choose from 4 modes where I-Sobot can perform fun and quirky scenes from Western battles to voice command mode that includes 10 words of phrases to say to I-Sobot where he will respond. With 17 Servo motors allowing human like articulation and 200 spoken phrases, you will never be lonesome again. I-Sobots distinctive design and ground-breaking functionality has us rather drooling at the mouth, in an ever-so manly way. Splendid.',
         10,2,1,10000 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5006, 'BATTERY_TYPE' , 'AAA', 10003 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5007, 'MATERIAL' , 'Wood', 10003 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4031, 'COLOR' , 'Green', 10003 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4032, 'IMAGE0' , 'Sobot_SOBOT3_a.jpg', 10003 );

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION, 
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10004,
          'SOBOT4', 'I-Sobot4',
          'Think small. BigSmall. Small is beautiful. Good things come in small packages. Small ones are more juicy. However you like to put it, sometimes theres a beauty and economy in reducing the scale of an object and cramming it full of cleverness. Now, the ever-so-clever chaps at Tomy have done just that with the creation of the I-Sobot. The what bot we hear you ask? The I-Sobot is the world smallest mass produced bi-pedal humanoid robot. But dont be fooled by his size - he has a big personality all of his own. Measuring only 16cm and weighing an amazing 300 grams, he is ready to play straight from the box. Choose from 4 modes where I-Sobot can perform fun and quirky scenes from Western battles to voice command mode that includes 10 words of phrases to say to I-Sobot where he will respond. With 17 Servo motors allowing human like articulation and 200 spoken phrases, you will never be lonesome again. I-Sobots distinctive design and ground-breaking functionality has us rather drooling at the mouth, in an ever-so manly way. Splendid.',
          10,2,1,10000 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5008, 'BATTERY_TYPE' , 'AAA', 10004 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5009, 'MATERIAL' , 'Wood', 10004 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4041, 'COLOR' , 'Red', 10004 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4042, 'IMAGE0' , 'Sobot_SOBOT4_a.jpg', 10004 );

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,   
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10005,
          'SOBOT5', 'I-Sobot5',
          'Think small. BigSmall. Small is beautiful. Good things come in small packages. Small ones are more juicy. However you like to put it, sometimes theres a beauty and economy in reducing the scale of an object and cramming it full of cleverness. Now, the ever-so-clever chaps at Tomy have done just that with the creation of the I-Sobot. The what bot we hear you ask? The I-Sobot is the world smallest mass produced bi-pedal humanoid robot. But dont be fooled by his size - he has a big personality all of his own. Measuring only 16cm and weighing an amazing 300 grams, he is ready to play straight from the box. Choose from 4 modes where I-Sobot can perform fun and quirky scenes from Western battles to voice command mode that includes 10 words of phrases to say to I-Sobot where he will respond. With 17 Servo motors allowing human like articulation and 200 spoken phrases, you will never be lonesome again. I-Sobots distinctive design and ground-breaking functionality has us rather drooling at the mouth, in an ever-so manly way. Splendid.',
          10,2,1,10000 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5010, 'BATTERY_TYPE' , 'AAA', 10005 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5011, 'MATERIAL' , 'Wood', 10005 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4051, 'COLOR' , 'Pink', 10005 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4052, 'IMAGE0' , 'Sobot_SOBOT5_a.jpg', 10005 );

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10006,
          'SOBOT6', 'I-Sobot6',
          'Think small. BigSmall. Small is beautiful. Good things come in small packages. Small ones are more juicy. However you like to put it, sometimes theres a beauty and economy in reducing the scale of an object and cramming it full of cleverness. Now, the ever-so-clever chaps at Tomy have done just that with the creation of the I-Sobot. The what bot we hear you ask? The I-Sobot is the world smallest mass produced bi-pedal humanoid robot. But dont be fooled by his size - he has a big personality all of his own. Measuring only 16cm and weighing an amazing 300 grams, he is ready to play straight from the box. Choose from 4 modes where I-Sobot can perform fun and quirky scenes from Western battles to voice command mode that includes 10 words of phrases to say to I-Sobot where he will respond. With 17 Servo motors allowing human like articulation and 200 spoken phrases, you will never be lonesome again. I-Sobots distinctive design and ground-breaking functionality has us rather drooling at the mouth, in an ever-so manly way. Splendid.',
          10,2,1,10000 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5012, 'BATTERY_TYPE' , 'AAA', 10006 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5013, 'MATERIAL' , 'Wood', 10006 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4061, 'COLOR' , 'White', 10006 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4062, 'IMAGE0' , 'Sobot_SOBOT6_a.jpg', 10006 );

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10007,
          'SOBOT7', 'I-Sobot7',
          'Think small. BigSmall. Small is beautiful. Good things come in small packages. Small ones are more juicy. However you like to put it, sometimes theres a beauty and economy in reducing the scale of an object and cramming it full of cleverness. Now, the ever-so-clever chaps at Tomy have done just that with the creation of the I-Sobot. The what bot we hear you ask? The I-Sobot is the world smallest mass produced bi-pedal humanoid robot. But dont be fooled by his size - he has a big personality all of his own. Measuring only 16cm and weighing an amazing 300 grams, he is ready to play straight from the box. Choose from 4 modes where I-Sobot can perform fun and quirky scenes from Western battles to voice command mode that includes 10 words of phrases to say to I-Sobot where he will respond. With 17 Servo motors allowing human like articulation and 200 spoken phrases, you will never be lonesome again. I-Sobots distinctive design and ground-breaking functionality has us rather drooling at the mouth, in an ever-so manly way. Splendid.',
          10,2,1,10000 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5014, 'BATTERY_TYPE' , 'Uranium', 10007 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5015, 'MATERIAL' , 'Brick', 10007 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4071, 'COLOR' , 'Red', 10007 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4072, 'IMAGE0' , 'Sobot_SOBOT7_a.jpg', 10007 );

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10008,
          'STT-1', 'Your Very Own Star Trek Tricorder',
          'Based on the classic 23rd century design, this 49.99 reproduction Star Trek Tricorder measures a compact 8 x 4 x 2. It requires four AA batteries (Dilithium cells are best, but no-name Alkaline batteries will work in a pinch). The manufacturer cleverly markets the device as a role-play replica, which is a polite way of saying, toy for grownups who like to run around in outfits that cause four-year-olds to mistake them for The Wiggles. Kids will be equally confused by the array of sound effects taken from the original series along with some cheesy blinky lights and a removable scanner accessory. And am I the only one who wishes that real geek gear looked a bit more like this? Available June 4, 2009 at a Starfleet supply depot near you. Oh, a matching phaser and communicator set is also available, should you lean that way.',
          1,1,1,10002 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5016, 'BATTERY_TYPE' , 'Uranium', 10008 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5017, 'MATERIAL' , 'Stone', 10008 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4081, 'COLOR' , 'White', 10008 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4082, 'IMAGE0' , 'Star-Trek-Tricorder_STT-1_a.jpg', 10008 );


INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10009,
          'BB-1', 'Beer bot',
          'To win one of the beer-bots, in a promotion for the companys new low malt beer, contestants must collect 36 tokens found on the specially marked beers. But the competition, starting in February, is only open to those in Japan.',
          4,2,1,10003 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5018, 'BATTERY_TYPE' , 'Litium', 10009 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5019, 'MATERIAL' , 'Stone', 10009 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4091, 'COLOR' , 'White', 10009 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4092, 'IMAGE0' , 'Beer-bot_BB-1_a.jpg', 10009 );

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10010,
          'BB-2', 'Beer bot, 3 language edition',
          'Some robotics experts see the promotion as a fun way to promote a wider interest in robotics. Others, however, say it is a gimmick that distracts from genuine robot research.',
          4,2,1,null );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5020, 'BATTERY_TYPE' , 'Litium', 10010 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5021, 'MATERIAL' , 'Stone', 10010 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4101, 'COLOR' , 'Red', 10010 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4102, 'IMAGE0' , 'Beer-bot_BB-2_a.jpg', 10010 );

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10011,
          'BB-3', 'Beer bot nahui edition',
          'Since putting up our original video of the Asahi Beerbot, weve had tons of requests to make it available. Well, now it is! You can now getByKey the original beer-pouring robot for yourself, and most likely become the only person on your continent who has one.',
          4,2,1,null );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5022, 'BATTERY_TYPE' , 'Litium', 10011 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5023, 'MATERIAL' , 'Stone', 10011 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4111, 'COLOR' , 'Green', 10011 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4112, 'IMAGE0' , 'Beer-bot_BB-3_a.jpg', 10011 );

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,  
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10012,
          'SBB-0101010101', 'Sony Beer bot',
          'Sony invents robot that can steal your beer.',
          2,1,1,null );

INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5024, 'BATTERY_TYPE' , 'Litium', 10012 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5025, 'MATERIAL' , 'Stone', 10012 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4121, 'COLOR' , 'Black', 10012 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4132, 'IMAGE0' , 'Beer-bot_SBB-0101010101_a.jpg', 10012 );

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10013,
          'HALL9000', 'Hall 9000',
          'HAL (Heuristically programmed ALgorithmic Computer) is an artificial intelligence, the sentient on-board computer of the spaceship Discovery. HAL is usually represented only as his television camera "eyes" that can be seen throughout the Discovery spaceship. The voice of HAL 9000 was performed by Canadian actor Douglas Rain. In the book, HAL became operational on 12 January 1997 (1992 in the film)[1] at the HAL Plant in Urbana, Illinois. His first instructor was Dr. Chandra (Mr. Langley in the film). HAL is depicted as being capable not only of speech, speech recognition, facial recognition, and natural language processing, but also lip reading, art appreciation, interpreting emotions, expressing emotions, reasoning, and playing chess, in addition to maintaining all systems on an interplanetary voyage.',
          2,1,1,10004 );

INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5026, 'BATTERY_TYPE' , 'Uranium', 10013 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5027, 'MATERIAL' , 'Plastic', 10013 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4141, 'COLOR' , 'Black', 10013 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4142, 'IMAGE0' , 'Hall-9000_HALL9000_a.jpg', 10013 );


INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION) VALUES ( 10005, 'Asimo','Asimo','asimo, honda, robot','asimo, honda, robot');
INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,  
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10014,
          'Asimo', 'Asimo',
          'ASIMO is a humanoid robot created by Honda. Standing at 130 centimeters (4 feet 3 inches) and weighing 54 kilograms (114 pounds), the robot resembles a small astronaut wearing a backpack and can walk or run on two feet at speeds up to 6 km/h (4.3 mph), matching EMIEW.[1] ASIMO was created at Hondas Research  Development Wako Fundamental Technical Research Center in Japan. It is the current model in a line of eleven that began in 1986 with E0. Officially, the name is an acronym for "Advanced Step in Innovative MObility". Hondas official statements[2] claim that the robots name is not a reference to science fiction writer and inventor of the Three Laws of Robotics, Isaac Asimov.',
          6,2,1,10005 );


INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5028, 'BATTERY_TYPE' , 'Uranium', 10014 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (5029, 'MATERIAL' , 'Plastic', 10014 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4151, 'COLOR' , 'White', 10014 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4152, 'IMAGE0' , 'Asimoseo_honda_Asimo_a.jpg', 10014 );



INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION) VALUES ( 10006, 'Beer-supply-cable','Beer supply cable','beer,supply,cable','beer,supply,cable');
INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION, 
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10015,
          'Cable1', 'Beer supply cable',
          'Beer supply cable for sobot series',
          0,  3   ,1,10006 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4200, 'IMAGE0' , 'Beer_supply_Cable1_a.jpg', 10015 );

INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION) VALUES ( 10007, 'Bolt','Bolt','bolt','bolt');
INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,   
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10016,
          'BOLT1', 'Bolt',
          'Just old bolt',
          0,  5   ,1,10007 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4201, 'IMAGE0' , 'rusted_nut_BOLT1_a.jpg', 10016 );

INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,  
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10017,
          'BOLT2', 'Bolt 2',
          'Set of new bolts',
          0,  5   ,1,10007 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4202, 'IMAGE0' , 'rusted_nut_bolts_BOLT2_a.jpg', 10017 );

INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION) VALUES ( 10008, 'Lemon-battery','Lemon-battery','Lemon,battery','Lemon,battery');
INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,  
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10018,
          'LEMONBAT', 'Lemon battery',
          'Lemon battery for sobot series',
          0,  4   ,1,10008 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4203, 'IMAGE0' , 'lemon_battery_LEMONBAT_a.jpg', 10018 );

INSERT INTO TSEO (SEO_ID, URI, TITLE, METAKEYWORDS, METADESCRIPTION) VALUES ( 10009, 'battery','battery','battery','battery');
INSERT INTO TPRODUCT (PRODUCT_ID,
          CODE,   NAME,   DESCRIPTION,  
          BRAND_ID,PRODUCTTYPE_ID,AVAILABILITY_ID,SEO_ID) VALUES (10019,
          'BAT1', 'Battery',
          'Old style battery for sobot series',
          0,  4   ,1,10009 );
INSERT INTO TPRODUCTATTRVALUE (ATTRVALUE_ID, CODE, VAL, PRODUCT_ID) VALUES (4204, 'IMAGE0' , 'BAT1_a.jpg', 10019 );





-- skus -------------------------------------------------------


INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,DESCRIPTION,RANK) VALUES (10000,  10000,  'SOBOT-BEER', 'I-Sobot Beer Edition',  'Special I-Sobot edition, that can play poker, open beer and smoke',  1 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,DESCRIPTION,RANK,SEO_ID) VALUES (10001,  10000, 'SOBOT-PINK', 'I-Sobot Girls Edition', 'Special I-Sobot edition, that can play poker, open beer and smoke',  2, 10001 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,DESCRIPTION,RANK) VALUES (10003, 10000,   'SOBOT-LIGHT','I-Sobot Light Edition',  'Special I-Sobot light edition, with build in lighter',  3 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10004, 10000, 'SOBOT-ORIG', 'I-Sobot original',  4 );

INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10005,  10001, 'SOBOT1', 'I-Sobot original 1', 1 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10006,  10002, 'SOBOT2', 'I-Sobot original 2', 1 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10007,  10003, 'SOBOT3', 'I-Sobot original 3', 1 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10008,  10004, 'SOBOT4', 'I-Sobot original 4', 1 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10009,  10005, 'SOBOT5', 'I-Sobot original 5', 1 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10010,  10006, 'SOBOT6', 'I-Sobot original 6', 1 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10011,  10007, 'SOBOT7', 'I-Sobot original 7', 1 );

INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10012, 10008, 'STT-1', 'Star trec tricoder', 1 );

INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10013, 10009, 'BB-1', 'Beer bot 1',  4 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10014, 10010, 'BB-2', 'Beer bot 2',  4 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10015, 10011, 'BB-3', 'Beer bot 3',  4 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10016, 10012, 'SBB-0101010101', 'Beer bot 4',  4 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10017, 10013, 'HALL9000', 'HALL9000',  400 );
INSERT INTO TSKU(SKU_ID,PRODUCT_ID,CODE,NAME,RANK) VALUES (10018, 10014, 'Asimo', 'Asimo',  1 );

-- product to category assign

INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10001,10000,104,999);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10002,10001,104,1);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10003,10002,104,2);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10004,10003,104,3);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10005,10004,104,4);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10006,10005,104,100);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10007,10006,104,100);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10008,10007,104,100);

INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10009,10008,109,100);

INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10010,10009,104,100);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10011,10010,104,100);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10012,10011,104,100);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10013,10012,104,100);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10014,10013,104,99);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10015,10014,104,1);

INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10016,10015,150,1);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10017,10016,150,2);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10018,10017,150,3);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10019,10018,150,4);
INSERT INTO TPRODUCTCATEGORY (PRODUCTCATEGORY_ID, PRODUCT_ID, CATEGORY_ID,RANK) VALUES (10020,10019,150,5);





INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE, SALE_PRICE)  VALUES (1, 10000, 10, 'EUR', 1, 85.58, 80.58);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (2, 10001, 10, 'EUR', 1, 89.58);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (3, 10003, 10, 'EUR', 2, 81.58);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (4, 10003, 10, 'EUR', 1, 101.58);

INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (5, 10005, 10, 'EUR', 1, 102);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (6, 10006, 10, 'EUR', 1, 102);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (7, 10007, 10, 'EUR', 1, 102);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (8, 10008, 10, 'EUR', 1, 102);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (9, 10009, 10, 'EUR', 1, 102);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (10, 10010, 10, 'EUR', 1, 102);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (11, 10011, 10, 'EUR', 1, 102);

INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (12, 10012, 10, 'EUR', 1, 500);

INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (13, 10013, 10, 'EUR', 1, 410);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (14, 10014, 10, 'EUR', 1, 415.58);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (15, 10015, 10, 'EUR', 1, 420);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (16, 10016, 10, 'EUR', 1, 417.17);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (17, 10017, 10, 'EUR', 1, 450);
INSERT INTO TSKUPRICE (SKUPRICE_ID, SKU_ID, SHOP_ID, CURRENCY, QTY, REGULAR_PRICE)  VALUES (18, 10018, 10, 'EUR', 1, 160000);


-- seo images -----------------------
INSERT INTO TSEOIMAGE(SEOIMAGE_ID, IMAGE_NAME, ALT, TITLE)
   VALUES(1, 'Sobot_SOBOT2_a.jpg', 'Sobot robot', 'Sobot robot. A lot of fun');

INSERT INTO TSEOIMAGE(SEOIMAGE_ID, IMAGE_NAME, ALT, TITLE)
   VALUES(2, 'Asimoseo_honda_Asimo_a.jpg', 'Asimo robot', 'Asimo robot from honda');

INSERT INTO TSEOIMAGE(SEOIMAGE_ID, IMAGE_NAME, ALT, TITLE)
   VALUES(4, 'lemon_battery_LEMONBAT_a.jpg', 'Lemon battery', 'Lemon battery');

INSERT INTO TSEOIMAGE(SEOIMAGE_ID, IMAGE_NAME, ALT, TITLE)
   VALUES(5, 'Sobot_SOBOT2_b.jpg', 'Sobot with bow and arrow', 'Sobot with bow and arrow');




-- product accosiations
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)
  VALUES (1, 'accessories' , 'Accessories' , 'Product accessories');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)
  VALUES (2, 'up' , 'Up sell' , 'Up sell');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)
  VALUES (3, 'cross' , 'Cross sell' , 'Cross sell');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION)
  VALUES (4, 'buywiththis' , 'Buy with this products' , 'Shoppers also buy with this product');


INSERT INTO TPRODUCTASSOCIATION(PRODUCTASSOCIATION_ID,RANK,ASSOCIATION_ID,PRODUCT_ID, ASSOCIATEDPRODUCT_ID) VALUES (1, 1, 2, 10002, 10014);

INSERT INTO TPRODUCTASSOCIATION(PRODUCTASSOCIATION_ID,RANK,ASSOCIATION_ID,PRODUCT_ID, ASSOCIATEDPRODUCT_ID) VALUES (2, 1, 1, 10002, 10015);
INSERT INTO TPRODUCTASSOCIATION(PRODUCTASSOCIATION_ID,RANK,ASSOCIATION_ID,PRODUCT_ID, ASSOCIATEDPRODUCT_ID) VALUES (3, 1, 1, 10002, 10016);
INSERT INTO TPRODUCTASSOCIATION(PRODUCTASSOCIATION_ID,RANK,ASSOCIATION_ID,PRODUCT_ID, ASSOCIATEDPRODUCT_ID) VALUES (4, 1, 1, 10002, 10017);
INSERT INTO TPRODUCTASSOCIATION(PRODUCTASSOCIATION_ID,RANK,ASSOCIATION_ID,PRODUCT_ID, ASSOCIATEDPRODUCT_ID) VALUES (5, 1, 1, 10002, 10018);
INSERT INTO TPRODUCTASSOCIATION(PRODUCTASSOCIATION_ID,RANK,ASSOCIATION_ID,PRODUCT_ID, ASSOCIATEDPRODUCT_ID) VALUES (6, 1, 1, 10002, 10019);



UPDATE HIBERNATE_UNIQUE_KEYS SET VALUE = 100000;

COMMIT;