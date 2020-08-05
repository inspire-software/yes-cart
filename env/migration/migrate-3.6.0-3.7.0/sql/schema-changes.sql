--
--  Copyright 2009 Inspire-Software.com
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
-- YC-982 Swiss billing feature in PostFinance payment gateway
--

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15180, 'postFinancePaymentGateway', 'PF_DELIVERY_AND_INVOICE_ON', null, 'Enable invoice and delivery data',
  'Invoice and delivery information will be sent with the order (ECOM_*)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15181, 'postFinancePaymentGateway', 'PF_ITEMISED_ITEM_CAT', null, 'Enabled itemised data item category',
  'Refer to PostFinance documentation on what values can be set (if not blank will be set for all itemised parameters)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15182, 'postFinancePaymentGateway', 'PF_ITEMISED_SHIP_CAT', null, 'Enabled itemised data shipping category',
  'Refer to PostFinance documentation on what values can be set (if not blank will be set for all itemised parameters)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15184, 'postFinancePaymentGateway', 'PF_DELIVERY_AND_INVOICE_ADDR2_IS_NUMBER', null, 'Enable invoice and delivery data (line 2 is number)',
  'When invoice parameters (ECOM_*) are generated address line 2 will be used as *STREET_NUMBER');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15185, 'postFinancePaymentGateway', 'PF_DELIVERY_AND_INVOICE_ADDR1_NUMBER_REGEX', null, 'Enable invoice and delivery data (line 1 regex)',
  'When invoice parameters (ECOM_*) are generated regex is used on address line 1 to extract and populate *STREET_NUMBER');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15186, 'postFinancePaymentGateway', 'PF_ITEMISED_USE_TAX_AMOUNT', null, 'Enabled itemised data tax amount',
  'By default ITEMVATCODEX parameters are set with tax rate. When set to true ITEMVATX sent instead with tax amount');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15280, 'postFinanceManualPaymentGateway', 'PF_DELIVERY_AND_INVOICE_ON', null, 'Enable invoice and delivery data',
  'Invoice and delivery information will be sent with the order (ECOM_*)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15281, 'postFinanceManualPaymentGateway', 'PF_ITEMISED_ITEM_CAT', null, 'Enabled itemised data item category',
  'Refer to PostFinance documentation on what values can be set (if not blank will be set for all itemised parameters)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15282, 'postFinanceManualPaymentGateway', 'PF_ITEMISED_SHIP_CAT', null, 'Enabled itemised data shipping category',
  'Refer to PostFinance documentation on what values can be set (if not blank will be set for all itemised parameters)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15284, 'postFinanceManualPaymentGateway', 'PF_DELIVERY_AND_INVOICE_ADDR2_IS_NUMBER', null, 'Enable invoice and delivery data (line 2 is number)',
  'When invoice parameters (ECOM_*) are generated address line 2 will be used as *STREET_NUMBER');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15285, 'postFinanceManualPaymentGateway', 'PF_DELIVERY_AND_INVOICE_ADDR1_NUMBER_REGEX', null, 'Enable invoice and delivery data (line 1 regex)',
  'When invoice parameters (ECOM_*) are generated regex is used on address line 1 to extract and populate *STREET_NUMBER');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15286, 'postFinanceManualPaymentGateway', 'PF_ITEMISED_USE_TAX_AMOUNT', null, 'Enabled itemised data tax amount',
  'By default ITEMVATCODEX parameters are set with tax rate. When set to true ITEMVATX sent instead with tax amount');

--
-- YC-983 No payment required payment gateway
--


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10701, 'noPaymentRequired', 'name', 'No payment required', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10702, 'noPaymentRequired', 'name_en', 'No payment required', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10703, 'noPaymentRequired', 'name_ru', 'Оплата не требуется', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10704, 'noPaymentRequired', 'name_uk', 'Оплата не потрібна', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10705, 'noPaymentRequired', 'name_de', 'Keine Zahlung erforderlich', 'Gateway-Namen (DE)', 'Gateway-Namen (DE)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10750, 'noPaymentRequired', 'priority', '100', 'Gateway priority', 'Gateway priority');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10751, 'noPaymentRequired', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

--
-- YC-668 Allow configuration of fulfilment centres for Products
--

