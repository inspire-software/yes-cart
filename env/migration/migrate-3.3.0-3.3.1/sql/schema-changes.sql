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

