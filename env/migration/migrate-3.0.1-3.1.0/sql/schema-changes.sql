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
-- YC-537 Display tax information on storefront
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10969,  'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO',  0,  NULL,  'Product: Enable price tax information',  'Enables price tax information on search results and product pages',  1008, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10968,  'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_N', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET',  0,  NULL,  'Product: Enable price tax information to show net prices',  'Additional configuration to configure showing net prices (without tax) if tax information is enabled, otherwise gross prices are shown',  1008, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10967,  'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_A', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT',  0,  NULL,  'Product: Enable price tax information to show tax amount',  'Additional configuration to configure showing tax amount if tax information is enabled, otherwise percentage is shown',  1008, 1001);

INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (20, 'true','SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO', 10, 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (21, 'false','SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET', 10, 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_N');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (22, 'false','SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT', 10, 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_A');


--
-- YC-584 Tidy up sql scripts for payment gateways
--

update TPAYMENTGATEWAYPARAMETER set P_LABEL = 'RELAY_RESPONSE_URL' where P_LABEL = 'RELAY_RESPONCE_URL';

--
-- YC-585 Add setting for shop to list allowed IP addresses for payment gateways
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10966,  'SHOP_PG_ALLOWED_IPS_REGEX', 'SHOP_PAYMENT_GATEWAYS_ALLOWED_IPS_REGEX',  0,  NULL,  'Payment Gateway: Allowed IPs regular expression',
'Regular expression to determine if PG callback is allowed from IP.
Blank means that all IPs are allowed.
If not blank allowed IP should match "regex.matcher(ip).matches()"
E.g. "^((192.168.0.)([0-9]){1,3})$" will match all IPs starting with "192.168.0."
WARNING: be careful with IPv4 vs IPv6',  1000, 1001);

--
--  YC-593 Improve payment domain model to include taxAmount field
--

alter table TCUSTOMERORDERPAYMENT add column ORDER_DELIVERY_TAX decimal(19,2) NOT NULL default 0;

--
-- YC-604 Add standard listener for local FS auto import
--

delete from TSYSTEMATTRVALUE where CODE = 'JOB_SEND_MAIL_PAUSE';
delete from TATTRIBUTE where CODE = 'JOB_SEND_MAIL_PAUSE';

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11104,  'JOB_SEND_MAIL_PAUSE', 'JOB_SEND_MAIL_PAUSE',  0,  NULL,  'Job\Mail: pause mail processing',
    'Pause email sending job',  1008, 1000);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11106,  'JOB_LOCAL_FILE_IMPORT_PAUSE', 'JOB_LOCAL_FILE_IMPORT_PAUSE',  0,  NULL,  'Job\Auto Import: pause import listener',
    'Pause local file system import listener',  1008, 1000);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11107,  'JOB_LOCAL_FILE_IMPORT_FS_ROOT', 'JOB_LOCAL_FILE_IMPORT_FS_ROOT',  0,  NULL,  'Job\Auto Import: listener directory root',
    'Directory root for listener to check for updates',  1001, 1000);

delete from TSYSTEMATTRVALUE where CODE = 'JOB_DEL_WAITING_INV_LAST_RUN';
delete from TATTRIBUTE where CODE = 'JOB_DEL_WAITING_INV_LAST_RUN';

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11108,  'JOB_DEL_WAITING_INV_LAST_RUN', 'JOB_DEL_WAITING_INV_LAST_RUN',  0,  NULL,  'Job\Inventory Reservation: Last run timestamp',
    'Timestamp of last run of the inventory job. Used to check inventory changes.',  1009, 1000);

--
-- YC-596 Empty carts clean up
--

alter table TSHOPPINGCARTSTATE add column EMPTY bit not null default 0;
-- alter table TSHOPPINGCARTSTATE add column EMPTY smallint not null default 0;

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11109,  'CART_EMPTY_ANONYMOUS_TIMEOUT_SECONDS', 'CART_EMPTY_ANONYMOUS_TIMEOUT_SECONDS',  0,  NULL,  'Cart: empty anonymous in seconds',
    'Cart empty anonymous seconds. All empty anonymous carts are deleted by bulk job. Default: 86400s (1 days)',  1006, 1000);

--
-- YC-609 Add shop level settings for storefront
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11110,  'SHOP_CATEGORY_ITEMS_FEATURED', 'SHOP_CATEGORY_ITEMS_FEATURED',  0,  NULL,  'Category: Quantity of featured items to show on category page',
   'How many featured items need to show',  1006, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11111,  'SHOP_CATEGORY_ITEMS_NEW_ARRIVAL', 'SHOP_CATEGORY_ITEMS_NEW_ARRIVAL',  0,  NULL,  'Category: Quantity of new arrival items to show on category page',
   'Quantity of new arrival items to show on category page',  1006, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11112,  'SHOP_CATEGORY_PRODUCTS_COLUMNS', 'SHOP_CATEGORY_PRODUCTS_COLUMNS',  0,  NULL,  'Category: Quantity of product pods in one row on category page',
   'Quantity of product pods in one row to show on category page',  1006, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11113,  'SHOP_CATEGORY_SUBCATEGORIES_COLUMNS', 'SHOP_CATEGORY_SUBCATEGORIES_COLUMNS',  0,  NULL,  'Category: Quantity of category pods in one row on category page',
   'Quantity of product pods in one row to show on category page',  1006, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11057,  'CATEGORY_PRODUCTS_COLUMNS', 'CATEGORY_PRODUCTS_COLUMNS',  0,  NULL,  'Quantity of product pods in one row on category page',
   'Quantity of product pods in one row to show on category page',  1006, 1002);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11056,  'CATEGORY_SUBCATEGORIES_COLUMNS', 'CATEGORY_SUBCATEGORIES_COLUMNS',  0,  NULL,  'Quantity of category pods in one row on category page',
   'Quantity of category pods in one row to show on category page',  1006, 1002);

--
-- YC-613 Category product thumbnail image sizes are not applied
--

UPDATE TATTRIBUTE SET CODE = 'PRODUCT_IMAGE_THUMB_WIDTH', GUID = 'PRODUCT_IMAGE_THUMB_WIDTH' where GUID = 'PRODUCT_IMAGE_TUMB_WIDTH';
UPDATE TATTRIBUTE SET CODE = 'PRODUCT_IMAGE_THUMB_HEIGHT', GUID = 'PRODUCT_IMAGE_THUMB_HEIGHT' where GUID = 'PRODUCT_IMAGE_TUMB_HEIGHT';

DELETE FROM TCATEGORYATTRVALUE where CODE = 'PRODUCT_IMAGE_TUMB_WIDTH';
DELETE FROM TCATEGORYATTRVALUE where CODE = 'PRODUCT_IMAGE_TUMB_HEIGHT';