alter table TSKUWAREHOUSE add column DISABLED bit default 0;
alter table TSKUWAREHOUSE add column AVAILABLEFROM datetime;
alter table TSKUWAREHOUSE add column AVAILABLETO datetime;
alter table TSKUWAREHOUSE add column RELEASEDATE datetime;
alter table TSKUWAREHOUSE add column AVAILABILITY integer default 1 not null;
alter table TSKUWAREHOUSE add column FEATURED bit;
alter table TSKUWAREHOUSE add column TAG varchar(255);
alter table TSKUWAREHOUSE add column MIN_ORDER_QUANTITY decimal(19,2);
alter table TSKUWAREHOUSE add column MAX_ORDER_QUANTITY decimal(19,2);
alter table TSKUWAREHOUSE add column STEP_ORDER_QUANTITY decimal(19,2);
alter table TSKU add column TAG varchar(255);


-- Derby
-- alter table TSKUWAREHOUSE add column DISABLED smallint not null default 0;
-- alter table TSKUWAREHOUSE add column AVAILABLEFROM timestamp;
-- alter table TSKUWAREHOUSE add column AVAILABLETO timestamp;
-- alter table TSKUWAREHOUSE add column RELEASEDATE timestamp;
-- alter table TSKUWAREHOUSE add column AVAILABILITY integer default 1 not null;
-- alter table TSKUWAREHOUSE add column FEATURED smallint;
-- alter table TSKUWAREHOUSE add column TAG varchar(255);
-- alter table TSKUWAREHOUSE add column MIN_ORDER_QUANTITY numeric(19,2);
-- alter table TSKUWAREHOUSE add column MAX_ORDER_QUANTITY numeric(19,2);
-- alter table TSKUWAREHOUSE add column STEP_ORDER_QUANTITY numeric(19,2);
-- alter table TSKU add column TAG varchar(255);

update TSKUWAREHOUSE w, TPRODUCT p, TSKU s set
  w.DISABLED = p.DISABLED,
  w.AVAILABLEFROM = p.AVAILABLEFROM,
  w.AVAILABLETO = p.AVAILABLETO,
  w.AVAILABILITY = p.AVAILABILITY,
  w.FEATURED = p.FEATURED,
  w.MIN_ORDER_QUANTITY = p.MIN_ORDER_QUANTITY,
  w.MAX_ORDER_QUANTITY = p.MAX_ORDER_QUANTITY,
  w.STEP_ORDER_QUANTITY = p.STEP_ORDER_QUANTITY
  where w.SKU_CODE = s.CODE and s.PRODUCT_ID = p.PRODUCT_ID;

-- Derby
-- update TSKUWAREHOUSE set
--   DISABLED = case when (select DISABLED from TPRODUCT, TSKU where  TSKU.PRODUCT_ID = TPRODUCT.PRODUCT_ID and SKU_CODE = TSKU.CODE) = 1 then 1 else 0 end,
--   AVAILABLEFROM = (select AVAILABLEFROM from TPRODUCT, TSKU where  TSKU.PRODUCT_ID = TPRODUCT.PRODUCT_ID and SKU_CODE = TSKU.CODE),
--   AVAILABLETO = (select AVAILABLETO from TPRODUCT, TSKU where  TSKU.PRODUCT_ID = TPRODUCT.PRODUCT_ID and SKU_CODE = TSKU.CODE),
--   AVAILABILITY = case when (select AVAILABILITY from TPRODUCT, TSKU where  TSKU.PRODUCT_ID = TPRODUCT.PRODUCT_ID and SKU_CODE = TSKU.CODE) is not null then (select AVAILABILITY from TPRODUCT, TSKU where  TSKU.PRODUCT_ID = TPRODUCT.PRODUCT_ID and SKU_CODE = TSKU.CODE) else 1 end,
--   FEATURED = case when (select FEATURED from TPRODUCT, TSKU where  TSKU.PRODUCT_ID = TPRODUCT.PRODUCT_ID and SKU_CODE = TSKU.CODE) = 1 then 1 else 0 end,
--   MIN_ORDER_QUANTITY = (select MIN_ORDER_QUANTITY from TPRODUCT, TSKU where  TSKU.PRODUCT_ID = TPRODUCT.PRODUCT_ID and SKU_CODE = TSKU.CODE),
--   MAX_ORDER_QUANTITY = (select MAX_ORDER_QUANTITY from TPRODUCT, TSKU where  TSKU.PRODUCT_ID = TPRODUCT.PRODUCT_ID and SKU_CODE = TSKU.CODE),
--   STEP_ORDER_QUANTITY = (select STEP_ORDER_QUANTITY from TPRODUCT, TSKU where  TSKU.PRODUCT_ID = TPRODUCT.PRODUCT_ID and SKU_CODE = TSKU.CODE);

