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
-- YC-542 Improve customer registration/account details field configuration.
--


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10983,  'SHOP_CUSTOMER_REG_ATTRS', 'SHOP_CUSTOMER_REGISTRATION_ATTRIBUTES',  0,  NULL,  'Customer: registration form attributes (CSV)',
    'List of customer attributes separated by comma to be shown on registration form',  1004, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10982,  'SHOP_CUSTOMER_PROFILE_ATTRS_V', 'SHOP_CUSTOMER_PROFILE_ATTRIBUTES_VISIBLE',  0,  NULL,  'Customer: profile form attributes (CSV)',
    'List of customer attributes separated by comma to be shown on profile form',  1004, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10981,  'SHOP_CUSTOMER_PROFILE_ATTRS_RO', 'SHOP_CUSTOMER_PROFILE_ATTRIBUTES_READONLY',  0,  NULL,  'Customer: profile form attributes - read only (CSV)',
    'List of customer attributes separated by comma to be shown on profile form but not editable',  1004, 1001);


--
-- YC-544 Sorting options must be derived from shop configuration (same as pagination options)
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11317,  'CATEGORY_SORT_OPTIONS', 'CATEGORY_SORT_OPTIONS',  0,  NULL,  'Category: sortable fields (CSV)',
    'List of sort fields separated by comma with fail over. Default is: displayName,sku,basePrice. All supported: name,displayName,basePrice,productCode,manufacturerCode,sku,brand,availability,created',  1004, 1002);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11318,  'SHOP_CATEGORY_SORT_OPTIONS', 'SHOP_CATEGORY_SORT_OPTIONS',  0,  NULL,  'Category: sortable fields (CSV)',
    'List of sort fields separated by comma with fail over. Default is: displayName,sku,basePrice. All supported: name,displayName,basePrice,productCode,manufacturerCode,sku,brand,availability,created',  1004, 1001);

--
-- YC-502 Ensure that cached cart in resilient repository honours expiry timeout
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11103,  'CART_ABANDONED_TIMEOUT_SECONDS', 'CART_ABANDONED_TIMEOUT_SECONDS',  0,  NULL,  'Cart: abandonment in seconds',
    'Cart abandonment seconds. All abandoned carts are deleted by bulk job. Default: 2592000s (30 days)',  1006, 1000);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10980,  'CART_SESSION_EXPIRY_SECONDS', 'CART_SESSION_EXPIRY_SECONDS',  0,  NULL,  'Cart: session expiry in seconds',
    'Cart session expiry in seconds. Invalidates login when session expires. Default: 21600s (6h)',  1006, 1001);


--
-- YC-538 Add cookie policy check support (EU)
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  10979,  'SHOP_COOKIE_POLICY_ENABLE', 'SHOP_COOKIE_POLICY_ENABLE',  0,  NULL,  'Shop: Cookie policy enable',
    'Enables notification for use of cookie on this site (search the Internet for ''The EU cookie law (e-Privacy Directive)'').',  1008, 1001);

INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (118, 'true','SHOP_COOKIE_POLICY_ENABLE', 10, 'SHOP_COOKIE_POLICY_ENABLE');


INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10009, 10000, 0, 'cookie_policy_include', 'Cookie policy message include for SHOP10','include', 'SHOP10_cookie_policy_include','SHOP10_cookie_policy_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12080,'CONTENT_BODY_en_1','
<small><b>Cookie Policy</b><br>
 We may store information about you using cookies (files which are sent by us to your computer or other access device) which
 we can access when you visit our site in future. We do this to enhance user experience. If you want to delete any cookies that
 are already on your computer, please refer to the instructions for your file management software to locate the file or directory
 that stores cookies. Our cookies will have the file names JSESSIONID, yc and yccookiepolicy. Information on deleting or controlling
 cookies is available at <a href="http://www.AboutCookies.org" target="_blank">www.AboutCookies.org</a>. Please note that by
 deleting our cookies or disabling future cookies you may not be able to access certain areas or features of our site.</small>
',10009,'12080_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12081,'CONTENT_BODY_ru_1','
<small><b>Cookie Policy</b><br>
 We may store information about you using cookies (files which are sent by us to your computer or other access device) which
 we can access when you visit our site in future. We do this to enhance user experience. If you want to delete any cookies that
 are already on your computer, please refer to the instructions for your file management software to locate the file or directory
 that stores cookies. Our cookies will have the file names JSESSIONID, yc and yccookiepolicy. Information on deleting or controlling
 cookies is available at <a href="http://www.AboutCookies.org" target="_blank">www.AboutCookies.org</a>. Please note that by
 deleting our cookies or disabling future cookies you may not be able to access certain areas or features of our site.</small>
',10009,'12081_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12082,'CONTENT_BODY_uk_1','
<small><b>Cookie Policy</b><br>
 We may store information about you using cookies (files which are sent by us to your computer or other access device) which
 we can access when you visit our site in future. We do this to enhance user experience. If you want to delete any cookies that
 are already on your computer, please refer to the instructions for your file management software to locate the file or directory
 that stores cookies. Our cookies will have the file names JSESSIONID, yc and yccookiepolicy. Information on deleting or controlling
 cookies is available at <a href="http://www.AboutCookies.org" target="_blank">www.AboutCookies.org</a>. Please note that by
 deleting our cookies or disabling future cookies you may not be able to access certain areas or features of our site.</small>
',10009,'12082_CAV');

