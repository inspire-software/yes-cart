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
-- YC-948 Customer specific payment methods
--

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10151, 'testPaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10251, 'courierPaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10351, 'prePaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10451, 'inStorePaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10551, 'invoicePaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (10651, 'authInvoicePaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');



INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (11154, 'authorizeNetAimPaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (11273, 'authorizeNetSimPaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');



INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12163, 'cyberSourcePaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');




INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (13157, 'liqPayPaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (13257, 'liqPayNoRefundPaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14257, 'payPalProPaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14361, 'payPalExpressPaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14562, 'payPalButtonPaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');



INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15179, 'postFinancePaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15279, 'postFinanceManualPaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');

--
-- YC-953 Rework query API in Admin, so that it is pluggable
--

UPDATE TATTRIBUTE SET GUID='SYSTEM_CONN_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_TIMEOUT_MS'  WHERE GUID='SYSTEM_BACKDOOR_TIMEOUT_MS';
UPDATE TATTRIBUTE SET GUID='SYSTEM_CONN_PRODB_IDX_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_PRODB_IDX_TIMEOUT_MS'  WHERE GUID='SYSTEM_BACKDOOR_PRODB_IDX_TIMEOUT_MS';
UPDATE TATTRIBUTE SET GUID='SYSTEM_CONN_PRODS_IDX_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_PRODS_IDX_TIMEOUT_MS'  WHERE GUID='SYSTEM_BACKDOOR_PRODS_IDX_TIMEOUT_MS';
UPDATE TATTRIBUTE SET GUID='SYSTEM_CONN_QUERY_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_QUERY_TIMEOUT_MS'  WHERE GUID='SYSTEM_BACKDOOR_SQL_TIMEOUT_MS';
UPDATE TATTRIBUTE SET GUID='SYSTEM_CONN_CACHE_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_CACHE_TIMEOUT_MS'  WHERE GUID='SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS';
UPDATE TATTRIBUTE SET GUID='SYSTEM_CONN_IMAGE_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_IMAGE_TIMEOUT_MS'  WHERE GUID='SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS';

UPDATE TSYSTEMATTRVALUE SET GUID='YC_SYSTEM_CONN_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_TIMEOUT_MS'  WHERE GUID='YC_SYSTEM_BACKDOOR_TIMEOUT_MS';
UPDATE TSYSTEMATTRVALUE SET GUID='YC_SYSTEM_CONN_PRODB_IDX_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_PRODB_IDX_TIMEOUT_MS'  WHERE GUID='YC_SYSTEM_BDOOR_PRODB_IDX_TIMEOUT_MS';
UPDATE TSYSTEMATTRVALUE SET GUID='YC_SYSTEM_CONN_PRODS_IDX_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_PRODS_IDX_TIMEOUT_MS'  WHERE GUID='YC_SYSTEM_BDOOR_PRODS_IDX_TIMEOUT_MS';
UPDATE TSYSTEMATTRVALUE SET GUID='YC_SYSTEM_CONN_QUERY_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_QUERY_TIMEOUT_MS'  WHERE GUID='YC_SYSTEM_BACKDOOR_SQL_TIMEOUT_MS';
UPDATE TSYSTEMATTRVALUE SET GUID='YC_SYSTEM_CONN_CACHE_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_CACHE_TIMEOUT_MS'  WHERE GUID='YC_SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS';
UPDATE TSYSTEMATTRVALUE SET GUID='YC_SYSTEM_CONN_IMAGE_TIMEOUT_MS', CODE='SYSTEM_CONNECTOR_IMAGE_TIMEOUT_MS'  WHERE GUID='YC_SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS';

--
-- YC-859 Split category and content domain models
--


    create table TCONTENT (
        CONTENT_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        PARENT_ID bigint,
        RANK integer,
        NAME varchar(255) not null,
        DISPLAYNAME longtext,
        DESCRIPTION longtext,
        UITEMPLATE varchar(255),
        DISABLED bit default 0,
        AVAILABLEFROM datetime,
        AVAILABLETO datetime,
        URI varchar(255) unique,
        TITLE varchar(255),
        METAKEYWORDS varchar(255),
        METADESCRIPTION varchar(255),
        DISPLAY_TITLE longtext,
        DISPLAY_METAKEYWORDS longtext,
        DISPLAY_METADESCRIPTION longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CONTENT_ID)
    );

    create table TCONTENTATTRVALUE (
        ATTRVALUE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        VAL longtext,
        INDEXVAL varchar(255),
        DISPLAYVAL longtext,
        CONTENT_ID bigint not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    );

    create index CN_DISABLED on TCONTENT (DISABLED);


    alter table TCONTENTATTRVALUE
        add index FK_AV_CONTENT_CONTENTID (CONTENT_ID),
        add constraint FK_AV_CONTENT_CONTENTID
        foreign key (CONTENT_ID)
        references TCONTENT (CONTENT_ID)
        on delete cascade;

    create index AV_CONTENT_CODE on TCONTENTATTRVALUE (CODE);
    create index AV_CONTENT_VAL on TCONTENTATTRVALUE (INDEXVAL);