alter table TPRODUCT drop column DISABLED;
alter table TPRODUCT drop column AVAILABLEFROM;
alter table TPRODUCT drop column AVAILABLETO;
alter table TPRODUCT drop column AVAILABILITY;
alter table TPRODUCT drop column FEATURED;
alter table TPRODUCT drop column MIN_ORDER_QUANTITY;
alter table TPRODUCT drop column MAX_ORDER_QUANTITY;
alter table TPRODUCT drop column STEP_ORDER_QUANTITY;


alter table TCUSTOMERWISHLIST add column SKU_CODE varchar(255);
alter table TCUSTOMERWISHLIST add column SUPPLIER_CODE varchar(255);

update TCUSTOMERWISHLIST l, TSKU s set
  l.SKU_CODE = s.CODE,
  l.SUPPLIER_CODE = 'Main'
  where l.SKU_ID = s.SKU_ID;

-- Derby
-- update TCUSTOMERWISHLIST set
--   SKU_CODE = (select CODE from TSKU where TCUSTOMERWISHLIST.SKU_ID = TSKU.SKU_ID),
--   SUPPLIER_CODE = 'Main';


alter table TCUSTOMERWISHLIST modify column SKU_CODE varchar(255) not null;
alter table TCUSTOMERWISHLIST modify column SUPPLIER_CODE varchar(255) not null;
-- Derby
-- alter table TCUSTOMERWISHLIST alter column SKU_CODE not null;
-- alter table TCUSTOMERWISHLIST alter column SUPPLIER_CODE not null;

alter table TCUSTOMERWISHLIST drop foreign key constraint FK_WL_SKU;
-- Derby
-- alter table TCUSTOMERWISHLIST drop constraint FK_WL_SKU;
alter table TCUSTOMERWISHLIST drop column SKU_ID;




--
-- YC-828 Extend ProductAvailabilityModel to include next delivery information
--

alter table TSKUWAREHOUSE add column RESTOCKDATE datetime;
alter table TSKUWAREHOUSE add column RESTOCKNOTE varchar(255);

-- alter table TSKUWAREHOUSE add column RESTOCKDATE timestamp;
-- alter table TSKUWAREHOUSE add column RESTOCKNOTE varchar(255);

--
-- YC-996 Refactor PromotionCouponUsageEntity to use code
--

alter table TPROMOTIONCOUPONUSAGE add column COUPON_CODE varchar(255);

update TPROMOTIONCOUPONUSAGE u, TPROMOTIONCOUPON c set
  u.COUPON_CODE = c.CODE
  where u.COUPON_ID = c.PROMOTIONCOUPON_ID;

-- Derby
-- update TPROMOTIONCOUPONUSAGE set
--   COUPON_CODE = (select CODE from TPROMOTIONCOUPON where TPROMOTIONCOUPONUSAGE.COUPON_ID = TPROMOTIONCOUPON.PROMOTIONCOUPON_ID);

alter table TPROMOTIONCOUPONUSAGE modify column COUPON_CODE varchar(255) not null;
-- Derby
-- alter table TPROMOTIONCOUPONUSAGE alter column COUPON_CODE not null;


alter table TPROMOTIONCOUPONUSAGE drop foreign key constraint FK_COUPON_USAGE;
-- Derby
-- alter table TPROMOTIONCOUPONUSAGE drop constraint FK_COUPON_USAGE;
alter table TPROMOTIONCOUPONUSAGE drop column COUPON_ID;

create index PROMOTIONCOUPONUSAGE_CODE on TPROMOTIONCOUPONUSAGE (COUPON_CODE);


--
-- YC-962 Allow fulfilment centre specific pricing
--

alter table TSKUPRICE add column SUPPLIER varchar(255);

