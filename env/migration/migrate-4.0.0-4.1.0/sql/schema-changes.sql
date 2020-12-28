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
-- YC-1042 Show more feature on category lister pages
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11130,  'SHOP_CATEGORY_PAGE_CTRL_DISABLE', 'SHOP_CATEGORY_PAGE_CTRL_DISABLE',  0,  NULL,  'Category: Hide pagination controls on category page',
   'Hide pagination buttons on the category page',  'Boolean', 'SHOP', 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11131,  'SHOP_CATEGORY_SORT_CTRL_DISABLE', 'SHOP_CATEGORY_SORT_CTRL_DISABLE',  0,  NULL,  'Category: Hide sorting controls on category page',
   'Disable sorting buttons on the category page',  'Boolean', 'SHOP', 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11132,  'SHOP_CATEGORY_PAGESIZE_CTRL_DISABLE', 'SHOP_CATEGORY_PAGESIZE_CTRL_DISABLE',  0,  NULL,  'Category: Hide page size controls on category page',
   'Disable page size buttons on the category page',  'Boolean', 'SHOP', 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE, ATTRIBUTEGROUP, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11133,  'SHOP_CATEGORY_SHOWMORE_CTRL_DISABLE', 'SHOP_CATEGORY_SHOWMORE_CTRL_DISABLE',  0,  NULL,  'Category: Hide show more controls on category page',
   'Disable show more buttons on the category page',  'Boolean', 'SHOP', 0, 0, 0, 0);


