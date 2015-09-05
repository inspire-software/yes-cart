--
--  Copyright 2009 Denys Pavlov, Igor Azarnyi
--
--     Licensed under the Apache License, Version 2.0 (the "License");
--     you may not use this file except in compliance with the License.
--     You may obtain a copy of the License at
--
--         http://www.apache.org/licenses/LICENSE-2.0
--
--     Unless required by applicable law or agreed to in writing, software
--     distributed under the License is distributed on an "AS IS" BASIS,
--     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--     See the License for the specific language governing permissions and
--     limitations under the License.
--

--
-- This script is for MySQL only with some Derby hints inline with comments
-- We highly recommend you seek YC's support help when upgrading your system
-- for detailed analysis of your code.
--
-- Upgrades organised in blocks representing JIRA tasks for which they are
-- necessary - potentially you may hand pick the upgrades you required but
-- to keep upgrade process as easy as possible for future we recommend full
-- upgrades
--

--
-- YC-542 Improve customer registration/account details field configuration.
--


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10983,  'SHOP_CUSTOMER_REG_ATTRS', 'SHOP_CUSTOMER_REGISTRATION_ATTRIBUTES',  0,  NULL,  'Customer: registration form attributes (CSV)',
    'List of customer attributes separated by comma to be shown on registration form',  1004, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10982,  'SHOP_CUSTOMER_PROFILE_ATTRS_V', 'SHOP_CUSTOMER_PROFILE_ATTRIBUTES_VISIBLE',  0,  NULL,  'Customer: profile form attributes (CSV)',
    'List of customer attributes separated by comma to be shown on profile form',  1004, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10981,  'SHOP_CUSTOMER_PROFILE_ATTRS_RO', 'SHOP_CUSTOMER_PROFILE_ATTRIBUTES_READONLY',  0,  NULL,  'Customer: profile form attributes - read only (CSV)',
    'List of customer attributes separated by comma to be shown on profile form but not editable',  1004, 1001);


--
-- YC-544 Sorting options must be derived from shop configuration (same as pagination options)
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11317,  'CATEGORY_SORT_OPTIONS', 'CATEGORY_SORT_OPTIONS',  0,  NULL,  'Category: sortable fields (CSV)',
    'List of sort fields separated by comma with fail over. Default is: displayName,sku,basePrice. All supported: name,displayName,basePrice,productCode,manufacturerCode,sku,brand,availability,created',  1004, 1002);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11318,  'SHOP_CATEGORY_SORT_OPTIONS', 'SHOP_CATEGORY_SORT_OPTIONS',  0,  NULL,  'Category: sortable fields (CSV)',
    'List of sort fields separated by comma with fail over. Default is: displayName,sku,basePrice. All supported: name,displayName,basePrice,productCode,manufacturerCode,sku,brand,availability,created',  1004, 1001);

--
-- YC-502 Ensure that cached cart in resilient repository honours expiry timeout
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11103,  'CART_ABANDONED_TIMEOUT_SECONDS', 'CART_ABANDONED_TIMEOUT_SECONDS',  0,  NULL,  'Cart: abandonment in seconds',
    'Cart abandonment seconds. All abandoned carts are deleted by bulk job. Default: 2592000s (30 days)',  1006, 1000);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10980,  'CART_SESSION_EXPIRY_SECONDS', 'CART_SESSION_EXPIRY_SECONDS',  0,  NULL,  'Cart: session expiry in seconds',
    'Cart session expiry in seconds. Invalidates login when session expires. Default: 21600s (6h)',  1006, 1001);


--
-- YC-538 Add cookie policy check support (EU)
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10979,  'SHOP_COOKIE_POLICY_ENABLE', 'SHOP_COOKIE_POLICY_ENABLE',  0,  NULL,  'Shop: Cookie policy enable',
    'Enables notification for use of cookie on this site (search the Internet for ''The EU cookie law (e-Privacy Directive)'').',  1008, 1001);

INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (118, 'true','SHOP_COOKIE_POLICY_ENABLE', 10, 'SHOP_COOKIE_POLICY_ENABLE');


INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10009, 10000, 0, 'cookie_policy_include', 'Cookie policy message include for SHOP10','include', 'SHOP10_cookie_policy_include','SHOP10_cookie_policy_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12080,'CONTENT_BODY_en_1','
<small><b>Cookie Policy</b><br>
 We may store information about you using cookies (files which are sent by us to your computer or other access device) which
 we can access when you visit our site in future. We do this to enhance user experience. If you want to delete any cookies that
 are already on your computer, please refer to the instructions for your file management software to locate the file or directory
 that stores cookies. Our cookies will have the file names JSESSIONID, yc and yccookiepolicy. Information on deleting or controlling
 cookies is available at <a href="http://www.AboutCookies.org" target="_blank">www.AboutCookies.org</a>. Please note that by
 deleting our cookies or disabling future cookies you may not be able to access certain areas or features of our site.</small>
',10009,'12080_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12081,'CONTENT_BODY_ru_1','
<small><b>Cookie Policy</b><br>
 We may store information about you using cookies (files which are sent by us to your computer or other access device) which
 we can access when you visit our site in future. We do this to enhance user experience. If you want to delete any cookies that
 are already on your computer, please refer to the instructions for your file management software to locate the file or directory
 that stores cookies. Our cookies will have the file names JSESSIONID, yc and yccookiepolicy. Information on deleting or controlling
 cookies is available at <a href="http://www.AboutCookies.org" target="_blank">www.AboutCookies.org</a>. Please note that by
 deleting our cookies or disabling future cookies you may not be able to access certain areas or features of our site.</small>
',10009,'12081_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12082,'CONTENT_BODY_uk_1','
<small><b>Cookie Policy</b><br>
 We may store information about you using cookies (files which are sent by us to your computer or other access device) which
 we can access when you visit our site in future. We do this to enhance user experience. If you want to delete any cookies that
 are already on your computer, please refer to the instructions for your file management software to locate the file or directory
 that stores cookies. Our cookies will have the file names JSESSIONID, yc and yccookiepolicy. Information on deleting or controlling
 cookies is available at <a href="http://www.AboutCookies.org" target="_blank">www.AboutCookies.org</a>. Please note that by
 deleting our cookies or disabling future cookies you may not be able to access certain areas or features of our site.</small>
',10009,'12082_CAV');

--
-- YC-539 Rework 'reset password' functionality
--

alter table TCUSTOMER add column AUTHTOKEN varchar(255);
alter table TCUSTOMER add column AUTHTOKENEXPIRY datetime;
-- alter table TCUSTOMER add column AUTHTOKENEXPIRY timestamp;

alter table TMANAGER add column AUTHTOKEN varchar(255);
alter table TMANAGER add column AUTHTOKENEXPIRY datetime;
-- alter table TMANAGER add column AUTHTOKENEXPIRY timestamp;

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10978,  'SHOP_CUSTOMER_PASSWORD_RESET_CC', 'SHOP_CUSTOMER_PASSWORD_RESET_CC',  0,  NULL,  'Customer: Password reset token',
    'Authorisation token to reset password immediately by call center operative.',  1000, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10977,  'SHOP_CUSTOMER_TOKEN_EXPIRY_SECONDS', 'SHOP_CUSTOMER_TOKEN_EXPIRY_SECONDS',  0,  NULL,  'Customer: Password reset token validity (s)',
    'Authorisation token validity if reset is requested by customer in second. Default is 86400s (1 day)',  1006, 1001);


INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (119, 'r@nD()mTok3n4Pa$$Re$3+','SHOP_CUSTOMER_PASSWORD_RESET_CC', 10, 'SHOP_CUSTOMER_PASSWORD_RESET_CC');


--
-- YC-397 Display wish list items grouped by tags
--

alter table TCUSTOMER add column PUBLICKEY varchar(255);


INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10010, 10000, 0, 'profile_wishlist_owner_include', 'Profile wishlist owner include for SHOP10','include', 'SHOP10_profile_wishlist_o_include','SHOP10_profile_wishlist_owner_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12090,'CONTENT_BODY_en_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<div class="col-xs-12"><ul class="wl-tag-cloud jsWishlistTagCloud"></ul></div>
</div>
',10010,'12090_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12091,'CONTENT_BODY_ru_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<div class="col-xs-12"><ul class="wl-tag-cloud jsWishlistTagCloud"></ul></div>
</div>
',10010,'12091_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12092,'CONTENT_BODY_uk_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<div class="col-xs-12"><ul class="wl-tag-cloud jsWishlistTagCloud"></ul></div>
</div>
',10010,'12092_CAV');


INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10011, 10000, 0, 'profile_wishlist_viewer_include', 'Profile wishlist viewer include for SHOP10','include', 'SHOP10_profile_wishlist_v_include','SHOP10_profile_wishlist_viewer_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12095,'CONTENT_BODY_en_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<p>You are viewing a shared wish list</p>
</div>
',10011,'12095_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12096,'CONTENT_BODY_ru_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<p>Вы просматриваете открытый список пожеланий</p>
</div>
',10011,'12096_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12097,'CONTENT_BODY_uk_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<p>Ви переглядаєте відкритий список побажань</p>
</div>
',10011,'12097_CAV');

--
-- YC-561 Decouple SkuWarehouseEntity and ProductSkuEntity
--

alter table TSKUWAREHOUSE add column SKU_CODE varchar(255);
update TSKUWAREHOUSE w set w.SKU_CODE = (select CODE from TSKU where SKU_ID = w.SKU_ID);
-- verify using this: select w.SKU_CODE, s.CODE from TSKUWAREHOUSE w, TSKU s where w.SKU_ID = s.SKU_ID;
-- drop FK
alter table TSKUWAREHOUSE drop foreign key FKAC00F89A4EC4B749;
alter table TSKUWAREHOUSE drop constraint FKAC00F89A4EC4B749;
-- drop old unique warehouse-sku index
alter table TSKUWAREHOUSE drop constraint U_SKUINVENTORY;
-- drop column
alter table TSKUWAREHOUSE drop column SKU_ID;
-- add new unique warehouse-sku index
alter table TSKUWAREHOUSE add constraint SKUWAREHOUSE_SKU unique (WAREHOUSE_ID, SKU_CODE);
-- add index to improve selects
create index SKUWAREHOUSE_SKUCODE on TSKUWAREHOUSE (SKU_CODE);


--
-- YC-564 Decouple SkuPriceEntity and ProductSkuEntity
--

alter table TSKUPRICE add column SKU_CODE varchar(255);
update TSKUPRICE p set p.SKU_CODE = (select CODE from TSKU where SKU_ID = p.SKU_ID);
-- verify using this: select p.SKU_CODE, s.CODE from TSKUPRICE p, TSKU s where p.SKU_ID = s.SKU_ID;
-- drop FK
alter table TSKUPRICE drop foreign key FK_SP_SKU;
alter table TSKUPRICE drop constraint FK_SP_SKU;
-- drop column
alter table TSKUPRICE drop column SKU_ID;
-- add index to improve selects
create index SKUPRICE_SKUCODE on TSKUPRICE (SKU_CODE);


--
--  YC-566 Create flexible framework for adding new import descriptors
--


    create table TDATAGROUP (
        DATAGROUP_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        NAME varchar(255) not null unique,
        DISPLAYNAME longtext,
        QUALIFIER varchar(45),
        TYPE varchar(45) not null,
        DESCRIPTORS longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        primary key (DATAGROUP_ID)
    );


    create table TDATADESCRIPTOR (
        DATADESCRIPTOR_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        NAME varchar(255) not null unique,
        TYPE varchar(45) not null,
        VALUE longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        primary key (DATADESCRIPTOR_ID)
    );



--     create table TDATAGROUP (
--         DATAGROUP_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
--         VERSION bigint not null default 0,
--         NAME varchar(255) not null unique,
--         DISPLAYNAME varchar(4000),
--         QUALIFIER varchar(45),
--         TYPE varchar(45) not null,
--         DESCRIPTORS varchar(4000),
--         CREATED_TIMESTAMP timestamp,
--         UPDATED_TIMESTAMP timestamp,
--         CREATED_BY varchar(64),
--         UPDATED_BY varchar(64),
--         primary key (DATAGROUP_ID)
--     );
--
--
--     create table TDATADESCRIPTOR (
--         DATADESCRIPTOR_ID  bigint not null GENERATED BY DEFAULT AS IDENTITY,
--         VERSION bigint not null default 0,
--         NAME varchar(255) not null unique,
--         TYPE varchar(45) not null,
--         VALUE varchar(20000),
--         CREATED_TIMESTAMP timestamp,
--         UPDATED_TIMESTAMP timestamp,
--         CREATED_BY varchar(64),
--         UPDATED_BY varchar(64),
--         primary key (DATADESCRIPTOR_ID)
--     );

INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1000, 'YC DEMO: Initial Data', 'IMPORT', '
shopcurrencies-demo.xml,
countrynames-demo.xml,
statenames-demo.xml,
carriernames-demo.xml,
carriershopnames-demo.xml,
carrierslanames-demo.xml,
promotionnames-demo.xml,
promotioncouponnames-demo.xml,
taxnames-demo.xml,
taxconfignames-demo.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1001, 'YC DEMO: IceCat Catalog', 'IMPORT', '
brandnames-demo.xml,
attributenames-demo.xml,
attributegroupnames-demo.xml,
producttypenames-demo.xml,
categorynames-demo.xml,
shopcategory-demo.xml,
productypeattributeviewgroupnames-demo.xml,
producttypeattrnames-demo.xml,
productnames-demo.xml,
productsku-demo.xml,
productsattributes-demo.xml,
productskuattributes-demo.xml,
skuinventory-demo.xml,
skuprices-demo.xml,
productcategorynames-demo.xml,
productaccessories-demo.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1002, 'YC DEMO: Product images (IceCat)', 'IMPORT', 'productimages.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1003, 'Catalog import (Categories and attribute definitions)', 'IMPORT', '
brandnames.xml,
attributenames.xml,
attributegroupnames.xml,
producttypenames.xml,
categorynames.xml,
shopcategory.xml,
productypeattributeviewgroupnames.xml,
producttypeattrnames.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1004, 'Product import (Products, SKUs and attribute values)', 'IMPORT', '
productandcategorynames.xml,
productnames.xml,
productcategorynames.delete.xml,
productcategorynames.xml,
productsku.xml,
productsattributes.delete.all.xml,
productsattributes.delete.xml,
productsattributes.xml,
productskuattributes.delete.all.xml,
productskuattributes.delete.xml,
productskuattributes.xml,
productaccessories.delete.xml,
productaccessories.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1005, 'Inventory import', 'IMPORT', 'skuinventory.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1006, 'Prices, Promotions and Taxes import', 'IMPORT', '
skuprices.xml,
promotionnames.xml,
promotioncouponnames.xml,
taxnames.xml,
taxconfignames.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1007, 'Content import', 'IMPORT', 'contentnames.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1008, 'Images: Products and SKU', 'IMPORT', 'productimages.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1009, 'Images: Category and Content', 'IMPORT', 'categoryimages.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1010, 'Images: Brands', 'IMPORT', 'brandimages.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1011, 'Images: Shop', 'IMPORT', 'shopimages.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1012, 'Location import', 'IMPORT', 'countrynames.xml,statenames.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1013, 'Carriers and SLA import', 'IMPORT', 'carriernames.xml,carriershopnames.xml,carrierslanames.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1014, 'Shops and Warehouses import', 'IMPORT', 'shop.xml,warehouse.xml');


INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1000, 'attributegroupnames-demo.xml', 'WEBINF_XML', 'attributegroupnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1001, 'attributegroupnames.xml', 'WEBINF_XML', 'attributegroupnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1002, 'attributenames-demo.xml', 'WEBINF_XML', 'attributenames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1003, 'attributenames.xml', 'WEBINF_XML', 'attributenames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1004, 'brandimages.xml', 'WEBINF_XML', 'brandimages.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1005, 'brandnames-demo.xml', 'WEBINF_XML', 'brandnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1006, 'brandnames.xml', 'WEBINF_XML', 'brandnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1007, 'carriernames-demo.xml', 'WEBINF_XML', 'carriernames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1008, 'carriernames.xml', 'WEBINF_XML', 'carriernames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1009, 'carriershopnames-demo.xml', 'WEBINF_XML', 'carriershopnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1010, 'carriershopnames.xml', 'WEBINF_XML', 'carriershopnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1011, 'carrierslanames-demo.xml', 'WEBINF_XML', 'carrierslanames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1012, 'carrierslanames.xml', 'WEBINF_XML', 'carrierslanames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1013, 'categoryimages.xml', 'WEBINF_XML', 'categoryimages.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1014, 'categorynames-demo.xml', 'WEBINF_XML', 'categorynames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1015, 'categorynames.xml', 'WEBINF_XML', 'categorynames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1016, 'contentnames.xml', 'WEBINF_XML', 'contentnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1017, 'countrynames-demo.xml', 'WEBINF_XML', 'countrynames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1018, 'countrynames.xml', 'WEBINF_XML', 'countrynames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1019, 'productaccessories-demo.xml', 'WEBINF_XML', 'productaccessories-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1020, 'productaccessories.delete.xml', 'WEBINF_XML', 'productaccessories.delete.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1021, 'productaccessories.xml', 'WEBINF_XML', 'productaccessories.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1022, 'productandcategorynames.xml', 'WEBINF_XML', 'productandcategorynames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1023, 'productcategorynames-demo.xml', 'WEBINF_XML', 'productcategorynames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1024, 'productcategorynames.delete.xml', 'WEBINF_XML', 'productcategorynames.delete.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1025, 'productcategorynames.xml', 'WEBINF_XML', 'productcategorynames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1026, 'productimages.xml', 'WEBINF_XML', 'productimages.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1027, 'productnames-demo.xml', 'WEBINF_XML', 'productnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1028, 'productnames.xml', 'WEBINF_XML', 'productnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1029, 'productsattributes-demo.xml', 'WEBINF_XML', 'productsattributes-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1030, 'productsattributes.delete.all.xml', 'WEBINF_XML', 'productsattributes.delete.all.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1031, 'productsattributes.delete.xml', 'WEBINF_XML', 'productsattributes.delete.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1032, 'productsattributes.xml', 'WEBINF_XML', 'productsattributes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1033, 'productsku-demo.xml', 'WEBINF_XML', 'productsku-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1034, 'productsku.xml', 'WEBINF_XML', 'productsku.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1035, 'productskuattributes-demo.xml', 'WEBINF_XML', 'productskuattributes-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1036, 'productskuattributes.delete.all.xml', 'WEBINF_XML', 'productskuattributes.delete.all.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1037, 'productskuattributes.delete.xml', 'WEBINF_XML', 'productskuattributes.delete.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1038, 'productskuattributes.xml', 'WEBINF_XML', 'productskuattributes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1039, 'producttypeattrnames-demo.xml', 'WEBINF_XML', 'producttypeattrnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1040, 'producttypeattrnames.xml', 'WEBINF_XML', 'producttypeattrnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1041, 'producttypenames-demo.xml', 'WEBINF_XML', 'producttypenames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1042, 'producttypenames.xml', 'WEBINF_XML', 'producttypenames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1043, 'productypeattributeviewgroupnames-demo.xml', 'WEBINF_XML', 'productypeattributeviewgroupnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1044, 'productypeattributeviewgroupnames.xml', 'WEBINF_XML', 'productypeattributeviewgroupnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1045, 'promotioncouponnames-demo.xml', 'WEBINF_XML', 'promotioncouponnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1046, 'promotioncouponnames.xml', 'WEBINF_XML', 'promotioncouponnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1047, 'promotionnames-demo.xml', 'WEBINF_XML', 'promotionnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1048, 'promotionnames.xml', 'WEBINF_XML', 'promotionnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1049, 'shop.xml', 'WEBINF_XML', 'shop.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1050, 'shopcategory-demo.xml', 'WEBINF_XML', 'shopcategory-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1051, 'shopcategory.xml', 'WEBINF_XML', 'shopcategory.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1052, 'shopcurrencies-demo.xml', 'WEBINF_XML', 'shopcurrencies-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1053, 'shopimages.xml', 'WEBINF_XML', 'shopimages.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1054, 'skuinventory-demo.xml', 'WEBINF_XML', 'skuinventory-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1055, 'skuinventory.xml', 'WEBINF_XML', 'skuinventory.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1056, 'skuprices-demo.xml', 'WEBINF_XML', 'skuprices-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1057, 'skuprices.xml', 'WEBINF_XML', 'skuprices.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1058, 'statenames-demo.xml', 'WEBINF_XML', 'statenames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1059, 'statenames.xml', 'WEBINF_XML', 'statenames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1060, 'taxconfignames-demo.xml', 'WEBINF_XML', 'taxconfignames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1061, 'taxconfignames.xml', 'WEBINF_XML', 'taxconfignames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1062, 'taxnames-demo.xml', 'WEBINF_XML', 'taxnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1063, 'taxnames.xml', 'WEBINF_XML', 'taxnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1064, 'warehouse.xml', 'WEBINF_XML', 'warehouse.xml');




--
-- YC-361 Track customer's IP address
--

alter table TCUSTOMERORDER add column ORDER_IP varchar(45);


--
-- YC-568 Refactor payment modules
--

DELETE FROM TSYSTEMATTRVALUE WHERE GUID='YC_SYSTEM_PAYMENT_MODULES_URLS';
DELETE FROM TATTRIBUTE WHERE GUID='SYSTEM_PAYMENT_MODULES_URLS';

