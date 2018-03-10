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
-- YC-804 Improved product associations by determining product type and grouping
--

alter table TPRODUCTTYPE add column DISPLAYNAME longtext;
-- alter table TPRODUCTTYPE add column DISPLAYNAME varchar(4000);

--
-- YC-805 Revise roles and permissions scheme
--

UPDATE TROLE SET DESCRIPTION = 'System admin (super user)' WHERE ROLE_ID = 1;
UPDATE TROLE SET DESCRIPTION = 'Shop manager (full access)' WHERE ROLE_ID = 2;
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (200, 'ROLE_SMSHOPUSER',     'ROLE_SMSHOPUSER', 'Shop user (read access)');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (201, 'ROLE_SMSUBSHOPUSER',     'ROLE_SMSUBSHOPUSER', 'Sub-shop manager (limited access)');
UPDATE TROLE SET DESCRIPTION = 'Inventory manager (full access)' WHERE ROLE_ID = 3;
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (300, 'ROLE_SMWAREHOUSEUSER','ROLE_SMWAREHOUSEUSER', 'Inventory user (read access)');
UPDATE TROLE SET DESCRIPTION = 'Call centre operator (read access)' WHERE ROLE_ID = 4;
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (400, 'ROLE_SMCALLCENTERCUSTOMER',    'ROLE_SMCALLCENTERCUSTOMER', 'Call centre customer manager (customer access)');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (401, 'ROLE_SMCALLCENTERORDERAPPROVE',    'ROLE_SMCALLCENTERORDERAPPROVE', 'Call centre order manager (approve orders)');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (402, 'ROLE_SMCALLCENTERORDERCONFIRM',    'ROLE_SMCALLCENTERORDERCONFIRM', 'Call centre order manager (confirm payments)');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (403, 'ROLE_SMCALLCENTERORDERPROCESS',    'ROLE_SMCALLCENTERORDERPROCESS', 'Call centre order manager (progress orders)');
UPDATE TROLE SET DESCRIPTION = 'Content manager (full access)' WHERE ROLE_ID = 5;
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (500, 'ROLE_SMCONTENTUSER',  'ROLE_SMCONTENTUSER', 'Content user (read access)');
UPDATE TROLE SET DESCRIPTION = 'Marketing manager (full access)' WHERE ROLE_ID = 6;
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (600, 'ROLE_SMMARKETINGUSER', 'ROLE_SMMARKETINGUSER', 'Marketing user (read access)');
UPDATE TROLE SET DESCRIPTION = 'Shipping manager (full access)' WHERE ROLE_ID = 7;
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (700, 'ROLE_SMSHIPPINGUSER',  'ROLE_SMSHIPPINGUSER', 'Shipping user (read access)');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (8, 'ROLE_SMCATALOGADMIN',  'ROLE_SMCATALOGADMIN', 'Catalog manager (full access)');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (800, 'ROLE_SMCATALOGUSER',  'ROLE_SMCATALOGUSER', 'Catalog user (read access)');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (9, 'ROLE_SMPIMADMIN',  'ROLE_SMPIMADMIN', 'PIM manager (full access)');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (900, 'ROLE_SMPIMUSER',  'ROLE_SMPIMUSER', 'PIM user (read access)');

--
-- YC-818	Add search suggest fade-out timeout configuration at shop level
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5017,  'SHOP_SEARCH_SUGGEST_FADE_OUT', 'SHOP_SEARCH_SUGGEST_FADE_OUT',  0,  NULL,  'Search: search suggest fade out (ms)',
  'Milliseconds after which result pop up should fade out (default is 3000)',  1006, 1001, 0, 0, 0, 0);


--
-- YC-819 Job for removing obsolete products
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11218,  'JOB_PROD_OBS_PAUSE', 'JOB_PROD_OBS_PAUSE',  0,  NULL,  'Job\\Obsolete product removal: pause',
    'Pause removing obsolete products job',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11219,  'JOB_PROD_OBS_MAX', 'JOB_PROD_OBS_MAX',  0,  NULL,  'Job\\Obsolete product removal: maximum offset for available to (days)',
    'Number of days after available to date which denotes obsolete products. Default: 365 (1yr)',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11220,  'JOB_PROD_OBS_BATCH_SIZE', 'JOB_PROD_OBS_BATCH_SIZE',  0,  NULL,  'Job\\Obsolete product removal: batch size',
    'Maximum products to remove per each run. Default: 500',  1006, 1000, 0, 0, 0, 0);

