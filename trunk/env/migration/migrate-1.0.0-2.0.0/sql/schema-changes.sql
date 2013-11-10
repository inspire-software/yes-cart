--
--  Copyright 2009 Igor Azarnyi, Denys Pavlov
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
-- YC-67 Add LOCALE column on TCUSTOMERORDER
--

alter table TCUSTOMERORDER add column LOCALE varchar(5) default 'en';



--
-- YC-277 Remove direct relationship between order details and product sku
--

-- add columns that allow blanks
alter table TCUSTOMERORDERDET add column CODE varchar(255);
alter table TCUSTOMERORDERDET add column PRODUCTNAME longtext;
alter table TCUSTOMERORDERDELIVERYDET add column CODE varchar(255);
alter table TCUSTOMERORDERDELIVERYDET add column PRODUCTNAME longtext;
-- Derby
-- alter table TCUSTOMERORDERDET add column CODE varchar(255);
-- alter table TCUSTOMERORDERDET add column PRODUCTNAME varchar(4000);
-- alter table TCUSTOMERORDERDELIVERYDET add column CODE varchar(255);
-- alter table TCUSTOMERORDERDELIVERYDET add column PRODUCTNAME varchar(4000);

-- set values from current relationship
update TCUSTOMERORDERDET o set CODE = (select s.CODE from TSKU s where s.SKU_ID = o.SKU_ID);
update TCUSTOMERORDERDELIVERYDET o set CODE = (select s.CODE from TSKU s where s.SKU_ID = o.SKU_ID);
-- This is a non-localised copy - so may need some clever way to do this but this is out of scope for YC
update TCUSTOMERORDERDET o set PRODUCTNAME = (select s.NAME from TSKU s where s.SKU_ID = o.SKU_ID);
update TCUSTOMERORDERDELIVERYDET o set PRODUCTNAME = (select s.NAME from TSKU s where s.SKU_ID = o.SKU_ID);

-- alter column not to allow blanks
alter table TCUSTOMERORDERDET modify column CODE varchar(255) not null;
alter table TCUSTOMERORDERDET modify column PRODUCTNAME longtext not null;
alter table TCUSTOMERORDERDELIVERYDET modify column CODE varchar(255) not null;
alter table TCUSTOMERORDERDELIVERYDET modify column PRODUCTNAME longtext not null;
-- Derby
-- alter table TCUSTOMERORDERDET alter column CODE not null;
-- alter table TCUSTOMERORDERDET alter column PRODUCTNAME not null;
-- alter table TCUSTOMERORDERDELIVERYDET alter column CODE not null;
-- alter table TCUSTOMERORDERDELIVERYDET alter column PRODUCTNAME not null;

-- drop indexes (this may be specific to MySQL, so possibly need to modify these steps)
alter table TCUSTOMERORDERDET drop foreign key FK_ODET_SKU;
alter table TCUSTOMERORDERDET drop index FK_ODET_SKU;
alter table TCUSTOMERORDERDET drop column SKU_ID;
alter table TCUSTOMERORDERDELIVERYDET drop foreign key FK_CODD_SKU;
alter table TCUSTOMERORDERDELIVERYDET drop index FK_CODD_SKU;
alter table TCUSTOMERORDERDELIVERYDET drop column SKU_ID;



--
-- YC-295 Carriers and sla i18n names and description
--

-- add columns that allow blanks
alter table TCARRIER add column DISPLAYNAME longtext;
-- alter table TCARRIER add column DISPLAYNAME varchar(4000);
alter table TCARRIER add column DISPLAYDESCRIPTION longtext;
-- alter table TCARRIER add column DISPLAYDESCRIPTION  varchar(4000);
alter table TCARRIERSLA add column DISPLAYNAME longtext;
-- alter table TCARRIERSLA add column DISPLAYNAME  varchar(4000);
alter table TCARRIERSLA add column DISPLAYDESCRIPTION longtext;
-- alter table TCARRIERSLA add column DISPLAYDESCRIPTION  varchar(4000);

-- modify descriptions to 1000 characters 4000 is too much
alter table TCARRIER modify column DESCRIPTION varchar(1000);
alter table TCARRIERSLA modify column DESCRIPTION varchar(1000);
-- Derby (derby cannot decrease the size so swap and rename)
-- alter table TCARRIER add column DESCRIPTION_TMP varchar(1000);
-- update TCARRIER set DESCRIPTION_TMP = DESCRIPTION;
-- alter table TCARRIER drop column DESCRIPTION;
-- rename column TCARRIER.DESCRIPTION_TMP to DESCRIPTION;
-- alter table TCARRIERSLA add column DESCRIPTION_TMP varchar(1000);
-- update TCARRIERSLA set DESCRIPTION_TMP = DESCRIPTION;
-- alter table TCARRIERSLA drop column DESCRIPTION;
-- rename column TCARRIERSLA.DESCRIPTION_TMP to DESCRIPTION;