--
-- YC-540 Improve address domain model
--

alter table TADDRESS CHANGE PHONES PHONE1 varchar(255);
-- rename column TADDRESS.PHONES to PHONE1;
alter table TADDRESS add column PHONE2 varchar(255);
alter table TADDRESS add column EMAIL1 varchar(255);
alter table TADDRESS add column EMAIL2 varchar(255);
alter table TADDRESS add column MOBILE1 varchar(255);
alter table TADDRESS add column MOBILE2 varchar(255);
alter table TADDRESS add column CUSTOM1 varchar(255);
alter table TADDRESS add column CUSTOM2 varchar(255);
alter table TADDRESS add column CUSTOM3 varchar(255);
alter table TADDRESS add column CUSTOM4 varchar(255);

--
-- YC-567 Add support for German language
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11305,  'SHOP_IMAGE0_de',  'SHOP_IMAGE0_de',  1,  NULL,  'Shop: Shop standard image  (DE)',  null,  1003, 1001);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11310,  'BRAND_IMAGE0_de',  'BRAND_IMAGE0_de',  1,  NULL,  'Marke standard image  (DE)',  null,  1003, 1005);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11105,  'CATEGORY_IMAGE0_de',  'CATEGORY_IMAGE0_de',  0,  NULL,  'Kategorie standard image  (DE)',   null,  1003, 1002);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11015,  'CATEGORY_DESCRIPTION_de', 'CATEGORY_DESCRIPTION_de',  0,  NULL,  'Kategorie beschreibung (de)',
  'Kategorie Beschreibung in Deutsch (de)',  1011, 1002);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11090,  'CONTENT_BODY_de_1', 'CONTENT_BODY_de_1',  0,  NULL,  'Inhalt (de)',
  'Inhalt in Deutsch (de). erste 4000 Zeichen',  1011, 1002);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11091,  'CONTENT_BODY_de_2', 'CONTENT_BODY_de_2',  0,  NULL,  'Inhalt (de)',
  'Inhalt in Deutsch (de). zweite 4000 Zeichen',  1011, 1002);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11368,  'IMAGE0_de',  'IMAGE0_de',  1,  NULL,  'Produkt standard image  (DE)',  'Produkt standard image  (DE)',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11369,  'IMAGE1_de',  'IMAGE1_de',  0,  NULL,  'Produkt alternative image 1',  'Produkt alternative image 1',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11370,  'IMAGE2_de',  'IMAGE2_de',  0,  NULL,  'Produkt alternative image 2',  'Produkt alternative image 2',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11371,  'IMAGE3_de',  'IMAGE3_de',  0,  NULL,  'Produkt alternative image 3',  'Produkt alternative image 3',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11372,  'IMAGE4_de',  'IMAGE4_de',  0,  NULL,  'Produkt alternative image 4',  'Produkt alternative image 4',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11373,  'IMAGE5_de',  'IMAGE5_de',  0,  NULL,  'Produkt alternative image 5',  'Produkt alternative image 5',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11023,  'PRODUCT_DESCRIPTION_de', 'PRODUCT_DESCRIPTION_de',  0,  NULL,  'Produkt beschreibung (de)',  'Produkt beschreibung in Deutsch (de)',  1011, 1003);
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12017,'CONTENT_BODY_de_1','<pre>Copyright 2009 Igor Azarnyi, Denys Pavlov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
</pre>',10001,'12017_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12016,'CONTENT_BODY_de_1','
<p>Diese Seite zeigt dynamische Inhalte Features</p>

<p>Links:
<ul>
  <li><a href="${contentURL(''license'')}">Seite Lizenz (Inhalt Link)</a></li>
  <li><a href="${categoryURL(''netbooks'')}">Notebooks (Kategorie Link) </a></li>
  <li><a href="${URL('''')}">Start (Normal Link)</a></li>
</ul>
</p>

<p>Dynamische Variable: ${datetime}</p>

<p>Dynamische beinhalten:</p>

${include(''license'')}

',10002,'12016_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12026,'CONTENT_BODY_de_1','
    <meta name="google-site-verification" content="rHZLga_ppoOy7iVYFQgRVDZOLa7fuT7cGs2t8TY4m6c" />