--
-- YC-826 Configurable shop specific integrations
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8025,  'SYSTEM_EXTENSION_CFG_PROPERTIES', 'SYSTEM_EXTENSION_CFG_PROPERTIES',  0,  NULL,  'System\\Customisations',
    'Property mapping for system customisations. E.g.
SHOP10.pricingPolicyProvider=[bean name]
SHOP10.priceResolver=[bean name]
SHOP10.taxProvider=[bean name]
SHOP10.productAvailabilityStrategy=[bean name]
Main.inventoryResolver=[bean name]',  1012, 1000, 0, 0, 0, 0);

--
-- YC-830 Performance improvements 3.4.x
-- YC-832 Refactor object images API
--

alter table TBRANDATTRVALUE add column INDEXVAL varchar(255);
update TBRANDATTRVALUE set INDEXVAL = VAL where CODE like 'BRAND_IMAGE%' or CODE like 'BRAND_FILE%';
create index AV_BRAND_VAL on TBRANDATTRVALUE (INDEXVAL);

alter table TCATEGORYATTRVALUE add column INDEXVAL varchar(255);
update TCATEGORYATTRVALUE set INDEXVAL = VAL where CODE like 'CATEGORY_IMAGE%' or CODE like 'CATEGORY_FILE%';
create index AV_CATEGORY_VAL on TCATEGORYATTRVALUE (INDEXVAL);

alter table TCUSTOMERATTRVALUE add column INDEXVAL varchar(255);
create index AV_CUSTOMER_VAL on TCUSTOMERATTRVALUE (INDEXVAL);

alter table TPRODUCTATTRVALUE add column INDEXVAL varchar(255);
update TPRODUCTATTRVALUE set INDEXVAL = VAL where CODE like 'IMAGE%' or CODE like 'FILE%';
create index AV_PRODUCT_VAL on TPRODUCTATTRVALUE (INDEXVAL);

alter table TPRODUCTSKUATTRVALUE add column INDEXVAL varchar(255);
update TPRODUCTSKUATTRVALUE set INDEXVAL = VAL where CODE like 'IMAGE%' or CODE like 'FILE%';
create index AV_SKU_VAL on TPRODUCTSKUATTRVALUE (INDEXVAL);

alter table TSHOPATTRVALUE add column INDEXVAL varchar(255);
update TSHOPATTRVALUE set INDEXVAL = VAL where CODE like 'SHOP_IMAGE%' or CODE like 'SHOP_FILE%' or CODE like 'SHOP_SYSFILE%';
create index AV_SHOP_VAL on TSHOPATTRVALUE (INDEXVAL);

alter table TSYSTEMATTRVALUE add column INDEXVAL varchar(255);
create index AV_SYSTEM_VAL on TSYSTEMATTRVALUE (INDEXVAL);

--
-- YC-837 Extendable API for cart items validation
--

update TATTRIBUTE set DESCRIPTION = 'Property mapping for system customisations. E.g.
SHOP10.pricingPolicyProvider=[bean name]
SHOP10.priceResolver=[bean name]
SHOP10.taxProvider=[bean name]
SHOP10.productAvailabilityStrategy=[bean name]
SHOP10.cartContentsValidator=[bean name]
Main.inventoryResolver=[bean name]' where CODE = 'SYSTEM_EXTENSION_CFG_PROPERTIES';


--
-- YC-838 Expose product type flags in Search DTO
--

-- OPTIONAL!!!! - Update shippable flag to true for all product types, since before this task
-- the product type's shippable flag was set to false by default and on some legacy systems
-- no one bothers to change it. If this was maintained then this update is not needed
update TPRODUCTTYPE set SHIPPABLE = 1;

--
-- YC-000 increase size of B2B charge ID

alter table TCUSTOMERORDER modify B2B_CHARGEID varchar(255);
-- alter table TCUSTOMERORDER alter column B2B_CHARGEID set data type varchar(255);