--
-- YC-282 Cache director url should not be hardcoded.
--

DELETE FROM TSYSTEMATTRVALUE WHERE CODE = 'SYSTEM_BACKDOOR_URI';
DELETE FROM TATTRIBUTE WHERE CODE = 'SYSTEM_BACKDOOR_URI';
DELETE FROM TSYSTEMATTRVALUE WHERE CODE = 'SYSTEM_CACHEDIRECTOR_URI';
DELETE FROM TATTRIBUTE WHERE CODE = 'SYSTEM_CACHEDIRECTOR_URI';


--
-- YC-149 Backdoor service system preferences
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11042,  'IMPORT_JOB_LOG_SIZE', 'IMPORT_JOB_LOG_SIZE',  1,  NULL,  'System. Import log file size in YUM',
  'Size in characters of tail of actual log file to display in YUM during import', 1006,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11043,  'IMPORT_JOB_TIMEOUT_MS', 'IMPORT_JOB_TIMEOUT_MS',  1,  NULL,  'System. Import job timeout',
  'Timeout in ms during which no ping action performed by import', 1006,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11044,  'SYSTEM_BACKDOOR_TIMEOUT_MS', 'SYSTEM_BACKDOOR_TIMEOUT_MS',  1,  NULL,  'System. Webservice timeout',
  'Timeout in ms for web service calls', 1006,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11045,  'SYSTEM_BACKDOOR_PRODB_IDX_TIMEOUT_MS', 'SYSTEM_BACKDOOR_PRODB_IDX_TIMEOUT_MS',  1,  NULL,
  'System. Webservice bulk product index timeout', 'Timeout in ms for web service calls', 1006,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11046,  'SYSTEM_BACKDOOR_PRODS_IDX_TIMEOUT_MS', 'SYSTEM_BACKDOOR_PRODS_IDX_TIMEOUT_MS',  1,  NULL,
  'System. Webservice single product index timeout', 'Timeout in ms for web service calls', 1006,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11047,  'SYSTEM_BACKDOOR_SQL_TIMEOUT_MS', 'SYSTEM_BACKDOOR_SQL_TIMEOUT_MS',  1,  NULL,
  'System. Webservice SQL, HSQL, FTQL timeout', 'Timeout in ms for web service calls', 1006,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11048,  'SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS', 'SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS',  1,  NULL,
  'System. Webservice clear cache timeout', 'Timeout in ms for web service calls', 1006,  1000);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11049,  'SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS', 'SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS',  1,  NULL,
  'System. Webservice image operation timeout', 'Timeout in ms for web service calls', 1006,  1000);

INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)   VALUES (1015,'10000','IMPORT_JOB_LOG_SIZE',100, 'YC_IMPORT_JOB_LOG_SIZE');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)   VALUES (1016,'60000','IMPORT_JOB_TIMEOUT_MS',100, 'YC_IMPORT_JOB_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)   VALUES (1017,'60000','SYSTEM_BACKDOOR_TIMEOUT_MS',100, 'YC_SYSTEM_BACKDOOR_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)   VALUES (1018,'60000','SYSTEM_BACKDOOR_PRODB_IDX_TIMEOUT_MS',100, 'SYSTEM_BACKDOOR_PRODB_IDX_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)   VALUES (1019,'60000','SYSTEM_BACKDOOR_PRODS_IDX_TIMEOUT_MS',100, 'SYSTEM_BACKDOOR_PRODS_IDX_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)   VALUES (1020,'60000','SYSTEM_BACKDOOR_SQL_TIMEOUT_MS',100, 'YC_SYSTEM_BACKDOOR_SQL_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)   VALUES (1021,'60000','SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS',100, 'YC_SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)   VALUES (1022,'60000','SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS',100, 'YC_SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS');


--
-- YC-155 Hibernate versions to solve concurrent modifications issue
--