--
--  YC-1004 Security Control
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8026,  'SYSTEM_EXTENSION_CFG_SECURITY', 'SYSTEM_EXTENSION_CFG_SECURITY',  0,  NULL,  'System\\Customisations\\Security Control',
    'Property mapping for system security control service. E.g.
[NodeType].HTTP.maxRequestsPerMinute=1000
[NodeType].HTTP.maxRequestsPerMinutePerIP=60
[NodeType].HTTP.blockIPCSV=192.0.0.18,192.10
[NodeType].HTTP.allowIPCSV=192.0.0.18,192.10
After changing these settings configurations need to be reloaded (use cluster panel)',  1012, 1000, 0, 0, 0, 0);


--
-- YC-1003 Managed lists
--


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8017,  'SHOP_SF_MNGLIST_T', 'SHOP_SF_MANAGED_LIST_TYPES',  0,  NULL,  'Customer: managed list feature enabled (CSV)',  'CSV of customer types which can use managed lists
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8018,  'SHOP_SF_LOGIN_MGR_ROLES', 'SHOP_SF_LOGIN_MANAGER',  0,  NULL,  'Manager: allowed to login in SF',  'Allow managers with ROLE_SMCALLCENTERLOGINSF to login to SF',
  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8019,  'SHOP_SF_LOGIN_MGR_C_BLST', 'SHOP_SF_LOGIN_MANAGER_CUSTOMER_BLACKLIST',  0,  NULL,  'Manager: customer blacklisting',  'Allows to hide some accounts from managers that login to SF',
  1012, 1001, 0, 0, 0, 0);

INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (404, 'ROLE_SMCALLCENTERLOGINSF',    'ROLE_SMCALLCENTERLOGINSF', 'Call centre order manager (SF login)');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (405, 'ROLE_SMCALLCENTERLOGINONBEHALF',    'ROLE_SMCALLCENTERLOGINONBEHALF', 'Call centre order manager (login on behalf)');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (406, 'ROLE_SMCALLCENTERCREATEMANAGEDLISTS',    'ROLE_SMCALLCENTERCREATEMANAGEDLISTS', 'Call centre order manager (create managed lists)');

delete from TSHOPPINGCARTSTATE;
alter table TSHOPPINGCARTSTATE add MANAGED bit default 0 not null;
alter table TSHOPPINGCARTSTATE add SHOP_ID bigint default 0 not null;
-- Derby
-- alter table TSHOPPINGCARTSTATE add MANAGED smallint default 0 not null;
-- alter table TSHOPPINGCARTSTATE add SHOP_ID bigint default 0 not null;

create index SHOPPINGCARTSTATE_SHOP on TSHOPPINGCARTSTATE (SHOP_ID);

alter table TCUSTOMERWISHLIST add column NOTIFICATION_EMAIL varchar(255);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11322,  'SHOP_CUSTOMER_SORT_OPTIONS', 'SHOP_CUSTOMER_SORT_OPTIONS',  0,  NULL,  'Manager: sortable fields (CSV)',
    'List of sort fields separated by comma with fail over. Default is: lastname,companyName1,createdTimestamp.',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11323,  'SHOP_CUSTOMER_IMAGE_WIDTH',  'SHOP_CUSTOMER_IMAGE_WIDTH',  0,  NULL,  'Manager: Customer image width in list',   'Customer image width in list',  1006, 1001, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11324,  'SHOP_CUSTOMER_IMAGE_HEIGHT',  'SHOP_CUSTOMER_IMAGE_HEIGHT',  0,  NULL,  'Manager: Customer image height in list',   'Customer image height in list',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11325,  'SHOP_CUSTOMER_RECORDS_COLUMNS', 'SHOP_CUSTOMER_RECORDS_COLUMNS',  0,  NULL,  'Manager: Quantity of customer pods in one row in list',
   'Quantity of product pods in one row to show on category page',  1006, 1001, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV, DISPLAYNAME)
  VALUES (  11153,  'ORDER_MANAGER_NAME', 'ORDER_MANAGER_NAME',  0,  NULL,  'Order Manager Name',  'Order Manager Name', 1000,  1006, 0, 0, 0, 0,
  'en#~#Order Manager Name#~#uk#~#Менеджер замовлення#~#ru#~#Менеджер заказа#~#de#~#Auftragsmanager');

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV, DISPLAYNAME)
  VALUES (  11154,  'ORDER_MANAGER_EMAIL', 'ORDER_MANAGER_EMAIL',  0,  NULL,  'Order Manager Email',  'Order Manager Email', 1000,  1006, 0, 0, 0, 0,
  'en#~#Order Manager Email#~#uk#~#Email Менеджера замовлення#~#ru#~#Email Менеджера заказа#~#de#~#E-Mail des Auftragsmanagers');

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV, DISPLAYNAME)
  VALUES (  11155,  'managedListLine', 'managedListLine',  0,  NULL,  'List',  'List', 1000,  1006, 0, 0, 0, 0,
  'en#~#List#~#uk#~#Список#~#ru#~#Список#~#de#~#Liste');

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV, DISPLAYNAME)
  VALUES (  11156,  'ItemCostPrice', 'ItemCostPrice',  0,  NULL,  'Cost',  'Cost', 1000,  1006, 0, 0, 0, 0,
  'en#~#Procurement#~#uk#~#Закупівля#~#ru#~#Закупка#~#de#~#Beschaffung');


