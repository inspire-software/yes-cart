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
