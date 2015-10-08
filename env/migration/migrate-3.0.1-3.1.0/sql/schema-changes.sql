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