--
--  Copyright 2009 Inspire-Software.com
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
-- YC-976 Upgrades 4.x.x
--

alter table TPRODUCTTYPEATTR drop foreign key constraint FK_PTA_ATTR;
-- alter table TPRODUCTTYPEATTR drop constraint FK_PTA_ATTR;
create index PTA_ATTRIBUTE_CODE on TPRODUCTTYPEATTR (CODE);

--
-- YC-1008 Timestamps information in Admin
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV, DISPLAYNAME)
  VALUES (  11157,  'REGISTRATION_MANAGER_NAME', 'REGISTRATION_MANAGER_NAME',  0,  NULL,  'Registration Manager Name',  'Registration Manager Name', 'Locked', 'CUSTOMER', 0, 0, 0, 0,
  'en#~#Registration Manager Name#~#uk#~#Менеджер реєстрації#~#ru#~#Менеджер регистрации#~#de#~#Registrierungsmanager');

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV, DISPLAYNAME)
  VALUES (  11158,  'REGISTRATION_MANAGER_EMAIL', 'REGISTRATION_MANAGER_EMAIL',  0,  NULL,  'Registration Manager Email',  'Registration Manager Email', 'Locked', 'CUSTOMER', 0, 0, 0, 0,
  'en#~#Registration Manager Email#~#uk#~#Email Менеджера реєстрації#~#ru#~#Email Менеджера регистрации#~#de#~#E-Mail des Registrierungsmanager');

--
-- YC-1032 Make credentials mechanism more flexible
--


alter table TCUSTOMER modify column EMAIL varchar(255);
-- alter table TCUSTOMER alter EMAIL null;
alter table TCUSTOMER add column LOGIN varchar(255);
update TCUSTOMER set LOGIN = EMAIL;
update TCUSTOMER set EMAIL = null where EMAIL like '#%';
alter table TCUSTOMER add column PHONE varchar(25);
update TCUSTOMER set PHONE = (select VAL from TCUSTOMERATTRVALUE where TCUSTOMER.CUSTOMER_ID = TCUSTOMERATTRVALUE.CUSTOMER_ID and CODE = 'CUSTOMER_PHONE');
delete from TCUSTOMERATTRVALUE where CODE = 'CUSTOMER_PHONE';
alter table TCUSTOMER modify column LOGIN varchar(255) not null;
-- alter table TCUSTOMER alter column LOGIN not null;
update TCUSTOMER set EMAIL = GUEST_EMAIL where GUEST = 1;
alter table TCUSTOMER drop column GUEST_EMAIL;

alter table TMANAGER modify column EMAIL varchar(255);
-- alter table TMANAGER alter column EMAIL null;
alter table TMANAGER add column LOGIN varchar(255);
create index MANAGER_LOGIN on TMANAGER (LOGIN);
update TMANAGER set LOGIN = EMAIL;
alter table TMANAGER modify column LOGIN varchar(255) not null unique;
-- alter table TMANAGER alter column LOGIN not null unique;
alter table TMANAGER add column PHONE varchar(25);

alter table TMANAGERROLE add column LOGIN varchar(255);
update TMANAGERROLE set LOGIN = EMAIL;
alter table TMANAGERROLE modify column LOGIN varchar(255) not null;
-- alter table TMANAGERROLE alter column LOGIN not null;
create index MANAGERROLE_LOGIN on TMANAGERROLE (LOGIN);
alter table TMANAGERROLE drop column EMAIL;


alter table TCUSTOMERORDER modify column EMAIL varchar(255);
-- alter table TCUSTOMERORDER alter column EMAIL null;
alter table TCUSTOMERORDER add column PHONE varchar(25);

delete from TSHOPPINGCARTSTATE;
alter table TSHOPPINGCARTSTATE add column CUSTOMER_LOGIN varchar(255);
create index SHOPPINGCARTSTATE_LOGIN on TSHOPPINGCARTSTATE (CUSTOMER_LOGIN);
drop index SHOPPINGCARTSTATE_EMAIL;
alter table TSHOPPINGCARTSTATE drop column CUSTOMER_EMAIL;

alter table TPROMOTIONCOUPONUSAGE add column CUSTOMER_REF varchar(255);
create index PROMOTIONCOUPONUSAGE_REF on TPROMOTIONCOUPONUSAGE (CUSTOMER_REF);
update TPROMOTIONCOUPONUSAGE set CUSTOMER_REF = CUSTOMER_EMAIL;
drop index PROMOTIONCOUPONUSAGE_EMAIL;
alter table TPROMOTIONCOUPONUSAGE modify column CUSTOMER_REF varchar(255) not null;
-- alter table TPROMOTIONCOUPONUSAGE alter column CUSTOMER_REF not null;
alter table TPROMOTIONCOUPONUSAGE drop column CUSTOMER_EMAIL;

update TATTRIBUTE set VAL = 'email' where GUID = 'email';
update TATTRIBUTE set VAL = 'phone' where GUID = 'CUSTOMER_PHONE';
update TATTRIBUTE set VAL = 'salutation' where GUID = 'salutation';
update TATTRIBUTE set VAL = 'firstname' where GUID = 'firstname';
update TATTRIBUTE set VAL = 'lastname' where GUID = 'lastname';
update TATTRIBUTE set VAL = 'customertype' where GUID = 'customertype';
update TATTRIBUTE set VAL = 'pricingpolicy' where GUID = 'pricingpolicy';
update TATTRIBUTE set VAL = 'b2bsubshop' where GUID = 'b2bsubshop';
update TATTRIBUTE set VAL = 'password' where GUID = 'password';
update TATTRIBUTE set VAL = 'confirmPassword' where GUID = 'confirmPassword';

--
-- YC-1035 Improved number sorting in filters
--

alter table TPRODUCTTYPEATTR add column IS_NUMERIC bit not null default 0;
-- alter table TPRODUCTTYPEATTR add column IS_NUMERIC smallint not null default 0;


--
-- YC-1036 Align naming for config files and variables
--

update TATTRIBUTE  set DESCRIPTION = 'This URI points to preview CSS. For example:
SFW: "wicket/resource/org.yes.cart.web.page.HomePage/::/::/::/::/::/style/preview.css"
SFG: "resources/style/preview.css"' where GUID = 'SYSTEM_PREVIEW_URI_CSS';


