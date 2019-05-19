/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.bulkexport.xml.impl;

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class CustomerOrderPaymentXmlEntityHandler extends AbstractXmlEntityHandler<CustomerOrderPayment> {

    public CustomerOrderPaymentXmlEntityHandler() {
        super("customer-order-payments");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, CustomerOrderPayment> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        handleInternal(tagPayment(null, tuple.getData()), writer, entityCount);

    }


    Tag tagPayment(final Tag parent, final CustomerOrderPayment payment) {

        return tag(parent, "customer-order-payment")
                .attr("id", payment.getCustomerOrderPaymentId())
                .attr("guid", payment.getGuid())
                .attr("shop-code", payment.getShopCode())
                    .tag("card")
                          .attr("type", payment.getCardType())
                          .attr("holder-name", payment.getCardHolderName())
                          .attr("expire-year", payment.getCardExpireYear())
                          .attr("expire-month", payment.getCardExpireMonth())
                    .end()
                    .tagTime("order-date", payment.getOrderDate())
                    .tag("order-amount")
                        .attr("currency", payment.getOrderCurrency())
                        .tagNum("amount", payment.getPaymentAmount())
                        .tagNum("tax", payment.getTaxAmount())
                    .end()
                    .tagChars("order-number", payment.getOrderNumber())
                    .tagChars("order-shipment", payment.getOrderShipment())
                    .tagChars("payment-gateway", payment.getTransactionGatewayLabel())
                    .tagChars("customer-ip", payment.getShopperIpAddress())
                    .tag("transaction")
                        .tagChars("reference", payment.getTransactionReferenceId())
                        .tagChars("request-token", payment.getTransactionRequestToken())
                        .tagChars("auth-code", payment.getTransactionAuthorizationCode())
                        .tagChars("operation", payment.getTransactionOperation())
                        .tagChars("operation-result-code", payment.getTransactionOperationResultCode())
                        .tagChars("operation-result-message", payment.getTransactionOperationResultMessage())
                        .tagChars("payment-processor-result", payment.getPaymentProcessorResult())
                        .tagBool("batch-settlement", payment.isPaymentProcessorBatchSettlement())
                    .end()
                    .tagTime(payment)
                .end();

    }

}