alter table TADDRESS add column VERSION bigint not null default 0;
alter table TASSOCIATION add column VERSION bigint not null default 0;
alter table TATTRIBUTE add column VERSION bigint not null default 0;
alter table TATTRIBUTEGROUP add column VERSION bigint not null default 0;
alter table TBRAND add column VERSION bigint not null default 0;
alter table TBRANDATTRVALUE add column VERSION bigint not null default 0;
alter table TCARRIER add column VERSION bigint not null default 0;
alter table TCARRIERSLA add column VERSION bigint not null default 0;
alter table TCATEGORY add column VERSION bigint not null default 0;
alter table TCATEGORYATTRVALUE add column VERSION bigint not null default 0;
alter table TCOUNTRY add column VERSION bigint not null default 0;
alter table TCUSTOMER add column VERSION bigint not null default 0;
alter table TCUSTOMERATTRVALUE add column VERSION bigint not null default 0;
alter table TCUSTOMERORDER add column VERSION bigint not null default 0;
alter table TCUSTOMERORDERDELIVERY add column VERSION bigint not null default 0;
alter table TCUSTOMERORDERDELIVERYDET add column VERSION bigint not null default 0;
alter table TCUSTOMERORDERDET add column VERSION bigint not null default 0;
alter table TCUSTOMERSHOP add column VERSION bigint not null default 0;
alter table TCUSTOMERWISHLIST add column VERSION bigint not null default 0;
alter table TENSEMBLEOPT add column VERSION bigint not null default 0;
alter table TETYPE add column VERSION bigint not null default 0;
alter table TMAILTEMPLATE add column VERSION bigint not null default 0;
alter table TMAILTEMPLATEGROUP add column VERSION bigint not null default 0;
alter table TMANAGER add column VERSION bigint not null default 0;
alter table TMANAGERROLE add column VERSION bigint not null default 0;
alter table TPRODTYPEATTRVIEWGROUP add column VERSION bigint not null default 0;
alter table TPRODUCT add column VERSION bigint not null default 0;
alter table TPRODUCTASSOCIATION add column VERSION bigint not null default 0;
alter table TPRODUCTATTRVALUE add column VERSION bigint not null default 0;
alter table TPRODUCTCATEGORY add column VERSION bigint not null default 0;
alter table TPRODUCTSKUATTRVALUE add column VERSION bigint not null default 0;
alter table TPRODUCTTYPE add column VERSION bigint not null default 0;
alter table TPRODUCTTYPEATTR add column VERSION bigint not null default 0;
alter table TROLE add column VERSION bigint not null default 0;
alter table TSEOIMAGE add column VERSION bigint not null default 0;
alter table TSHOP add column VERSION bigint not null default 0;
alter table TSHOPADVPLACE add column VERSION bigint not null default 0;
alter table TSHOPADVRULES add column VERSION bigint not null default 0;
alter table TSHOPATTRVALUE add column VERSION bigint not null default 0;
alter table TSHOPCATEGORY add column VERSION bigint not null default 0;
alter table TSHOPDISCOUNT add column VERSION bigint not null default 0;
alter table TSHOPDISCOUNTRULE add column VERSION bigint not null default 0;
alter table TSHOPEXCHANGERATE add column VERSION bigint not null default 0;
alter table TSHOPTOPSELLER add column VERSION bigint not null default 0;
alter table TSHOPURL add column VERSION bigint not null default 0;
alter table TSHOPWAREHOUSE add column VERSION bigint not null default 0;
alter table TSKU add column VERSION bigint not null default 0;
alter table TSKUPRICE add column VERSION bigint not null default 0;
alter table TSKUWAREHOUSE add column VERSION bigint not null default 0;
alter table TSTATE add column VERSION bigint not null default 0;
alter table TSYSTEM add column VERSION bigint not null default 0;
alter table TSYSTEMATTRVALUE add column VERSION bigint not null default 0;
alter table TWAREHOUSE add column VERSION bigint not null default 0;


--
-- YC-141 postcreate.sql indexes and constraints
--

create index IMAGE_NAME_IDX on TSEOIMAGE (IMAGE_NAME);


--
--  YC-258 Shopper segmentation
--

alter table TCUSTOMER add column TAG varchar(255);


--
--  YC-309 Add additional indexes
--

create index CUSTOMERORDER_NUM on TCUSTOMERORDER (ORDERNUM);
create index CUSTOMERORDER_CART on TCUSTOMERORDER (CART_GUID);

create index CUSTOMERORDERPAYMENT_ONUM on TCUSTOMERORDERPAYMENT (ORDER_NUMBER);
create index PAYMENTGATEWAYPARAMETER_PGL on TPAYMENTGATEWAYPARAMETER (PG_LABEL);


--
-- YC-303 Rework ShopDiscount and ShopDiscountRule domain objects
--

alter table TSHOPDISCOUNT drop foreign key FK_SD_SHOP;
alter table TSHOPDISCOUNTRULE drop foreign key FK_SDR_SDRULE;