--
-- YC-821 Allow product type filter navigation
--

INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1099, 'java.lang.String', 'Locked', 'Locked');

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11026,  'productType', 'productType',  0,  NULL,  'Product type',  'Product type (used for product type navigation)',  1099, 1003, 0, 0, 0, 1);

--
-- YC-840 Sortable Brand facet
--

alter table TCATEGORY drop column NAV_BY_BRAND;

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11027,  'brand', 'brand',  0,  NULL,  'Product brand',  'Product brand (used for product type navigation)',  1099, 1003, 0, 0, 0, 1);

INSERT INTO TPRODUCTTYPEATTR (PRODTYPEATTR_ID, GUID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, NAV_TYPE, SIMILARITY)
  VALUES (501,'DefaultProductProductBrand', 'brand', 500, 500, 1, 'S', 0);

INSERT INTO TPRODUCTTYPEATTR (PRODTYPEATTR_ID, GUID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, NAV_TYPE, SIMILARITY)
  VALUES (550,'DefaultAccessoryProductType', 'productType', 501, 500, 1, 'S', 0);
INSERT INTO TPRODUCTTYPEATTR (PRODTYPEATTR_ID, GUID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, NAV_TYPE, SIMILARITY)
  VALUES (551,'DefaultAccessoryProductBrand', 'brand', 500, 501, 1, 'S', 0);


--
-- YC-841 Product tags filter navigation
--

alter table TPRODUCTTYPEATTR add column NAV_TEMPLATE varchar(64);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11028,  'tag', 'tag',  0,  NULL,  'Product tag',  'Product tag (used for product type navigation)',  1099, 1003, 0, 0, 0, 0);

INSERT INTO TPRODUCTTYPEATTR (PRODTYPEATTR_ID, GUID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, NAV_TYPE, SIMILARITY, NAV_TEMPLATE)
  VALUES (502,'DefaultProductProductTag', 'tag', 500, 500, 1, 'S', 0, 'i18n');
INSERT INTO TPRODUCTTYPEATTR (PRODTYPEATTR_ID, GUID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, NAV_TYPE, SIMILARITY, NAV_TEMPLATE)
  VALUES (552,'DefaultAccessoryProductTag', 'tag', 500, 501, 1, 'S', 0, 'i18n');

--
-- YC-820 Allow setting default product type for shop to allow filter navigation in global search
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5020,  'SHOP_DEF_NAV_CAT', 'SHOP_DEFAULT_NAVIGATION_CATEGORY',  0,  NULL,  'Search: default shop navigation category (GUID)',
    'GUID of category which contains default navigation settings',  1000, 1001, 0, 0, 0, 0);

--
-- YC-796 Allow shipping methods sorting by priority
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  4050,  'SHOP_CARRIER_SLA_DISABLED', 'SHOP_CARRIER_SLA_DISABLED',  0,  NULL,  'Disabled shop carrier SLA',  'Disabled shop carrier SLA (CSV of PKs)',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  4051,  'SHOP_CARRIER_SLA_RANKS', 'SHOP_CARRIER_SLA_RANKS',  0,  NULL,  'Properties config of carrier SLA ranks',  'Properties config of carrier SLA ranks (PK=rank)',  1012, 1001, 0, 0, 0, 0);

--
--  YC-853 Use JDK8 java.time.* instead of Date and SimpleDateFormat
--

update TETYPE set JAVATYPE = 'java.time.LocalDateTime', BUSINESSTYPE = 'DateTime', GUID = 'DateTime' where JAVATYPE = 'java.util.Date';
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1015, 'java.time.LocalDate', 'Date', 'Date');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1016, 'java.time.Instant', 'Timestamp', 'Timestamp');

--
--  YC-863 Improve CMS editor
--

update TATTRIBUTE  set DESCRIPTION = 'This URI points to preview CSS. For example:
SFW: "wicket/resource/org.yes.cart.web.page.HomePage/::/::/::/::/::/style/yc-preview.css"
SFG: "resources/style/yc-preview.css"' where GUID = 'SYSTEM_PREVIEW_URI_CSS';

