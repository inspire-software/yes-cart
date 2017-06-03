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
-- YC-763 Improve customer order domain model
--

alter table TCUSTOMERORDERDELIVERY add column EXPORT_BLOCK bit not null default 0;
-- alter table TCUSTOMERORDERDELIVERY add column EXPORT_BLOCK smallint not null default 0;
alter table TCUSTOMERORDERDELIVERY add column EXPORT_LAST_DATE datetime;
-- alter table TCUSTOMERORDERDELIVERY add column EXPORT_LAST_DATE timestamp;
alter table TCUSTOMERORDERDELIVERY add column EXPORT_LAST_STATUS longtext;
-- alter table TCUSTOMERORDERDELIVERY add column EXPORT_LAST_STATUS varchar(4000);
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
  VALUES (  5016,  'SHOP_ADRDIS_CUSTOMER_TYPES', 'SHOP_ADDRESSBOOK_DISABLED_CUSTOMER_TYPES',  0,  NULL,  'Customer: types that cannot modify address book',
  'CSV of customer types that cannot modify address book (e.g. B2B setup with admin authorised addresses only)
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

--
-- YC-656 Improve order domain model by allowing copying attributes to order lines
--

alter table TCUSTOMERORDERDET add column STORED_ATTRIBUTES longtext;
alter table TCUSTOMERORDERDELIVERYDET add column STORED_ATTRIBUTES longtext;
-- alter table TCUSTOMERORDERDET add column STORED_ATTRIBUTES varchar(4000);
-- alter table TCUSTOMERORDERDELIVERYDET add column STORED_ATTRIBUTES varchar(4000);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8008,  'SHOP_PRODUCT_STORED_ATTRIBUTES', 'SHOP_PRODUCT_STORED_ATTRIBUTES',  0,  NULL,  'Product: stored attributes to copy to order',
  'Attributes that should be copied to order lines',  1004, 1001, 0, 0, 0, 0);

--
-- YC-752 Improve delivery time information for the order
--

alter table TCARRIERSLA add column MIN_DAYS integer;
alter table TCARRIERSLA add column EXCLUDE_WEEK_DAYS varchar(15);
alter table TCARRIERSLA add column EXCLUDE_DATES longtext;
-- alter table TCARRIERSLA add column EXCLUDE_DATES varchar(4000);
alter table TCARRIERSLA add column GUARANTEED bit not null default 0;
-- alter table TCARRIERSLA add column GUARANTEED smallint not null DEFAULT 0;

alter table TCUSTOMERORDER add column B2B_REMARKS varchar(255);

alter table TCUSTOMERORDERDELIVERY add column DELIVERY_REMARKS varchar(255);
alter table TCUSTOMERORDERDELIVERY add column DELIVERY_EST_MIN datetime;
-- alter table TCUSTOMERORDERDELIVERY add column DELIVERY_EST_MIN timestamp;
alter table TCUSTOMERORDERDELIVERY add column DELIVERY_EST_MAX datetime;
-- alter table TCUSTOMERORDERDELIVERY add column DELIVERY_EST_MAX timestamp;
alter table TCUSTOMERORDERDELIVERY add column DELIVERY_GUARANTEED datetime;
-- alter table TCUSTOMERORDERDELIVERY add column DELIVERY_GUARANTEED timestamp;

alter table TCUSTOMERORDERDET add column B2B_REMARKS varchar(255);
alter table TCUSTOMERORDERDET add column DELIVERY_REMARKS varchar(255);
alter table TCUSTOMERORDERDET add column DELIVERY_EST_MIN datetime;
-- alter table TCUSTOMERORDERDET add column DELIVERY_EST_MIN timestamp;
alter table TCUSTOMERORDERDET add column DELIVERY_EST_MAX datetime;
-- alter table TCUSTOMERORDERDET add column DELIVERY_EST_MAX timestamp;
alter table TCUSTOMERORDERDET add column DELIVERY_GUARANTEED datetime;
-- alter table TCUSTOMERORDERDET add column DELIVERY_GUARANTEED timestamp;

