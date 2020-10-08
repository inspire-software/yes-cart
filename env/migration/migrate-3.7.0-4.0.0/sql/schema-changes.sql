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
  VALUES (  11157,  'REGISTRATION_MANAGER_NAME', 'REGISTRATION_MANAGER_NAME',  0,  NULL,  'Registration Manager Name',  'Registration Manager Name', 'String', 'CUSTOMER', 0, 0, 0, 0,
  'en#~#Registration Manager Name#~#uk#~#Менеджер реєстрації#~#ru#~#Менеджер регистрации#~#de#~#Registrierungsmanager');

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV, DISPLAYNAME)
  VALUES (  11158,  'REGISTRATION_MANAGER_EMAIL', 'REGISTRATION_MANAGER_EMAIL',  0,  NULL,  'Registration Manager Email',  'Registration Manager Email', 'String', 'CUSTOMER', 0, 0, 0, 0,
  'en#~#Registration Manager Email#~#uk#~#Email Менеджера реєстрації#~#ru#~#Email Менеджера регистрации#~#de#~#E-Mail des Registrierungsmanager');

