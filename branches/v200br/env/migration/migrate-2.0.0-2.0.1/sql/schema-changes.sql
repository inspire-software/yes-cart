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
-- YC-379 Create and add wish list Items. Wish list items modifications to support price, tags and quantity
--

alter table TCUSTOMERWISHLIST add column TAG varchar(255);
alter table TCUSTOMERWISHLIST add column QTY decimal(19,2) not null default 1;
alter table TCUSTOMERWISHLIST add column REGULAR_PRICE_WHEN_ADDED decimal(19,2) not null default 0;
alter table TCUSTOMERWISHLIST add column VISIBILITY varchar(1) default 'P';
-- Deby uses numberic
-- alter table TCUSTOMERWISHLIST add column TAG varchar(255);
-- alter table TCUSTOMERWISHLIST add column QTY numeric(19,2) not null default 1;
-- alter table TCUSTOMERWISHLIST add column REGULAR_PRICE_WHEN_ADDED numeric(19,2) not null default 0;
-- alter table TCUSTOMERWISHLIST add column VISIBILITY varchar(1) default 'P';