alter table TCUSTOMERORDERDELIVERYDET add column B2B_REMARKS varchar(255);
alter table TCUSTOMERORDERDELIVERYDET add column DELIVERY_REMARKS varchar(255);
alter table TCUSTOMERORDERDELIVERYDET add column DELIVERY_EST_MIN datetime;
-- alter table TCUSTOMERORDERDELIVERYDET add column DELIVERY_EST_MIN timestamp;
alter table TCUSTOMERORDERDELIVERYDET add column DELIVERY_EST_MAX datetime;
-- alter table TCUSTOMERORDERDELIVERYDET add column DELIVERY_EST_MAX timestamp;
alter table TCUSTOMERORDERDELIVERYDET add column DELIVERY_GUARANTEED datetime;
-- alter table TCUSTOMERORDERDELIVERYDET add column DELIVERY_GUARANTEED timestamp;

alter table TWAREHOUSE add column DEFAULT_STD_LEAD_TIME integer default 0;
alter table TWAREHOUSE add column DEFAULT_BO_LEAD_TIME integer default 0;

--
-- YC-675 Price service should retrieve sub shop price when B2B mode is enabled
--


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10884,  'SHOP_B2B_STRICT_PRICE', 'SHOP_B2B_STRICT_PRICE',  0,  NULL,  'Shop: B2B strict price mode enable',
    'Disables main shop look up. Only sub shop prices are considered',  1008, 1001, 0, 0, 0, 0);

--
--  YC-765 Add Invoice payment gateway
--


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10501, 'invoicePaymentGateway', 'name', 'Invoice payment', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10502, 'invoicePaymentGateway', 'name_en', 'Invoice payment', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10503, 'invoicePaymentGateway', 'name_ru', 'Счет фактура', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10504, 'invoicePaymentGateway', 'name_uk', 'Рахунок-фактура', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10505, 'invoicePaymentGateway', 'name_de', 'Rechnungen', 'Gateway-Namen (DE)', 'Gateway-Namen (DE)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10550, 'invoicePaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');



INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10601, 'authInvoicePaymentGateway', 'name', 'Authorize invoice payment', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10602, 'authInvoicePaymentGateway', 'name_en', 'Authorize invoice payment', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10603, 'authInvoicePaymentGateway', 'name_ru', 'Счет фактура с авторизацией', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10604, 'authInvoicePaymentGateway', 'name_uk', 'Рахунок фактура з авторизацією', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10605, 'authInvoicePaymentGateway', 'name_de', 'Rechnungsfreigabe', 'Gateway-Namen (DE)', 'Gateway-Namen (DE)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10650, 'authInvoicePaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');

--
-- YC-695 B2B repeat order
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8010,  'SHOP_SF_REPEAT_ORDER_T', 'SHOP_SF_REPEAT_ORDER_TYPES',  0,  NULL,  'Customer: repeat order feature enabled (CSV)',  'CSV of customer types which can repeat order
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

--
-- YC-766 Shopping Lists feature
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8011,  'SHOP_SF_SHOPLIST_T', 'SHOP_SF_SHOPPING_LIST_TYPES',  0,  NULL,  'Customer: shopping list feature enabled (CSV)',  'CSV of customer types which can create shopping lists
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

--
-- YC-769 Per line remarks in shopping cart
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8012,  'SHOP_SF_B2B_LINEREMARK_T', 'SHOP_SF_B2B_LINE_REMARKS_TYPES',  0,  NULL,  'Customer: line remarks feature enabled (CSV)',  'CSV of customer types which can leave remarks per line
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8013,  'SHOP_SF_B2B_ORDFORM_T', 'SHOP_SF_B2B_ORDER_FORM_TYPES',  0,  NULL,  'Customer: B2B form feature enabled (CSV)',  'CSV of customer types which can add B2B information on the order
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);


--
-- YC-741 PG callbacks need to be stored in callback and then a job needs to pick them up, so that we can replay them.
--

CREATE TABLE TPAYMENTGATEWAYCALLBACK (
  PAYMENTGATEWAYCALLBACK_ID bigint NOT NULL AUTO_INCREMENT,
  CREATED_BY varchar(64) DEFAULT NULL,
  CREATED_TIMESTAMP datetime DEFAULT NULL,
  UPDATED_BY varchar(64) DEFAULT NULL,
  UPDATED_TIMESTAMP datetime DEFAULT NULL,
  GUID varchar(36) DEFAULT NULL,
  SHOP_CODE varchar(255) DEFAULT NULL,
  REQUEST_DUMP longtext DEFAULT NULL,
  PG_PARAMS longtext DEFAULT NULL,
  PROCESSED bit DEFAULT 0,
  PG_LABEL varchar(64) DEFAULT NULL,
  PRIMARY KEY (PAYMENTGATEWAYCALLBACK_ID)
)  ENGINE=InnoDB;