update TATTRIBUTE set DESCRIPTION = 'This template is used to adjust URLs in content (<img src=""/> and <a href=""/>). For example:
DEV: http://{primaryShopURL}:8080/ where {primaryShopURL} is a placeholder for shop primary domain
PROD: http://{primaryShopURL}/ where {primaryShopURL} is a placeholder for shop primary domain'  where GUID = 'SYSTEM_PREVIEW_URL_TEMPLATE';

--
-- YC-872 Secure string attribute type
--

INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1017, 'java.lang.String', 'SecureString', 'SecureString');

alter table TATTRIBUTE add column SECURE_ATTRIBUTE bit not null default 0;
-- alter table TATTRIBUTE add column SECURE_ATTRIBUTE smallint not null default 0;

update TATTRIBUTE set ETYPE_ID = 1017 where GUID in (
  'SHOP_CUSTOMER_PASSWORD_RESET_CC',
  'SHOP_MAIL_SERVER_PASSWORD'
);
update TATTRIBUTE set SECURE_ATTRIBUTE = 1 where GUID in (
  'SHOP_CUSTOMER_PASSWORD_RESET_CC',
  'SHOP_MAIL_SERVER_CUSTOM_ENABLE',
  'SHOP_MAIL_SERVER_HOST',
  'SHOP_MAIL_SERVER_PORT',
  'SHOP_MAIL_SERVER_USERNAME',
  'SHOP_MAIL_SERVER_PASSWORD',
  'SHOP_MAIL_SERVER_STARTTLS_ENABLE',
  'SHOP_MAIL_SERVER_SMTPAUTH_ENABLE',
  'SHOP_SYSFILE0'
);

alter table TPAYMENTGATEWAYPARAMETER add column BUSINESSTYPE varchar(255);
alter table TPAYMENTGATEWAYPARAMETER add column SECURE_ATTRIBUTE bit not null default 0;
-- alter table TPAYMENTGATEWAYPARAMETER add column SECURE_ATTRIBUTE smallint not null default 0;

update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'authorizeNetAimPaymentGateway' and P_LABEL like '%API_LOGIN_ID';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'authorizeNetAimPaymentGateway' and P_LABEL like '%TRANSACTION_KEY';

update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'authorizeNetAimPaymentGateway' and P_LABEL like '%API_LOGIN_ID';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'authorizeNetAimPaymentGateway' and P_LABEL like '%TRANSACTION_KEY';

update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'authorizeNetSimPaymentGateway' and P_LABEL like '%API_LOGIN_ID';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'authorizeNetSimPaymentGateway' and P_LABEL like '%TRANSACTION_KEY';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'authorizeNetSimPaymentGateway' and P_LABEL like '%MD5_HASH_KEY';

update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'authorizeNetSimPaymentGateway' and P_LABEL like '%API_LOGIN_ID';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'authorizeNetSimPaymentGateway' and P_LABEL like '%TRANSACTION_KEY';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'authorizeNetSimPaymentGateway' and P_LABEL like '%MD5_HASH_KEY';

update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'cyberSourcePaymentGateway' and P_LABEL like '%merchantID';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'cyberSourcePaymentGateway' and P_LABEL like '%proxyPassword';

update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'cyberSourcePaymentGateway' and P_LABEL like '%merchantID';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'cyberSourcePaymentGateway' and P_LABEL like '%proxyHost';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'cyberSourcePaymentGateway' and P_LABEL like '%proxyPort';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'cyberSourcePaymentGateway' and P_LABEL like '%proxyUser';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'cyberSourcePaymentGateway' and P_LABEL like '%proxyPassword';

update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'liqPayPaymentGateway' and P_LABEL like '%LP_MERCHANT_ID';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'liqPayPaymentGateway' and P_LABEL like '%LP_MERCHANT_KEY';

update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'liqPayPaymentGateway' and P_LABEL like '%LP_MERCHANT_ID';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'liqPayPaymentGateway' and P_LABEL like '%LP_MERCHANT_KEY';

update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'liqPayNoRefundPaymentGateway' and P_LABEL like '%LP_MERCHANT_ID';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'liqPayNoRefundPaymentGateway' and P_LABEL like '%LP_MERCHANT_KEY';