--
-- YC-802 Improved data federation
--

alter table TMANAGER add column PRODUCT_SUPPLIER_CATALOGS varchar(255);
alter table TMANAGER add column CATEGORY_CATALOGS varchar(1024);

-- example update all manager if Product.supplierCatalogCode had been used
-- update TMANAGER set PRODUCT_SUPPLIER_CATALOGS = 'ABC,DEF,XYZ';

--
-- New attributes for Date of birth for customer profiles
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV, REXP, DISPLAYNAME, V_FAILED_MSG)
VALUES (  11620,  'CUSTOMER_DOB_YEAR', 'CUSTOMER_DOB_YEAR',  1,  NULL,  'Date of Birth (Year)',  'Date of Birth (Year)', 1006,  1006, 0, 0, 0, 0,
          '^(19[0-9]{2}|20[0-9]{2})$',
          'de#~#Geburtsdatum (Jahr)#~#en#~#Date of Birth (Year)#~#ru#~#Дата рождения (год)#~#uk#~#Дата народження (рік)#~#',
          'en#~#''${input}'' is not a valid year (e.g. 1990)#~#uk#~#''${input}'' недійсний рік (наприклад, 1990)#~#ru#~#''${input}'' недействительный год (например, 1990)#~#de#~#''${input}'' ist kein gültiges Jahr (z. B. 1990)');
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV, CHOICES, DISPLAYNAME)
VALUES (  11621,  'CUSTOMER_DOB_MONTH', 'CUSTOMER_DOB_MONTH',  1,  NULL,  'Date of Birth (Month)',  'Date of Birth (Month)', 1004,  1006, 0, 0, 0, 0,
          'de#~#1-Jan,2-Feb,3-Mär,4-Apr,5-Mai,6-Jun,7-Jul,8-Aug,9-Sep,10-Okt,11-Nov,12-Dez#~#en#~#1-Jan,2-Feb,3-Mar,4-Apr,5-May,6-Jun,7-Jul,8-Aug,9-Sep,10-Oct,11-Nov,12-Dec#~#ru#~#1-янв,2-фев,3-мар,4-апр,5-май,6-июн,7-июл,8-авг,9-сен,10-окт,11-ноя,12-дек#~#uk#~#1-січ,2-лют,3-бер,4-кві,5-тра,6-чер,7-лип,8-сер,9-вер,10-жов,11-лис,12-гру#~#',
          'de#~#Geburtsdatum (Monat)#~#en#~#Date of Birth (Month)#~#ru#~#Дата рождения (Месяц)#~#uk#~#Дата народження (Місяць)#~#');
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV, REXP, DISPLAYNAME, V_FAILED_MSG)
VALUES (  11622,  'CUSTOMER_DOB_DAY', 'CUSTOMER_DOB_DAY',  1,  NULL,  'Date of Birth (Day)',  'Date of Birth (Day)', 1006,  1006, 0, 0, 0, 0,
          '^([1-9]{1}|1[0-9]|2[0-9]{1}|30|31)$',
          'de#~#Geburtsdatum (Tag)#~#en#~#Date of Birth (Day)#~#ru#~#Дата рождения (День)#~#uk#~#Дата народження (День)#~#',
          'en#~#''${input}'' is not a valid day (1 to 31)#~#uk#~#''${input}'' недійсний день (1-31)#~#ru#~#''${input}'' недействительный день (1-31)#~#de#~#''${input}'' ist kein gültiges Tag (1-31)');