-- CREATE TABLE TPAYMENTGATEWAYCALLBACK (
--   PAYMENTGATEWAYCALLBACK_ID bigint NOT NULL AUTO_INCREMENT,
--   CREATED_BY varchar(64) DEFAULT NULL,
--   CREATED_TIMESTAMP timestamp DEFAULT NULL,
--   UPDATED_BY varchar(64) DEFAULT NULL,
--   UPDATED_TIMESTAMP timestamp DEFAULT NULL,
--   GUID varchar(36) DEFAULT NULL,
--   SHOP_CODE varchar(255) DEFAULT NULL,
--   REQUEST_DUMP longtext DEFAULT NULL,
--   PG_PARAMS longtext DEFAULT NULL,
--   PROCESSED bit DEFAULT 0,
--   PG_LABEL varchar(64) DEFAULT NULL,
--   PRIMARY KEY (PAYMENTGATEWAYCALLBACK_ID)
-- )  ENGINE=InnoDB;

create index PAYMENTGATEWAYCALLBACK_PROCESSED on TPAYMENTGATEWAYCALLBACK (PG_LABEL);
create index PAYMENTGATEWAYCALLBACK_GUID on TPAYMENTGATEWAYCALLBACK (GUID);
create index PAYMENTGATEWAYCALLBACK_SHOP on TPAYMENTGATEWAYCALLBACK (SHOP_CODE);

--
-- YC-742 Alternative ProcessAllocationOrderEventHandlerImpl for auto imports
--

alter table TCUSTOMERORDERDELIVERY add column DELIVERY_CONFIRMED datetime;
-- alter table TCUSTOMERORDERDELIVERY add column DELIVERY_CONFIRMED timestamp;

alter table TCUSTOMERORDERDELIVERYDET add column DELIVERY_CONFIRMED datetime;
-- alter table TCUSTOMERORDERDELIVERYDET add column DELIVERY_CONFIRMED timestamp;
alter table TCUSTOMERORDERDELIVERYDET add column DELIVERED_QUANTITY decimal(19,2);
-- alter table TCUSTOMERORDERDELIVERYDET add column DELIVERED_QUANTITY numeric(19,2);
alter table TCUSTOMERORDERDELIVERYDET add column SUPPLIER_INVOICE_NO varchar(64);
alter table TCUSTOMERORDERDELIVERYDET add column SUPPLIER_INVOICE_DATE datetime;
-- alter table TCUSTOMERORDERDELIVERYDET add column SUPPLIER_INVOICE_DATE timestamp;

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11213,  'JOB_DELWAITINV_PAUSE', 'JOB_DELIVERY_WAIT_INVENTORY_PAUSE',  0,  NULL,  'Job\\Inventory Awaiting Delivery Processing: pause',
    'Pause inventory awaiting delivery processing job',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11214,  'JOB_DELINFOUPDT_PAUSE', 'JOB_DELIVERY_INFO_UPDATE_PAUSE',  0,  NULL,  'Job\\Order Delivery Information Update Processing: pause',
    'Pause order delivery information update processing job',  1008, 1000, 0, 0, 0, 0);

--
-- YC-772 Integrate order auto export listener into order state machine
--

alter table TCUSTOMERORDER add column ELIGIBLE_FOR_EXPORT varchar(20);
create index CUSTOMERORDER_ELIGEXP on TCUSTOMERORDER (ELIGIBLE_FOR_EXPORT);

alter table TCUSTOMERORDERDELIVERY add column ELIGIBLE_FOR_EXPORT varchar(20);
create index CUSTOMERORDERDELIVERY_ELIGEXP on TCUSTOMERORDERDELIVERY (ELIGIBLE_FOR_EXPORT);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11215,  'JOB_ORDERAUTOEXP_PAUSE', 'JOB_ORDER_AUTO_EXPORT_PAUSE',  0,  NULL,  'Job\\Order Auto Export Processing: pause',
    'Pause order auto export processing job',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8009,  'ORDER_EXP_MAIL_SUP', 'ORDER_EXPORTER_MAIL_SUPPORTED_SUPPLIERS',  0,  NULL,  'Export Orders\\Auto mail notification',
    'Property mapping for supplier codes and corresponding emails (CSV). This is email notification export to suppliers upon successful/authorised payment of the order.
