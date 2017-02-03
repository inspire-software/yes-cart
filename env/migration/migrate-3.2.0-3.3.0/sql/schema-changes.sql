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
-- YC-719 Customer object should support pricing policy attribute
--

alter table TCUSTOMER add column PRICINGPOLICY varchar(255);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11063,  'pricingpolicy', 'pricingpolicy',  1,  NULL,  'Customer Pricing Policy',  'Customer Pricing Policy', 1000,  1006, 0, 0, 0, 0);

--
-- YC-720 SkuPrice object should support pricing policy attribute
--

alter table TSKUPRICE add column PRICINGPOLICY varchar(255);

create index SKUPRICE_PRICINGPOLICY on TSKUPRICE (PRICINGPOLICY);

--
-- YC-724 Search products and sku by manufacturing code optimizations
--

alter table TPRODUCT add column PIM_CODE varchar(255);

create index PRODUCT_MCODE on TPRODUCT (MANUFACTURER_CODE);
create index PRODUCT_PCODE on TPRODUCT (PIM_CODE);
create index SKU_MCODE on TSKU (MANUFACTURER_CODE);
create index SKU_BCODE on TSKU (BARCODE);

--
-- YC-727 Disable shops
--

alter table TSHOP add column DISABLED bit not null default 0;
-- alter table TSHOP add column DISABLED smallint not null default 0;


--
-- YC-694 Change SLA pricing to use price lists
--

alter table TCARRIERSLA drop column PRICE;
alter table TCARRIERSLA drop column PER_CENT;
alter table TCARRIERSLA drop column PRICE_NOTLESS;
alter table TCARRIERSLA drop column PERCENT_NOTLESS;
alter table TCARRIERSLA drop column COST_NOTLESS;
alter table TCARRIERSLA drop column COST_NOTLESS;
alter table TCARRIERSLA drop column CURRENCY;

UPDATE TDATAGROUP SET DESCRIPTORS = '
shopcurrencies-demo.xml,
countrynames-demo.xml,
statenames-demo.xml,
carriernames-demo.xml,
carriershopnames-demo.xml,
carrierslanames-demo.xml,
promotionnames-demo.xml,
promotioncouponnames-demo.xml,
skuprices-demo.xml,
taxnames-demo.xml,
taxconfignames-demo.xml' WHERE NAME = 'YC DEMO: Initial Data';

--
-- YC-730 Shipping cost strategy based on weight and/or volume
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11024,  'PRODUCT_WEIGHT_KG', 'PRODUCT_WEIGHT_KG',  0,  NULL,  'Product weight KG',  'Product weight KG',  1005, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11025,  'PRODUCT_VOLUME_M3', 'PRODUCT_VOLUME_M3',  0,  NULL,  'Product volume m3',  'Product volume m3',  1005, 1003, 0, 0, 0, 0);

--
-- YC-734 Improve access to carriers for shop owners
--

alter table TCARRIERSHOP add column DISABLED bit default 0;
-- alter table TCARRIERSHOP add column DISABLED smallint default 0;

create index I_CRS_SHOP_DISABLED on TCARRIERSHOP (DISABLED);


--
-- Tidy up attribute types
--

update TATTRIBUTE set ETYPE_ID = 1001 where CODE = 'SYSTEM_IMAGE_VAULT';

--
-- YC-739 Improve access to warehouse for shop owners
--

alter table TSHOPWAREHOUSE add column DISABLED bit default 0;
-- alter table TSHOPWAREHOUSE add column DISABLED smallint default 0;

create index I_SWE_SHOP_DISABLED on TSHOPWAREHOUSE (DISABLED);

--
-- YC-743 Improve access to customers for shop owners
--

alter table TCUSTOMERSHOP add column DISABLED bit default 0;
-- alter table TCUSTOMERSHOP add column DISABLED smallint default 0;

create index I_CS_SHOP_DISABLED on TCUSTOMERSHOP (DISABLED);

--
-- YC-748 Improve preview capabilities for CMS in JAM
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11017,  'SYSTEM_PREVIEW_URL_TEMPLATE', 'SYSTEM_PREVIEW_URL_TEMPLATE',  1,  NULL,  'Admin\\CMS preview URL template',
  'This template is used to adjust URLs in content (<img src=""/> and <a href=""/>). For example: http://{primaryShopURL}:8080/ where {primaryShopURL} is a placeholder', 1000,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11018,  'SYSTEM_PREVIEW_URI_CSS', 'SYSTEM_PREVIEW_URI_CSS',  1,  NULL,  'Admin\\CMS preview CSS URI',
  'This URI points to preview CSS. For example on SFW: "yes-shop/wicket/resource/org.yes.cart.web.page.HomePage/::/::/::/::/::/style/yc-preview.css"', 1000,  1000, 0, 0, 0, 0);


