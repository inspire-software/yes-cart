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
-- YC-652 Introduce a separate field for customer and customer address to hold 'title'
--

alter table TMANAGER  add column SALUTATION varchar(24);
alter table TCUSTOMER add column SALUTATION varchar(24);
alter table TADDRESS  add column SALUTATION varchar(24);

update TATTRIBUTE set DESCRIPTION = 'Placeholders:
{{salutation}} {{firstname}} {{middlename}} {{lastname}}
{{addrline1}} {{addrline2}} {{postcode}} {{city}} {{countrycode}} {{statecode}}
{{phone1}} {{phone2}} {{mobile1}} {{mobile2}}
{{email1}} {{email2}}
{{custom1}} {{custom2}} {{custom3}} {{custom4}}', ETYPE_ID = 1011
where CODE = 'SHOP_ADDRESS_FORMATTER';

update TATTRIBUTE set DESCRIPTION = 'Placeholders: {{salutation}} {{firstname}} {{middlename}} {{lastname}}', ETYPE_ID = 1011 where CODE = 'SHOP_CUSTOMER_FORMATTER';

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11052,  'salutation', 'salutation',  0,  NULL,  'Salutation',  'Salutation CSV options
e.g. "en|Mr-Mr,Mrs-Mrs,Dr-Dr"', 1004,  1006);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11053,  'firstname', 'firstname',  1,  NULL,  'First name',  'First name', 1000,  1006);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11054,  'lastname', 'lastname',  1,  NULL,  'Last name',  'Last name', 1000,  1006);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  11055,  'middlename', 'middlename',  0,  NULL,  'Middle name',  'Middle name', 1000,  1006);

INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (23, 'firstname,middlename,lastname,CUSTOMER_PHONE,MARKETING_OPT_IN','SHOP_CUSTOMER_REGISTRATION_ATTRIBUTES', 10, 'SHOP_CUSTOMER_REGISTRATION_10');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (24, 'firstname,middlename,lastname,CUSTOMER_PHONE,MARKETING_OPT_IN','SHOP_CUSTOMER_PROFILE_ATTRIBUTES_VISIBLE', 10, 'SHOP_CUSTOMER_PROFILE_10');

--
-- YC-653 Allow configurable address form
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, GUID)
  VALUES (  11039,  'SHOP_CUSTOMER_ADDRESS_PREFIX',  0,  NULL,  'Customer: address form prefix attribute',
'Address form prefix attribute used to define various address forms.
Prefix will be used to select ADDRESS attributes
E.g. if this attribute is ADDR_FORM and Customer attribute value for it is "default"
then fields would be resolved as "default_firstname", "default_lastname" etc.',  1000, 1001, 'SHOP_CUSTOMER_ADDR_PREF');

INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, GUID, CODE, NAME, DESCRIPTION) VALUES (1007, 'ADDRESS', 'ADDRESS', 'Customer address settings.', '');

--
-- YC-654 Allow choosing primary shop url
--

alter table TSHOPURL add column PRIMARY_URL bit not null default 0;
-- alter table TSHOPURL add column PRIMARY_URL smallint not null DEFAULT 0;