drop table TSHOPDISCOUNTRULE;
drop table TSHOPDISCOUNT;

alter table TCUSTOMERORDERDET add column SALE_PRICE decimal(19,2) not null default 0;
-- alter table TCUSTOMERORDERDET add column SALE_PRICE numeric(19,2) not null default 0;
update TCUSTOMERORDERDET set SALE_PRICE = PRICE;
alter table TCUSTOMERORDERDET add column IS_GIFT bit not null default 0;
-- alter table TCUSTOMERORDERDET add column IS_GIFT smallint not null default 0;
alter table TCUSTOMERORDERDET add column IS_PROMO_APPLIED bit not null default 0;
-- alter table TCUSTOMERORDERDET add column IS_PROMO_APPLIED smallint not null default 0;
alter table TCUSTOMERORDERDET add column APPLIED_PROMO varchar(255);

alter table TCUSTOMERORDERDELIVERYDET add column SALE_PRICE decimal(19,2) not null default 0;
-- alter table TCUSTOMERORDERDELIVERYDET add column SALE_PRICE numeric(19,2) not null default 0;
update TCUSTOMERORDERDELIVERYDET set SALE_PRICE = PRICE;
alter table TCUSTOMERORDERDELIVERYDET add column IS_GIFT bit not null default 0;
-- alter table TCUSTOMERORDERDELIVERYDET add column IS_GIFT smallint not null default 0;
alter table TCUSTOMERORDERDELIVERYDET add column IS_PROMO_APPLIED bit not null default 0;
-- alter table TCUSTOMERORDERDELIVERYDET add column IS_PROMO_APPLIED smallint not null default 0;
alter table TCUSTOMERORDERDELIVERYDET add column APPLIED_PROMO varchar(255);

alter table TCUSTOMERORDERDELIVERY add column LIST_PRICE decimal(19,2) not null default 0;
-- alter table TCUSTOMERORDERDELIVERY add column LIST_PRICE numeric(19,2) not null default 0;
update TCUSTOMERORDERDELIVERY set LIST_PRICE = PRICE;
alter table TCUSTOMERORDERDELIVERY add column IS_PROMO_APPLIED bit not null default 0;
-- alter table TCUSTOMERORDERDELIVERY add column IS_PROMO_APPLIED smallint not null default 0;
alter table TCUSTOMERORDERDELIVERY add column APPLIED_PROMO varchar(255);

alter table TCUSTOMERORDER add column PRICE decimal(19,2) not null default 0;
-- alter table TCUSTOMERORDER add column PRICE numeric(19,2) not null default 0;
update TCUSTOMERORDER as ord set PRICE = (select sum(PRICE) from TCUSTOMERORDERDET as det where det.CUSTOMERORDER_ID = ord.CUSTOMERORDER_ID) where 0 < (select count(det2.CUSTOMERORDER_ID) from TCUSTOMERORDERDET as det2 where det2.CUSTOMERORDER_ID = ord.CUSTOMERORDER_ID);
alter table TCUSTOMERORDER add column LIST_PRICE decimal(19,2) not null default 0;
-- alter table TCUSTOMERORDER add column LIST_PRICE numeric(19,2) not null default 0;
update TCUSTOMERORDER as ord set LIST_PRICE = (select sum(LIST_PRICE) from TCUSTOMERORDERDET as det where det.CUSTOMERORDER_ID = ord.CUSTOMERORDER_ID) where 0 < (select count(det2.CUSTOMERORDER_ID) from TCUSTOMERORDERDET as det2 where det2.CUSTOMERORDER_ID = ord.CUSTOMERORDER_ID);
alter table TCUSTOMERORDER add column IS_PROMO_APPLIED bit not null default 0;
-- alter table TCUSTOMERORDER add column IS_PROMO_APPLIED smallint not null default 0;
alter table TCUSTOMERORDER add column APPLIED_PROMO varchar(255);


create table TPROMOTION (
    PROMOTION_ID bigint not null auto_increment,
    VERSION bigint not null default 0,
    CODE varchar(255) not null unique,
    RANK integer default 500,
    SHOP_CODE varchar(255) not null,
    CURRENCY varchar(5) not null,
    PROMO_TYPE varchar(1) not null,
    PROMO_ACTION varchar(1) not null,
    ELIGIBILITY_CONDITION longtext not null,
    PROMO_ACTION_CONTEXT varchar(255),
    NAME varchar(255) not null,
    DISPLAYNAME longtext,
    DESCRIPTION varchar(100),
    DISPLAYDESCRIPTION longtext,
    TAG varchar(255),
    CAN_BE_COMBINED bit not null,
    ENABLED bit not null,
    ENABLED_FROM datetime,
    ENABLED_TO datetime,
    CREATED_TIMESTAMP datetime,
    UPDATED_TIMESTAMP datetime,
    CREATED_BY varchar(64),
    UPDATED_BY varchar(64),
    GUID varchar(36) not null unique,
    primary key (PROMOTION_ID)
);