--
-- YC-749 Perform order splitting by fulfilment centre
--

alter table TCUSTOMERORDERDELIVERYDET add column SUPPLIER_CODE varchar(255);
alter table TCUSTOMERORDERDET add column SUPPLIER_CODE varchar(255);

-- Template for updating order details for specific shop to specific supplier code - this has to be warehouse.CODE
-- assigned to the shop:
-- update TCUSTOMERORDERDET od set od.SUPPLIER_CODE = 'XXX' where od.CUSTOMERORDER_ID in (select o.CUSTOMERORDER_ID from TCUSTOMERORDER o where o.SHOP_ID = xxx);
update TCUSTOMERORDERDET od set od.SUPPLIER_CODE = 'Main' where od.CUSTOMERORDER_ID in (select o.CUSTOMERORDER_ID from TCUSTOMERORDER o where o.SHOP_ID = 10);
-- update TCUSTOMERORDERDELIVERYDET od set od.SUPPLIER_CODE = 'XXX' where od.CUSTOMERORDERDELIVERY_ID in (select d.CUSTOMERORDERDELIVERY_ID from TCUSTOMERORDER o, TCUSTOMERORDERDELIVERY d where o.CUSTOMERORDER_ID = d.CUSTOMERORDER_ID and o.SHOP_ID = xxx);
update TCUSTOMERORDERDELIVERYDET od set od.SUPPLIER_CODE = 'Main' where od.CUSTOMERORDERDELIVERY_ID in (select d.CUSTOMERORDERDELIVERY_ID from TCUSTOMERORDER o, TCUSTOMERORDERDELIVERY d where o.CUSTOMERORDER_ID = d.CUSTOMERORDER_ID and o.SHOP_ID = 10);


alter table TWAREHOUSE add column DISPLAYNAME longtext;
-- alter table TWAREHOUSE add column DISPLAYNAME varchar(4000);

alter table TCARRIERSLA add column SUPPORTED_FCS varchar(1024);

--
-- YC-737 Split ImpexFederationFilter into two separate filters
--

INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2006, 'Export Attribute Definitions', 'EXPORT', 'attributes.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2007, 'Export Brands', 'EXPORT', 'brands.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2008, 'Export Product Types (Definitions)', 'EXPORT', 'producttypes.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2009, 'Export Product Types (Attributes)', 'EXPORT', 'producttypeattributes.xml');

INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2006, 'attributes.xml', 'WEBINF_XML', 'attributes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2007, 'brands.xml', 'WEBINF_XML', 'brands.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2008, 'producttypes.xml', 'WEBINF_XML', 'producttypes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2009, 'producttypeattributes.xml', 'WEBINF_XML', 'producttypeattributes.xml');

--
-- YC-707 Shopping cart object should have setting to include or exclude taxes
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10965,  'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_T', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_TYPES',  0,  NULL,  'Product: Limit price tax information to customer types',
  'Limits price tax information on search results and product pages for given customer types. Tax info must be enabled. Blank value is treated as no restriction. To reference guests use B2G, blank types are treated as B2C.',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10964,  'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_C', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE',  0,  NULL,  'Product: Allow changing price tax information view for customer types',
  'Allow changing price tax information view for customer types. Blank value is treated as no customer can change tax information view. To reference guests use B2G, blank types are treated as B2C.',  1004, 1001, 0, 0, 0, 0);

--
-- YC-751 Global product reindex must prevent inventory changed processor from running
--


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11116,  'JOB_REINDEX_PRODUCT_BATCH_SIZE', 'JOB_REINDEX_PRODUCT_BATCH_SIZE',  0,  NULL,  'Job\\Product re-index: batch size',
    'Number of products to reindex in single batch.',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11117,  'JOB_PRODINVUP_DELTA', 'JOB_PRODINVUP_DELTA',  0,  NULL,  'Job\\Inventory Change Detection: max delta after delay',
    'Number of inventory records that had changed after a second the job started (default is 100). If changes exceed this number then re-indexing is postponed.',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11118,  'JOB_PRODINVUP_FULL', 'JOB_PRODINVUP_FULL',  0,  NULL,  'Job\\Inventory Change Detection: changes for full reindex',
    'Number of inventory records that should trigger full re-index rather than partial. Default is 1000',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11119,  'JOB_PRODINVUP_DELTA_S', 'JOB_PRODINVUP_DELTA_S',  0,  NULL,  'Job\\Inventory Change Detection: delta delay in seconds',
    'Delay for delta check. Default 15s.',  1006, 1000, 0, 0, 0, 0);

