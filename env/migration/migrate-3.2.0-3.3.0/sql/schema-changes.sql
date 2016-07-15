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