--
-- YC-978 Remove Etype entity and create type property on attribute
-- YC-979 Remove AttributeGroup entity and create group property on attribute
--

alter table TATTRIBUTE add column ATTRIBUTEGROUP varchar(255);
alter table TATTRIBUTE add column ETYPE varchar(255);

alter table TATTRIBUTE drop foreign key FK_ATTRIBUTE_ATTRIBUTEGROUP;
alter table TATTRIBUTE drop index FK_ATTRIBUTE_ATTRIBUTEGROUP;

alter table TATTRIBUTE drop foreign key FK_ATTRIBUTE_ETYPE;
alter table TATTRIBUTE drop index FK_ATTRIBUTE_ETYPE;

create index ATTR_GROUP on TATTRIBUTE (ATTRIBUTEGROUP);
create index ATTR_ETYPE on TATTRIBUTE (ETYPE);

update TATTRIBUTE set ATTRIBUTEGROUP = (select CODE from TATTRIBUTEGROUP where TATTRIBUTE.ATTRIBUTEGROUP_ID = TATTRIBUTEGROUP.ATTRIBUTEGROUP_ID);
update TATTRIBUTE set ETYPE = (select BUSINESSTYPE from TETYPE where TETYPE.ETYPE_ID = TATTRIBUTE.ETYPE_ID);

alter table TATTRIBUTE drop column ATTRIBUTEGROUP_ID;
alter table TATTRIBUTE drop column ETYPE_ID;

--
-- YC-867 Bundle products and Collections
-- YC-1016 Configurable products
--

alter table TPRODUCTTYPE drop column ENSEMBLE;

alter table TPRODUCT add column CONFIGURABLE bit default 0;
alter table TPRODUCT add column NOT_SOLD_SEPARATELY bit default 0;
-- alter table TPRODUCT add column CONFIGURABLE smallint DEFAULT 0;
-- alter table TPRODUCT add column NOT_SOLD_SEPARATELY smallint DEFAULT 0;


alter table TCUSTOMERORDERDET add column ITEM_GROUP varchar(36);
alter table TCUSTOMERORDERDET add column CONFIGURABLE bit default 0;
alter table TCUSTOMERORDERDET add column NOT_SOLD_SEPARATELY bit default 0;
-- alter table TCUSTOMERORDERDET add column CONFIGURABLE smallint DEFAULT 0;
-- alter table TCUSTOMERORDERDET add column NOT_SOLD_SEPARATELY smallint DEFAULT 0;

alter table TCUSTOMERORDERDELIVERYDET add column ITEM_GROUP varchar(36);
alter table TCUSTOMERORDERDELIVERYDET add column CONFIGURABLE bit default 0;
alter table TCUSTOMERORDERDELIVERYDET add column NOT_SOLD_SEPARATELY bit default 0;
-- alter table TCUSTOMERORDERDELIVERYDET add column CONFIGURABLE smallint DEFAULT 0;
-- alter table TCUSTOMERORDERDELIVERYDET add column NOT_SOLD_SEPARATELY smallint DEFAULT 0;

drop table TENSEMBLEOPT;

    create table TPRODUCTOPT (
        PRODUCTOPT_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        MANDATORY bit not null,
        RANK integer,
        QTY numeric(19,2) not null default 1,
        PRODUCT_ID bigint not null,
        CODE varchar(255) not null,
        SKU_CODE varchar(255),
        OPTION_SKU_CODES varchar(255),
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODUCTOPT_ID)
    );

    alter table TPRODUCTOPT
        add index FK_OPT_PROD (PRODUCT_ID),
        add constraint FK_OPT_PROD
        foreign key (PRODUCT_ID)
        references TPRODUCT (PRODUCT_ID);