--
-- YC-754 Closed access shops
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8004,  'SHOP_SF_REQUIRE_LOGIN', 'SHOP_SF_REQUIRE_LOGIN',  1,  NULL,  'Customer: login required',  'Anonymous browsing for this shop is prohibited',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8005,  'SHOP_SF_REQUIRE_REG_AT', 'SHOP_SF_REQUIRE_REG_APPROVE_TYPES',  0,  NULL,  'Customer: registration types that require approval email (CSV)',  'CSV of customer types that must be approved by shop admin
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8006,  'SHOP_SF_REQUIRE_REG_NT', 'SHOP_SF_REQUIRE_REG_NOTIFY_TYPES',  0,  NULL,  'Customer: registration types that require notification email (CSV)',  'CSV of customer types for which shop admin is notified
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

--
-- YC-000 Organise Shop Preferences naming
--

update TATTRIBUTE set name = 'Tax: Enable price tax information' where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO';
update TATTRIBUTE set name = 'Tax: Enable price tax information to show net prices' where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET';
update TATTRIBUTE set name = 'Tax: Enable price tax information to show tax amount' where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT';
update TATTRIBUTE set name = 'Tax: Display price tax information for customer types only' where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_TYPES';
update TATTRIBUTE set name = 'Customer: Allow changing price tax information view for customer types' where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE';
update TATTRIBUTE set name = 'Shop: primary shop admin email' where CODE = 'SHOP_ADMIN_EMAIL';
update TATTRIBUTE set name = 'Checkout: enable guest checkout' where CODE = 'SHOP_CHECKOUT_ENABLE_GUEST';
update TATTRIBUTE set name = 'Customer: supported customer types' where CODE = 'SHOP_CUSTOMER_TYPES';
update TATTRIBUTE set name = 'Customer: session expiry in seconds' where CODE = 'CART_SESSION_EXPIRY_SECONDS';
update TATTRIBUTE set name = 'Checkout: enable quantity picker for products' where CODE = 'CART_ADD_ENABLE_QTY_PICKER';

--
-- YC-755 Add new Property av type
--

INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1012, 'java.util.String', 'Properties', 'Properties');

--
-- YC-000 YCE features in overview
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5000,  'SHOP_SEARCH_ENABLE_COMPOUND', 'SHOP_SEARCH_ENABLE_COMPOUND',  0,  NULL,  'Search: compound search enable',
  'Enable compound search. If set to true preserved previously searched phrases until they are explicitly removed',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5010,  'SHOP_SEARCH_ENABLE_SUGGEST', 'SHOP_SEARCH_ENABLE_SUGGEST',  0,  NULL,  'Search: search suggest enable',
  'Enable search suggest. If set to true will perform "as you type" product look up under search box',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5011,  'SHOP_SEARCH_SUGGEST_MIN_CHARS', 'SHOP_SEARCH_SUGGEST_MIN_CHARS',  0,  NULL,  'Search: search suggest min chars',
  'Minimum number of characters in search box that trigger search suggest (default is 3)',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5012,  'SHOP_SEARCH_SUGGEST_MAX_ITEMS', 'SHOP_SEARCH_SUGGEST_MAX_ITEMS',  0,  NULL,  'Search: search suggest max items',
  'Maximum number of suggested products (default is 10)',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5013,  'SHOP_RFQ_CUSTOMER_TYPES', 'SHOP_RFQ_CUSTOMER_TYPES',  0,  NULL,  'Customer: types that eligible to send RFQ',
  'CSV of customer types eligible for request for quote
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8007,  'SHOP_SF_PAGE_TRACE', 'SHOP_SF_PAGE_TRACE',  0,  NULL,  'Maintenance: enable page render trace',
  'If this is enabled html rendered will contain information about how this page was constructed (CMS includes, resources and cache info)',  1008, 1001, 0, 0, 0, 0);