E.g. MAIN.INITPAID=sales@warehouse.com,admin@warehouse.com
SECOND.INITPAID=sales@wahouse2.com',  1012, 1001, 0, 0, 0, 0);

--
--  YC-774 External reference for carrier SLA to use in exports
--

alter table TCARRIERSLA add column EXTERNAL_REF varchar(40);

--
-- YC-775 Shop alias codes to aid integrations
--

    create table TSHOPALIAS (
        STOREALIAS_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        SHOP_ALIAS varchar(255) not null unique,
        SHOP_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (STOREALIAS_ID)
    ) ;

    alter table TSHOPALIAS
        add index FK_SHOPALIAS_SHOP (SHOP_ID),
        add constraint FK_SHOPALIAS_SHOP
        foreign key (SHOP_ID)
        references TSHOP (SHOP_ID);

--     create  table TSHOPALIAS (
--         STOREALIAS_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
--         VERSION bigint not null DEFAULT 0,
--         SHOP_ALIAS varchar(255) not null unique,
--         SHOP_ID bigint not null,
--         CREATED_TIMESTAMP timestamp,
--         UPDATED_TIMESTAMP timestamp,
--         CREATED_BY varchar(64),
--         UPDATED_BY varchar(64),
--         GUID varchar(36) not null unique,
--         primary key (STOREALIAS_ID)
--     );
--
--     alter table TSHOPALIAS
--         add constraint FK_SHOPALIAS_SHOP
--         foreign key (SHOP_ID)
--         references TSHOP;

--
-- YC-777 Product must have manufacturerPartCode, supplierCode and supplierCatalogCode
--

alter table TPRODUCT add column MANUFACTURER_PART_CODE varchar(255);
alter table TPRODUCT add column SUPPLIER_CODE varchar(255);
alter table TPRODUCT add column SUPPLIER_CATALOG_CODE varchar(255);

alter table TSKU add column MANUFACTURER_PART_CODE varchar(255);
alter table TSKU add column SUPPLIER_CODE varchar(255);
alter table TSKU add column SUPPLIER_CATALOG_CODE varchar(255);

create index PRODUCT_MPCODE on TPRODUCT (MANUFACTURER_PART_CODE);
create index PRODUCT_SCODE on TPRODUCT (SUPPLIER_CODE);
create index PRODUCT_SCCODE on TPRODUCT (SUPPLIER_CATALOG_CODE);
create index SKU_MPCODE on TSKU (MANUFACTURER_PART_CODE);
create index SKU_SCODE on TSKU (SUPPLIER_CODE);
create index SKU_SCCODE on TSKU (SUPPLIER_CATALOG_CODE);

--
-- YC-778 CustomerOrder should have getAllValues()
--

alter table TCUSTOMERORDER add column STORED_ATTRIBUTES longtext;
-- alter table TCUSTOMERORDER add column STORED_ATTRIBUTES varchar(4000);

--
-- YC-781 Allow customer to specify requested delivery date
--

alter table TCUSTOMERORDER add column REQUESTED_DELIVERY_DATE datetime;
-- alter table TCUSTOMERORDER add column REQUESTED_DELIVERY_DATE timestamp;
alter table TCUSTOMERORDERDELIVERY add column REQUESTED_DELIVERY_DATE datetime;
-- alter table TCUSTOMERORDERDELIVERY add column REQUESTED_DELIVERY_DATE timestamp;
alter table TCARRIERSLA add column EXCLUDED_CT varchar(255);
alter table TCARRIERSLA add column NAMEDDAY bit not null default 0;
-- alter table TCARRIERSLA add column NAMEDDAY smallint not null DEFAULT 0;

--
-- YC-782 Tax options per customer type
--

delete from TSHOPATTRVALUE where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_TYPES';
delete from TATTRIBUTE where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_TYPES';

delete from TSHOPATTRVALUE where CODE in ('SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT') and VAL = 'false';
update TSHOPATTRVALUE set VAL = 'B2G,B2C,B2B,B2E' where CODE in ('SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT') and VAL = 'true';

update TATTRIBUTE set name = 'Tax: Enable price tax information for customer types (CSV)', ETYPE_ID = 1004 where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO';
update TATTRIBUTE set name = 'Tax: Show net prices for customer types (CSV)', ETYPE_ID = 1004 where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET';
update TATTRIBUTE set name = 'Tax: Show tax amount for customer types (CSV)', ETYPE_ID = 1004 where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT';