--     create table TCONTENT (
--         CONTENT_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
--         VERSION bigint not null DEFAULT 0,
--         PARENT_ID bigint,
--         RANK integer,
--         NAME varchar(255) not null,
--         DISPLAYNAME varchar(4000),
--         DESCRIPTION varchar(4000),
--         UITEMPLATE varchar(255),
--         DISABLED smallint DEFAULT 0,
--         AVAILABLEFROM timestamp,
--         AVAILABLETO timestamp,
--         URI varchar(255) unique,
--         TITLE varchar(255),
--         METAKEYWORDS varchar(255),
--         METADESCRIPTION varchar(255),
--         DISPLAY_TITLE varchar(4000),
--         DISPLAY_METAKEYWORDS varchar(4000),
--         DISPLAY_METADESCRIPTION varchar(4000),
--         CREATED_TIMESTAMP timestamp,
--         UPDATED_TIMESTAMP timestamp,
--         CREATED_BY varchar(64),
--         UPDATED_BY varchar(64),
--         GUID varchar(36) not null unique,
--         primary key (CONTENT_ID)
--     );
--
--     create table TCONTENTATTRVALUE (
--         ATTRVALUE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
--         VERSION bigint not null DEFAULT 0,
--         VAL varchar(4000),
--         INDEXVAL varchar(255),
--         DISPLAYVAL varchar(4000),
--         CONTENT_ID bigint not null,
--         CODE varchar(255) not null,
--         CREATED_TIMESTAMP timestamp,
--         UPDATED_TIMESTAMP timestamp,
--         CREATED_BY varchar(64),
--         UPDATED_BY varchar(64),
--         GUID varchar(36) not null unique,
--         primary key (ATTRVALUE_ID)
--     );
--
--
--     create index CN_DISABLED on TCONTENT (DISABLED);
--
--
--     alter table TCONTENTATTRVALUE
--         add constraint FK_AV_CONTENT_CONTENTID
--         foreign key (CONTENT_ID)
--         references TCONTENT
--         on delete cascade;
--
--     create index AV_CONTENT_CODE on TCONTENTATTRVALUE (CODE);
--     create index AV_CONTENT_VAL on TCONTENTATTRVALUE (INDEXVAL);
--
--


--
-- YC-545 Ability to export/import whole shop using YUM
--

INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2101, 'YC DEMO: Export Site SHOP10 (ZIP)', 'EXPORT', 'siteshop10-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2101, 'siteshop10-demo.xml', 'WEBINF_XML/XML', 'siteshop10-demo.xml');

INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1015, 'Site import', 'IMPORT', '
xml/site/attributegroups.xml,
xml/site/attributes.xml,
xml/site/etypes.xml,
xml/site/brands.xml,
xml/site/category.xml,
xml/site/countries.xml,
xml/site/countrystates.xml,
xml/site/fulfilmentcentres.xml,
xml/site/shippingproviders.xml,
xml/site/payment-callback.xml,
xml/site/payment-parameters.xml,
xml/site/payment-payments.xml,
xml/site/shops.xml,
xml/site/tax.xml,
xml/site/taxconfigs.xml,
xml/site/inventory.xml,
xml/site/pricelist.xml,
xml/site/promotions.xml,
xml/site/customers.xml,
xml/site/customerorders.xml,
xml/site/organisationusers.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1101, 'xml/site/attributegroups.xml', 'WEBINF_XML/XML', 'xml/site/attributegroups.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1102, 'xml/site/attributes.xml', 'WEBINF_XML/XML', 'xml/site/attributes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1103, 'xml/site/etypes.xml', 'WEBINF_XML/XML', 'xml/site/etypes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1104, 'xml/site/brands.xml', 'WEBINF_XML/XML', 'xml/site/brands.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1105, 'xml/site/category.xml', 'WEBINF_XML/XML', 'xml/site/category.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1106, 'xml/site/countries.xml', 'WEBINF_XML/XML', 'xml/site/countries.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1107, 'xml/site/countrystates.xml', 'WEBINF_XML/XML', 'xml/site/countrystates.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1108, 'xml/site/fulfilmentcentres.xml', 'WEBINF_XML/XML', 'xml/site/fulfilmentcentres.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1109, 'xml/site/shippingproviders.xml', 'WEBINF_XML/XML', 'xml/site/shippingproviders.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1110, 'xml/site/payment-callback.xml', 'WEBINF_XML/XML', 'xml/site/payment-callback.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1111, 'xml/site/payment-parameters.xml', 'WEBINF_XML/XML', 'xml/site/payment-parameters.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1112, 'xml/site/payment-payments.xml', 'WEBINF_XML/XML', 'xml/site/payment-payments.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1113, 'xml/site/shops.xml', 'WEBINF_XML/XML', 'xml/site/shops.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1114, 'xml/site/tax.xml', 'WEBINF_XML/XML', 'xml/site/tax.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1115, 'xml/site/taxconfigs.xml', 'WEBINF_XML/XML', 'xml/site/taxconfigs.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1116, 'xml/site/inventory.xml', 'WEBINF_XML/XML', 'xml/site/inventory.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1117, 'xml/site/pricelist.xml', 'WEBINF_XML/XML', 'xml/site/pricelist.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1118, 'xml/site/promotions.xml', 'WEBINF_XML/XML', 'xml/site/promotions.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1119, 'xml/site/customers.xml', 'WEBINF_XML/XML', 'xml/site/customers.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1120, 'xml/site/customerorders.xml', 'WEBINF_XML/XML', 'xml/site/customerorders.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1121, 'xml/site/organisationusers.xml', 'WEBINF_XML/XML', 'xml/site/organisationusers.xml');