--
-- YC-759 Add descriptor to export tax information
--

INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2010, 'Export Tax Configurations', 'EXPORT', 'taxconfigs.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2010, 'taxconfigs.xml', 'WEBINF_XML', 'taxconfigs.xml');


--
-- YC-761 Increase number of custom fields on Address to 10
--

update TATTRIBUTE set DESCRIPTION = 'Placeholders:
{{salutation}} {{firstname}} {{middlename}} {{lastname}}
{{addrline1}} {{addrline2}} {{postcode}} {{city}} {{countrycode}} {{statecode}}
{{phone1}} {{phone2}} {{mobile1}} {{mobile2}} {{email1}} {{email2}}
{{custom0}} {{custom1}} {{custom2}} {{custom3}} {{custom4}}
{{custom5}} {{custom6}} {{custom7}} {{custom8}} {{custom9}}
For country/type/language specific formatting add attributes with suffixes _[code], _[type] or _[lang]' where GUID = 'SHOP_ADDRESS_FORMATTER';

update  TATTRIBUTE set DESCRIPTION = 'List of address form attributes separated by comma.
Available fields:
salutation, firstname, middlename, lastname
addrline1, addrline2, postcode, city, countrycode, statecode
phone1, phone2, mobile1, mobile2
email1, email2
custom0, custom1, custom2, custom3, custom4
custom5, custom6, custom7, custom8, custom9' where GUID = 'default_addressform';

alter table TADDRESS add column CUSTOM0 varchar(255);
alter table TADDRESS add column CUSTOM5 varchar(255);
alter table TADDRESS add column CUSTOM6 varchar(255);
alter table TADDRESS add column CUSTOM7 varchar(255);
alter table TADDRESS add column CUSTOM8 varchar(255);
alter table TADDRESS add column CUSTOM9 varchar(255);

--
-- YC-716 B2B customer accounts
--

alter table TCUSTOMERORDERDELIVERY add column EXPORT_BLOCK bit not null default 0;
-- alter table TCUSTOMERORDERDELIVERY add column EXPORT_BLOCK smallint not null default 0;
alter table TCUSTOMERORDERDELIVERY add column EXPORT_LAST_DATE datetime;
-- alter table TCUSTOMERORDERDELIVERY add column EXPORT_LAST_DATE timestamp;
alter table TCUSTOMERORDERDELIVERY add column EXPORT_LAST_STATUS varchar(255);
alter table TCUSTOMERORDERDELIVERY add column EXPORT_LAST_DELIVERYSTATUS varchar(64);



alter table TCUSTOMERORDER add column EXPORT_BLOCK bit not null default 0;
-- alter table TCUSTOMERORDER add column EXPORT_BLOCK smallint not null default 0;
alter table TCUSTOMERORDER add column EXPORT_LAST_DATE datetime;
-- alter table TCUSTOMERORDER add column EXPORT_LAST_DATE timestamp;
alter table TCUSTOMERORDER add column EXPORT_LAST_STATUS varchar(255);
alter table TCUSTOMERORDER add column EXPORT_LAST_ORDERSTATUS varchar(64);