update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'liqPayNoRefundPaymentGateway' and P_LABEL like '%LP_MERCHANT_ID';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'liqPayNoRefundPaymentGateway' and P_LABEL like '%LP_MERCHANT_KEY';

update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'payPalProPaymentGateway' and P_LABEL like '%API_USER_NAME';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'payPalProPaymentGateway' and P_LABEL like '%API_USER_PASSWORD';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'payPalProPaymentGateway' and P_LABEL like '%SIGNATURE';

update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'payPalProPaymentGateway' and P_LABEL like '%API_USER_NAME';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'payPalProPaymentGateway' and P_LABEL like '%API_USER_PASSWORD';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'payPalProPaymentGateway' and P_LABEL like '%SIGNATURE';

update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'payPalExpressPaymentGateway' and P_LABEL like '%API_USER_NAME';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'payPalExpressPaymentGateway' and P_LABEL like '%API_USER_PASSWORD';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'payPalExpressPaymentGateway' and P_LABEL like '%SIGNATURE';

update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'payPalExpressPaymentGateway' and P_LABEL like '%API_USER_NAME';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'payPalExpressPaymentGateway' and P_LABEL like '%API_USER_PASSWORD';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'payPalExpressPaymentGateway' and P_LABEL like '%SIGNATURE';

update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'payPalButtonPaymentGateway' and P_LABEL like '%PPB_USER';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'payPalButtonPaymentGateway' and P_LABEL like '%PPB_PASSWORD';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'payPalButtonPaymentGateway' and P_LABEL like '%PPB_SIGNATURE';

update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'payPalButtonPaymentGateway' and P_LABEL like '%PPB_USER';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'payPalButtonPaymentGateway' and P_LABEL like '%PPB_PASSWORD';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'payPalButtonPaymentGateway' and P_LABEL like '%PPB_SIGNATURE';

update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'postFinancePaymentGateway' and P_LABEL like '%PF_PSPID';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'postFinancePaymentGateway' and P_LABEL like '%PF_SHA_IN';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'postFinancePaymentGateway' and P_LABEL like '%PF_SHA_OUT';

update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'postFinancePaymentGateway' and P_LABEL like '%PF_PSPID';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'postFinancePaymentGateway' and P_LABEL like '%PF_SHA_IN';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'postFinancePaymentGateway' and P_LABEL like '%PF_SHA_OUT';

update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'postFinanceManualPaymentGateway' and P_LABEL like '%PF_PSPID';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'postFinanceManualPaymentGateway' and P_LABEL like '%PF_SHA_IN';
update TPAYMENTGATEWAYPARAMETER set BUSINESSTYPE = 'SecureString' where PG_LABEL = 'postFinanceManualPaymentGateway' and P_LABEL like '%PF_SHA_OUT';

update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'postFinanceManualPaymentGateway' and P_LABEL like '%PF_PSPID';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'postFinanceManualPaymentGateway' and P_LABEL like '%PF_SHA_IN';
update TPAYMENTGATEWAYPARAMETER set SECURE_ATTRIBUTE = 1 where PG_LABEL = 'postFinanceManualPaymentGateway' and P_LABEL like '%PF_SHA_OUT';

--
-- YC-000 RC review non-safe usage of variables in SHOP10_paymentpage_message content
--

update TCATEGORYATTRVALUE set VAL = '
<h2>Order Payment</h2>

<% if (result) { %>
   <p>
      Your order has been successfully created. You will receive confirmation by e-mail.
   </p>
   <a href="/" class="btn btn-primary2" rel="bookmark">Continue shopping</a>
   <% if (binding.hasVariable(''order'') && order.customer != null) { %>
      <a href="/orders" class="btn btn-primary" rel="nofollow">Check order status</a>
   <% } %>
<% } else {
   if (binding.hasVariable(''missingStock'') && missingStock !=null) { %>
      <p>
         Item ${product} with code ${sku} has just gone out of stock. Please try to buy similar product
      </p>
      <a href="/" class="btn btn-primary2" rel="bookmark">Back to Home page</a>
   <% } else { %>
      <p>
         An error occurred while trying to create your order. Please try again.
      </p>
      <a href="/" class="btn btn-primary2" rel="bookmark">Back to Home page</a>
   <% } %>
<% } %>