--
-- YC-787 Fulfilment centres allow/block part shipment
--

alter table TWAREHOUSE add column MULTI_SHIP_SUPPORTED bit not null default 0;
-- alter table TWAREHOUSE add column MULTI_SHIP_SUPPORTED smallint not null DEFAULT 0;


--
-- YC-712 File attachments attributes
--

INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1013, 'java.lang.String', 'File', 'File');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1014, 'java.lang.String', 'SystemFile', 'SystemFile');

update TATTRIBUTE set  DESCRIPTION = 'Root directory for image repository.
Default: context://../imagevault
Recommended: file:///home/yc/server/share/imagevault' where CODE = 'SYSTEM_IMAGE_VAULT';

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11120,  'SYSTEM_FILE_VAULT', 'SYSTEM_FILE_VAULT',  1,  NULL,  'System\\File Root directory for image repository',
  'Root directory for file repository.
Default: context://../filevault
Recommended: file:///home/yc/server/share/filevault', 1001,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11121,  'SYSTEM_SYSFILE_VAULT', 'SYSTEM_SYSFILE_VAULT',  1,  NULL,  'System\\System File Root directory for image repository',
  'Root directory for secure file repository (no storefont access)
Default: context://../sysfilevault
Recommended: file:///home/yc/server/share/sysfilevault', 1001,  1000, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11306,  'SHOP_FILE0',  'SHOP_FILE0',  1,  NULL,  'Shop: Shop file',  null,  1013, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11307,  'SHOP_SYSFILE0',  'SHOP_SYSFILE0',  1,  NULL,  'Shop: Shop system file',  null,  1014, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11309,  'BRAND_FILE0', 'BRAND_FILE0',  1,  NULL,  'Brand file',  null,  1013, 1005, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11320,  'CATEGORY_FILE0', 'CATEGORY_FILE0',  0,  NULL,  'Category file',   'Category file',  1003, 1002, 0, 0, 0, 0);

INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1024,'context://../filevault','SYSTEM_FILE_VAULT',100, 'YC_SYSTEM_FILE_VAULT');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1025,'context://../sysfilevault','SYSTEM_SYSFILE_VAULT',100, 'YC_SYSTEM_SYSFILE_VAULT');

--
-- YC-000 YC help section config
--


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11029,  'SYSTEM_PANEL_HELP_DOCS', 'SYSTEM_PANEL_HELP_DOCS',  1,  NULL,  'Admin\\Help doc link',
  'Help doc link in Admin help section', 1002,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11030,  'SYSTEM_PANEL_HELP_COPYRIGHT', 'SYSTEM_PANEL_HELP_COPYRIGHT',  1,  NULL,  'Admin\\Copyright note',
  'Copyright note in Admin help section', 1011,  1000, 0, 0, 0, 0);

--
-- YC-789 Ability to configure "separate shipping" checkbox
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10963,  'SHOP_DELIVERY_ONE_ADDRESS_DISABLE', 'SHOP_DELIVERY_ONE_ADDRESS_DISABLE',  0,  NULL,  'Customer: Disable same address for billing',
  'Disable use of same address feature for customer types. Blank value is treated as enabled. To reference guests use B2G, blank types are treated as B2C.',  1004, 1001, 0, 0, 0, 0);

--
-- YC-790	B2B anonymous browsing without prices
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10962,  'SHOP_PRODUCT_HIDE_PRICES', 'SHOP_PRODUCT_HIDE_PRICES',  0,  NULL,  'Customer: hide prices',
  'Hide prices for customer types. Blank value is treated as show prices. To reference guests use B2G, blank types are treated as B2C.
This setting must be used together with Block checkout feature to prevent going through checkout for customer types that cannot see prices',  1004, 1001, 0, 0, 0, 0);


--
-- YC-792 JAM should display environment label
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11031,  'SYSTEM_PANEL_LABEL', 'SYSTEM_PANEL_LABEL',  1,  NULL,  'Admin\\System label',
  'Label on the side panel to warn of the type of environment you are on', 1011,  1000, 0, 0, 0, 0);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1027,'<span class="label label-success">DEVELOPMENT ENVIRONMENT</span>','SYSTEM_PANEL_LABEL',100, 'YC_SYSTEM_PANEL_LABEL');