',10003,'12026_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12036,'CONTENT_BODY_de_1','
    <script type="text/javascript">

        var _gaq = _gaq || [];
        _gaq.push([''_setAccount'', ''UA-4983157-12'']);
        _gaq.push([''_setDomainName'', ''yes-cart.org'']);
        _gaq.push([''_trackPageview'']);

        (function() {
            var ga = document.createElement(''script''); ga.type = ''text/javascript''; ga.async = true;
            ga.src = (''https:'' == document.location.protocol ? ''https://ssl'' : ''http://www'') + ''.google-analytics.com/ga.js'';
            var s = document.getElementsByTagName(''script'')[0]; s.parentNode.insertBefore(ga, s);
        })();

    </script>',10004,'12036_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12046,'CONTENT_BODY_de_1','
<span class="glyphicon glyphicon-envelope blue"></span>
<a href="http://www.yes-cart.org/#contact" target="_blank">Kontaktformular</a>
',10005,'12046_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12056,'CONTENT_BODY_de_1','
<div class="section-title"><h2>Warenkorb</h2></div>
',10006,'12056_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12066,'CONTENT_BODY_de_1','
<div class="section-title"><h2>Gutschein zufügen</h2></div>
',10007,'12066_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12076,'CONTENT_BODY_de_1','
<div class="section-title"><h2>Info zur Bestellung</h2></div>
<div class="clearfix"><% if (shoppingCart.orderMessage != null) { %>
aktuelle Meldung: "${shoppingCart.orderMessage}"<% } else { %>Bitte Text einfügen<% } %></div>
',10008,'12076_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12083,'CONTENT_BODY_de_1','
<small><b>Cookie Richtlinien</b><br>
 We may store information about you using cookies (files which are sent by us to your computer or other access device) which
 we can access when you visit our site in future. We do this to enhance user experience. If you want to delete any cookies that
 are already on your computer, please refer to the instructions for your file management software to locate the file or directory
 that stores cookies. Our cookies will have the file names JSESSIONID, yc and yccookiepolicy. Information on deleting or controlling
 cookies is available at <a href="http://www.AboutCookies.org" target="_blank">www.AboutCookies.org</a>. Please note that by
 deleting our cookies or disabling future cookies you may not be able to access certain areas or features of our site.</small>
',10009,'12083_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12093,'CONTENT_BODY_de_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<div class="col-xs-12"><ul class="wl-tag-cloud jsWishlistTagCloud"></ul></div>
</div>
',10010,'12093_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12098,'CONTENT_BODY_de_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<p>Sie sehen einen gemeinsamen Wunschzettel</p>
</div>
',10011,'12098_CAV');

--
-- YC-576 Shop specific mail servers
--


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10976,  'SHOP_MAIL_SERVER_CUSTOM_ENABLE', 'SHOP_MAIL_SERVER_CUSTOM_ENABLE',  0,  NULL,  'Mail: use custom mail server settings',
    'Enable custom mail server settings for this shop',  1008, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10975,  'SHOP_MAIL_SERVER_HOST', 'SHOP_MAIL_SERVER_HOST',  0,  NULL,  'Mail: custom mail server host',
    'Custom mail server host e.g. mail.somedomain.com',  1000, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10974,  'SHOP_MAIL_SERVER_PORT', 'SHOP_MAIL_SERVER_PORT',  0,  NULL,  'Mail: custom mail server port',
    'Custom mail server port e.g. 587',  1006, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10973,  'SHOP_MAIL_SERVER_USERNAME', 'SHOP_MAIL_SERVER_USERNAME',  0,  NULL,  'Mail: custom mail server username',
    'Custom mail server username. Required if SMTP-AUTH is enabled',  1000, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10972,  'SHOP_MAIL_SERVER_PASSWORD', 'SHOP_MAIL_SERVER_PASSWORD',  0,  NULL,  'Mail: custom mail server password',
    'Custom mail server password. Required if SMTP-AUTH is enabled',  1000, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10971,  'SHOP_MAIL_SERVER_SMTPAUTH_ENABLE', 'SHOP_MAIL_SERVER_SMTPAUTH_ENABLE',  0,  NULL,  'Mail: use custom mail server SMTP-AUTH enable',
    'Enable SMTP authentication on custom mail server (Require username and password)',  1008, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10970,  'SHOP_MAIL_SERVER_STARTTLS_ENABLE', 'SHOP_MAIL_SERVER_STARTTLS_ENABLE',  0,  NULL,  'Mail: use custom mail server TLS encryption enable',
    'Enable TLS encryption on custom mail server (Must be supported)',  1008, 1001);