' where GUID = '12510_CAV';

update TCATEGORYATTRVALUE set VAL = '
<h2>Оплата заказа</h2>

<% if (result) { %>
   <p>
      Ваш заказ был успешно оформлен. Вы получите уведомление на электронный адрес.
   </p>
   <a href="/" class="btn btn-primary2" rel="bookmark">За новыми покупками</a>
   <% if (binding.hasVariable(''order'') && order.customer != null) { %>
       <a href="/orders" class="btn btn-primary" rel="nofollow">Проверить статус заказа</a>
   <% } %>
<% } else {
   if (missingStock !=null) { %>
      <p>
         Недостаточное количество ${product} (код ${sku}) на складе. Попробуйте купить похожий продукт. Приносим свои извинения
      </p>
      <a href="/" class="btn btn-primary2" rel="bookmark">Перейти на главную</a>
   <% } else { %>
      <p>
         Произошла ошибка при создании Вашего заказа. Попробуйте еще раз.
      </p>
      <a href="/" class="btn btn-primary2" rel="bookmark">Перейти на главную</a>
   <% } %>
<% } %>

' where GUID = '12511_CAV';

update TCATEGORYATTRVALUE set VAL = '
<h2>Оплата замовлення</h2>

<% if (result) { %>
   <p>
      Ваше замовлення було успішно оформлено. Ви отримаєте повідомлення на електронну адресу.
   </p>
   <a href="/" class="btn btn-primary2" rel="bookmark">За новими покупками</a>
   <% if (binding.hasVariable(''order'') && order.customer != null) { %>
      <a href="/orders" class="btn btn-primary" rel="nofollow">Перевірити статус замовлення</a>
   <% } %>
<% } else {
   if (binding.hasVariable(''missingStock'') && missingStock !=null) { %>
      <p>
         Недостатня кількість ${product} (код ${sku}) на складі. Спробуйте купити схожий товар. Приносимо вибачення
      </p>
      <a href="/" class="btn btn-primary2" rel="bookmark">Повернутися на головну</a>
   <% } else { %>
      <p>
         Сталася помилка при створені Вашого замовлення. Спробуйте ще раз.
      </p>
      <a href="/" class="btn btn-primary2" rel="bookmark">Повернутися на головну</a>
   <% } %>
<% } %>

' where GUID = '12513_CAV';

update TCATEGORYATTRVALUE set VAL = '
<h2>Order Payment</h2>

<% if (result) { %>
   <p>
      Ihre Bestellung wurde erfolgreich erstellt. Sie erhalten eine Bestätigung per E-Mail.
   </p>
   <a href="/" class="btn btn-primary2" rel="bookmark">Weiter mit Einkaufen</a>
   <% if (binding.hasVariable(''order'') && order.customer != null) { %>
     <a href="/orders" class="btn btn-primary" rel="nofollow">Status der Bestellung überprüfen</a>
   <% } %>
<% } else {
   if (binding.hasVariable(''missingStock'') && missingStock !=null) { %>
      <p>
         Leider ist der Artikel in der gewünschten Anzahl ${product} mit Artikel Nummer ${sku} nicht an Lager. Versuchen Sie ein vergleichbares Produkt zu kaufen.
      </p>
      <a href="/" class="btn btn-primary2" rel="bookmark">Zurück zur Startseite</a>
   <% } else { %>
      <p>
         Beim Erstellen Ihrer Bestellung ist ein Fehler aufgetreten. Bitte versuchen Sie es nochmals.
      </p>
      <a href="/" class="btn btn-primary2" rel="bookmark">Zurück zur Startseite</a>
   <% } %>
<% } %>

' where GUID = '12514_CAV';

--
-- YC-000 RC review non-safe usage of variables in SHOP10_resultpage_message content
--

