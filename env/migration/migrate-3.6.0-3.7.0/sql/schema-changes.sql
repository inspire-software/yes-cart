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
  l.SUPPLIER_CODE = 'Main',
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