--     create table TPRODUCTOPT (
--         PRODUCTOPT_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
--         VERSION bigint not null DEFAULT 0,
--         MANDATORY smallint not null,
--         RANK integer,
--         QTY numeric(19,2) not null default 1,
--         PRODUCT_ID bigint not null,
--         CODE varchar(255) not null,
--         SKU_CODE varchar(255),
--         OPTION_SKU_CODES varchar(255),
--         CREATED_TIMESTAMP timestamp,
--         UPDATED_TIMESTAMP timestamp,
--         CREATED_BY varchar(64),
--         UPDATED_BY varchar(64),
--         GUID varchar(36) not null unique,
--         primary key (PRODUCTOPT_ID)
--     );
--
--     alter table TPRODUCTOPT
--         add constraint FK_OPT_PROD
--         foreign key (PRODUCT_ID)
--         references TPRODUCT;

--
-- YC-1021 Allow system setting to override custom mail server global preferences
--


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV, SECURE_ATTRIBUTE)
  VALUES (  11181,  'SYSTEM_MAIL_SERVER_CUSTOM_ENABLE', 'SYSTEM_MAIL_SERVER_CUSTOM_ENABLE',  0,  NULL,  'Mail: use custom mail server settings',
    'Enable custom mail server settings for this shop',  'Boolean', 'SYSTEM', 0, 0, 0, 0, 1);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV, SECURE_ATTRIBUTE)
  VALUES (  11182,  'SYSTEM_MAIL_SERVER_HOST', 'SYSTEM_MAIL_SERVER_HOST',  0,  NULL,  'Mail: custom mail server host',
    'Custom mail server host e.g. mail.somedomain.com',  'String', 'SYSTEM', 0, 0, 0, 0, 1);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV, SECURE_ATTRIBUTE)
  VALUES (  11183,  'SYSTEM_MAIL_SERVER_PORT', 'SYSTEM_MAIL_SERVER_PORT',  0,  NULL,  'Mail: custom mail server port',
    'Custom mail server port e.g. 587',  'Integer', 'SYSTEM', 0, 0, 0, 0, 1);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV, SECURE_ATTRIBUTE)
  VALUES (  11184,  'SYSTEM_MAIL_SERVER_USERNAME', 'SYSTEM_MAIL_SERVER_USERNAME',  0,  NULL,  'Mail: custom mail server username',
    'Custom mail server username. Required if SMTP-AUTH is enabled',  'String', 'SYSTEM', 0, 0, 0, 0, 1);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV, SECURE_ATTRIBUTE)
  VALUES (  11185,  'SYSTEM_MAIL_SERVER_PASSWORD', 'SYSTEM_MAIL_SERVER_PASSWORD',  0,  NULL,  'Mail: custom mail server password',
    'Custom mail server password. Required if SMTP-AUTH is enabled',  'SecureString', 'SYSTEM', 0, 0, 0, 0, 1);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV, SECURE_ATTRIBUTE)
  VALUES (  11186,  'SYSTEM_MAIL_SERVER_SMTPAUTH_ENABLE', 'SYSTEM_MAIL_SERVER_SMTPAUTH_ENABLE',  0,  NULL,  'Mail: use custom mail server SMTP-AUTH enable',
    'Enable SMTP authentication on custom mail server (Require username and password)',  'Boolean', 'SYSTEM', 0, 0, 0, 0, 1);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV, SECURE_ATTRIBUTE)
  VALUES (  11187,  'SYSTEM_MAIL_SERVER_STARTTLS_ENABLE', 'SYSTEM_MAIL_SERVER_STARTTLS_ENABLE',  0,  NULL,  'Mail: use custom mail server TLS encryption enable',
    'Enable TLS encryption on custom mail server (Must be supported)',  'Boolean', 'SYSTEM', 0, 0, 0, 0, 1);

--
-- YC-1024 Configuration for choosing HTTPS and HTTP protocols for absolute URLs
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  7999,  'SHOP_URL_PREFER_HTTP', 'SHOP_URL_PREFER_HTTP',  0,  NULL,  'Shop: URL prefer HTTP over HTTPs',  'Prefer HTTP over HTTPs in absolute links (e.g. sitemap.xml, API)',
  'Boolean', 'SHOP', 0, 0, 0, 0);

--
-- YC-1025 Feature toggle for Global Only searches
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  4999,  'SHOP_SEARCH_ENABLE_GLOBAL_ONLY', 'SHOP_SEARCH_ENABLE_GLOBAL_ONLY',  0,  NULL,  'Search: global only enable',
  'Enable global only search. If set to true free text search is performed globally always (customer is redirected away from current category)',  'Boolean', 'SHOP', 0, 0, 0, 0);