update TCATEGORYATTRVALUE set VAL = '
<h2>Payment result</h2>
<%
def _status = binding.hasVariable(''status'') ? status : (binding.hasVariable(''hint'') ? hint : "");
if (_status.equals("ok")) { %>
	<p>Order successfully placed</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">Continue shopping</a>
  <% if (binding.hasVariable(''order'') && order?.customer != null) { %>
  	<a href="/orders" class="btn btn-primary" rel="nofollow">Check order status</a>
  <% } %>
<% } else if (_status.equals("cancel")) { %>
	<p>Order was cancelled. This maybe due to payment failure or insufficient stock</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">Continue shopping</a>
<% } else { %>
	<p>Errors in payment</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">Back to Homepage</a>
<% } %>

' where GUID = '12520_CAV';
update TCATEGORYATTRVALUE set VAL = '
<h2>Результат оплаты</h2>
<%
def _status = binding.hasVariable(''status'') ? status : (binding.hasVariable(''hint'') ? hint : "");
if (_status.equals("ok")) { %>
	<p>Заказ успешно оформлен</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">За новыми покупками</a>
  <% if (binding.hasVariable(''order'') && order?.customer != null) { %>
  	<a href="/orders" class="btn btn-primary" rel="nofollow">Проверить статус заказа</a>
  <% } %>
<% } else if (_status.equals("cancel")) { %>
	<p>Заказ отменен. Возможная причина - это ошибка при оплате, либо недостаточное кол-во товара на складе</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">За новыми покупками</a>
<% } else { %>
	<p>Ошибки при оплате</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">Перейти на главную</a>
<% } %>

' where GUID = '12521_CAV';
update TCATEGORYATTRVALUE set VAL = '
<h2>Результат оплати</h2>
<%
def _status = binding.hasVariable(''status'') ? status : (binding.hasVariable(''hint'') ? hint : "");
if (_status.equals("ok")) { %>
	<p>Замовлення успішно оформлене</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">За новими покупками</a>
  <% if (binding.hasVariable(''order'') && order?.customer != null) { %>
	  <a href="/orders" class="btn btn-primary" rel="nofollow">Перевірити статус замовлення</a>
	<% } %>
<% } else if (_status.equals("cancel")) { %>
	<p>Замовлення скасовано. Можлива причина - це помилка при оплаті, або недостатня кількість товару на складі</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">За новими покупками</a>
<% } else { %>
	<p>Помилка при оплаті</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">Повернутися на головну</a>
<% } %>

' where GUID = '12522_CAV';
update TCATEGORYATTRVALUE set VAL = '
<h2>Resultat des Zahlungsvorgangs</h2>
<%
def _status = binding.hasVariable(''status'') ? status : (binding.hasVariable(''hint'') ? hint : "");
if (_status.equals("ok")) { %>
	<p>Bestellung erfolgreich getätigt</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">Weiter Einkaufen / Zur Startseite</a>
  <% if (binding.hasVariable(''order'') && order?.customer != null) { %>
   	<a href="/orders" class="btn btn-primary" rel="nofollow">Status der Bestellung verfolgen</a>
  <% } %>
<% } else if (_status.equals("cancel")) { %>
	<p>Die Bestellung wurde annuliert oder die Artikel ist nicht mehr an Lager. Das kann der Grund für den Abbruch der Zahlung sein</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">Weiter Einkaufen / Zur Startseite</a>
<% } else { %>
	<p>Fehler bei der Zahlung</p>
	<a href="/" class="btn btn-primary2" rel="bookmark">Zurück zur Startseite</a>
<% } %>

' where GUID = '12523_CAV';

--
-- YC-809 Upgrade paypal-core
--

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14256, 'payPalProPaymentGateway',
'VERSION',
'204.0'
, 'Merchant API version', 'see https://developer.paypal.com/docs/classic/release-notes/merchant/PayPal_Merchant_API_Release_Notes_204/');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14360, 'payPalExpressPaymentGateway',
'VERSION',
'204.0'
, 'Merchant API version', 'see https://developer.paypal.com/docs/classic/release-notes/merchant/PayPal_Merchant_API_Release_Notes_204/');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14561, 'payPalButtonPaymentGateway', 'PPB_BUSINESS', '', 'Merchant email',
  'This value overrides ''Api user name'' and is passed as ''business'' parameter in button');

--
-- YC-000 RC Review increase CART_STATE to 64K
--

alter table TSHOPPINGCARTSTATE modify column CART_STATE varbinary(65536);
