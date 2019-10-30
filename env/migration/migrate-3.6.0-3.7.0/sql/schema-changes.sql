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




