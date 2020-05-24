/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.payment.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.payment.impl.PaymentProcessorImpl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * This is surrogate for real processor, need to keep common processing logic during different pg tests.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentProcessorSurrogate extends PaymentProcessorImpl {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentProcessorSurrogate.class);

    private final CustomerOrderPaymentService customerOrderPaymentService;


    /**
     * Construct payment processor.
     *
     * @param customerOrderPaymentService generic service to use.
     */
    public PaymentProcessorSurrogate(final CustomerOrderPaymentService customerOrderPaymentService) {
        super(customerOrderPaymentService);
        this.customerOrderPaymentService = customerOrderPaymentService;
    }

    /**
     * Construct payment processor.
     *
     * @param customerOrderPaymentService generic service to use.
     */
    public PaymentProcessorSurrogate(CustomerOrderPaymentService customerOrderPaymentService,
                                     PaymentGateway paymentGateway) {
        super(customerOrderPaymentService);
        setPaymentGateway(paymentGateway);
        this.customerOrderPaymentService = customerOrderPaymentService;
    }


    /**
     * AuthCapture or immediate sale operation will be be used if payment gateway does not support normal flow authorize - delivery - capture.
     *
     * @param order                     to authorize payments.
     * @param forceSinglePayment        flag is true for authCapture operation, when payment gateway not supports
     *                                  several payments per order
     * @param forceProcessing           force processing
     * @param params                    for payment gateway to create template from.
     *
     * @return status of operation.
     */
    @Override
    public String authorizeCapture(final CustomerOrder order,
                                   boolean forceSinglePayment,
                                   boolean forceProcessing,
                                   final Map params) {

        return super.authorizeCapture(order, forceSinglePayment, forceProcessing, params);
    }




    /**
     * Reverse authorized payments. This can be when one of the payments from whole set is failed.
     * Reverse authorization will be applied to authorized payments only
     *
     * @param orderNum order with some authorized payments
     * @param forceProcessing
     */
    @Override
    public void reverseAuthorizations(final String orderNum, final boolean forceProcessing) {

        super.reverseAuthorizations(orderNum, forceProcessing);

    }

    /**
     * {@inheritDoc}
     */
    public String shipmentComplete(final CustomerOrder order,
                                   final String orderShipmentNumber,
                                   boolean forceProcessing) {
        return shipmentComplete(order, orderShipmentNumber, forceProcessing, BigDecimal.ZERO);
    }

    /**
     * Particular shipment is complete. Funds can be captured.
     * In case of multiple delivery and single payment, capture on last delivery.
     *
     * @param order               order
     * @param orderShipmentNumber internal shipment number.
     *                            Each order has at least one delivery.
     * @param addToPayment        amount to add for each payment if it not null
     * @return status of operation.
     */
    public String shipmentComplete(final CustomerOrder order,
                                   final String orderShipmentNumber,
                                   boolean forceProcessing,
                                   final BigDecimal addToPayment) {

        return shipmentComplete(order, orderShipmentNumber, forceProcessing, new HashMap() {{
            put("forceManualProcessing", false);
            put("forceManualProcessingMessage", null);
            put("forceAddToEveryPaymentAmount", addToPayment);
        }});


    }


    /**
     * {@inheritDoc}
     */
    public String cancelOrder(final CustomerOrder order,
                              final boolean forceProcessing,
                              final boolean useRefund) {

        return cancelOrder(order, forceProcessing, new HashMap() {{
            put("forceManualProcessing", false);
            put("forceManualProcessingMessage", null);
            put("forceAutoProcessingOperation", useRefund ? PaymentGateway.REFUND : PaymentGateway.VOID_CAPTURE);
        }});

    }


}