-- create table TPROMOTION (
--     PROMOTION_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
--     VERSION bigint not null default 0,
--     CODE varchar(255) not null unique,
--     SHOP_CODE varchar(255) not null,
--     CURRENCY varchar(5) not null,
--     PROMO_TYPE varchar(1) not null,
--     PROMO_ACTION varchar(1) not null,
--     ELIGIBILITY_CONDITION varchar(4000) not null,
--     PROMO_ACTION_CONTEXT varchar(255),
--     NAME varchar(255) not null,
--     DISPLAYNAME varchar(4000),
--     DESCRIPTION varchar(100),
--     DISPLAYDESCRIPTION varchar(4000),
--     TAG varchar(255),
--     CAN_BE_COMBINED smallint not null,
--     ENABLED smallint not null,
--     ENABLED_FROM timestamp,
--     ENABLED_TO timestamp,
--     CREATED_TIMESTAMP timestamp,
--     UPDATED_TIMESTAMP timestamp,
--     CREATED_BY varchar(64),
--     UPDATED_BY varchar(64),
--     GUID varchar(36) not null unique,
--     primary key (PROMOTION_ID)
-- );


create index PROMO_SHOP_CODE on TPROMOTION (SHOP_CODE);
create index PROMO_CURRENCY on TPROMOTION (CURRENCY);
create index PROMO_PTYPE on TPROMOTION (PROMO_TYPE);
create index PROMO_PACTION on TPROMOTION (PROMO_ACTION);
create index PROMO_ENABLED on TPROMOTION (ENABLED);
create index PROMO_ENABLED_FROM on TPROMOTION (ENABLED_FROM);
create index PROMO_ENABLED_TO on TPROMOTION (ENABLED_TO);

-- Codes on shop and promotion originally were not unique
drop index SHOP_CODE;
create unique index SHOP_CODE on TSHOP(CODE);
drop index PROMO_SHOP_CODE;
create unique index PROMO_SHOP_CODE on TPROMOTION(CODE);

-- Rank was missing from the original commit - add like so:
alter table TPROMOTION add column RANK integer default 500;


--
-- YC-318 Rework mail delivery
--


    create table TMAIL (
        MAIL_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        SHOP_CODE varchar(255),
        SUBJECT varchar(512) not null,
        EMAIL_FROM varchar(512) not null,
        EMAIL_RECEPIENTS varchar(512) not null,
        EMAIL_CC varchar(512),
        EMAIL_BCC varchar(512),
        TEXT_VERSION clob,
        HTML_VERSION clob,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PROMOTION_ID)
    );


--     create table TMAIL (
--         MAIL_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
--         VERSION bigint not null default 0,
--         SHOP_CODE varchar(255),
--         SUBJECT varchar(512) not null,
--         EMAIL_FROM varchar(512) not null,
--         EMAIL_RECEPIENTS varchar(512) not null,
--         EMAIL_CC varchar(512),
--         EMAIL_BCC varchar(512),
--         TEXT_VERSION clob,
--         HTML_VERSION clob,
--         CREATED_TIMESTAMP timestamp,
--         UPDATED_TIMESTAMP timestamp,
--         CREATED_BY varchar(64),
--         UPDATED_BY varchar(64),
--         GUID varchar(36) not null unique,
--         primary key (MAIL_ID)
--     );



    create table TMAILPART (
        MAILPART_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        MAIL_ID bigint not null default 0,
        RESOURCE_ID varchar(255),
        FILENAME varchar(255),
        PART_DATA blob,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PROMOTION_ID)
    );

--     create table TMAILPART (
--         MAILPART_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
--         VERSION bigint not null default 0,
--         MAIL_ID bigint not null default 0,
--         RESOURCE_ID varchar(255),
--         FILENAME varchar(255),
--         PART_DATA blob,
--         CREATED_TIMESTAMP timestamp,
--         UPDATED_TIMESTAMP timestamp,
--         CREATED_BY varchar(64),
--         UPDATED_BY varchar(64),
--         GUID varchar(36) not null unique,
--         primary key (MAIL_ID)
--     );



alter table TMAILPART         add constraint FKMAILMAILPART
        foreign key (MAIL_ID)
        references TMAIL
        on delete cascade;