alter table TCUSTOMERORDER add column B2B_REF varchar(64);
alter table TCUSTOMERORDER add column B2B_EMPLOYEEID varchar(64);
alter table TCUSTOMERORDER add column B2B_CHARGEID varchar(64);
alter table TCUSTOMERORDER add column B2B_APPROVE_REQUIRE bit not null default 0;
-- alter table TCUSTOMERORDER add column B2B_APPROVE_REQUIRE smallint not null default 0;
alter table TCUSTOMERORDER add column B2B_APPROVEDBY varchar(64);
alter table TCUSTOMERORDER add column B2B_APPROVED_DATE datetime;
-- alter table TCUSTOMERORDER add column B2B_APPROVED_DATE timestamp;


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5014,  'SHOP_SF_REQORDERAPPROVE_TYPES', 'SHOP_SF_REQUIRE_ORDER_APPROVE_TYPES',  0,  NULL,  'Customer: types that require order approval',
  'CSV of customer types that require order approval (e.g. B2B setup with supervisor approval)
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5015,  'SHOP_SF_CANNOTCHECKOUT_TYPES', 'SHOP_SF_CANNOT_PLACE_ORDER_TYPES',  0,  NULL,  'Customer: types that cannot place orders',
  'CSV of customer types that cannot place orders (e.g. B2B setup with browse catalog only)
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11064,  'CUSTOMER_B2B_REF', 'CUSTOMER_B2B_REF',  1,  NULL,  'B2B Ref',  'Default customer reference on placed orders', 1000,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11065,  'CUSTOMER_B2B_EMPID', 'CUSTOMER_B2B_EMPLOYEE_ID',  1,  NULL,  'B2B Employee ID',  'B2B Employee ID, used when placing order', 1000,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11066,  'CUSTOMER_B2B_CHRGID', 'CUSTOMER_B2B_CHARGE_ID',  1,  NULL,  'B2B Charge to ID',  'B2B Charge to ID, used when placing order', 1000,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11067,  'CUSTOMER_B2B_REQAPPR', 'CUSTOMER_B2B_REQUIRE_APPROVE',  1,  NULL,  'B2B Require order approval',  'If set to true orders of this customer will need to be approved by supervisor', 1008,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11068,  'CUSTOMER_BLOCK_CHKOUT', 'CUSTOMER_BLOCK_CHECKOUT',  1,  NULL,  'Block checkout',  'If set to true customer cannot proceed with checkout', 1008,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11150,  'CUSTOMER_B2B_REQAPPR_X', 'CUSTOMER_B2B_REQUIRE_APPROVE_X',  1,  NULL,  'B2B Require order approval over X',  'Order amount that will trigger approval flag on this order. e.g. 1000', 1006,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11151,  'CUSTOMER_BLOCK_CHKOUT_X', 'CUSTOMER_BLOCK_CHECKOUT_X',  1,  NULL,  'Block checkout over X',  'Order amount that blocks checkou. e.g. 10000t', 1006,  1006, 0, 0, 0, 0);

alter table TSHOP add column MASTER_ID bigint;

alter table TSHOP add index FK_SH_MASTER (MASTER_ID), add constraint FK_SH_MASTER foreign key (MASTER_ID) references TSHOP (SHOP_ID);
-- alter table TSHOP add constraint FK_SH_MASTER foreign key (MASTER_ID) references TSHOP;


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10883,  'SHOP_B2B_ADDRESSBOOK', 'SHOP_B2B_ADDRESSBOOK',  0,  NULL,  'Shop: B2B addressbook mode enable',
    'Disables customer addressbook access, all customers can only use B2B shop addressbook',  1008, 1001, 0, 0, 0, 0);

--
-- YC-000 Performance optimisations and runtime improvements
--


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11206,  'JOB_EXPIRE_GUESTS_PAUSE', 'JOB_EXPIRE_GUESTS_PAUSE',  0,  NULL,  'Job\\Expired Guest Accounts Clean Up: pause clean up',
    'Pause local file system import listener',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11207,  'JOB_CUSTOMER_TAG_PAUSE', 'JOB_CUSTOMER_TAG_PAUSE',  0,  NULL,  'Job\\Customer Tagging: pause tagging',
    'Pause customer tagging (batch processing of tagging promotions)',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11208,  'JOB_ABANDONED_CARTS_PAUSE', 'JOB_ABANDONED_CARTS_PAUSE',  0,  NULL,  'Job\\Abandoned Shopping Cart State Clean Up: pause clean up',
    'Pause abandoned cart clean up (batch removal of old carts)',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11209,  'JOB_EMPTY_CARTS_PAUSE', 'JOB_EMPTY_CARTS_PAUSE',  0,  NULL,  'Job\\Empty Anonymous Shopping Cart State Clean Up: pause clean up',
    'Pause empty cart clean up (batch removal of empty carts)',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11210,  'JOB_PRODINVUP_PAUSE', 'JOB_PRODINVUP_PAUSE',  0,  NULL,  'Job\\Inventory Change Detection: pause reindex',
    'Pause product change detection job (stops re-indexing of changed inventory)',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11211,  'JOB_GLOBALREINDEX_PAUSE', 'JOB_GLOBALREINDEX_PAUSE',  0,  NULL,  'Job\\Reindex All Products: pause reindex',
    'Pause full products reindex job',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11212,  'JOB_EXPIREREINDEX_PAUSE', 'JOB_EXPIREREINDEX_PAUSE',  0,  NULL,  'Job\\Reindex Discontinued Products: pause reindex',
    'Pause discontinued products reindex job',  1008, 1000, 0, 0, 0, 0);

