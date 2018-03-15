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

package org.yes.cart.payment.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayInternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.impl.CustomerOrderPaymentEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.payment.impl.PaymentProcessorImpl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                                     PaymentGatewayInternalForm paymentGateway) {
        super(customerOrderPaymentService);
        setPaymentGateway(paymentGateway);
        this.customerOrderPaymentService = customerOrderPaymentService;
    }


    /**
     * AuthCapture or immediate sale operation will be be used if payment gateway does not support normal flow authorize - delivery - capture.
     *
     * @param order  to authorize payments.
     * @param params for payment gateway to create template from. Also if this map contains key
     *               forceSinglePayment, only one payment will be created (hack to support pay pal express).
     * @return status of operation.
     */
    public String authorizeCapture(final CustomerOrder order, final Map params) {

        return super.authorizeCapture(order, params);
    }




    /**
     * Reverse authorized payments. This can be when one of the payments from whole set is failed.
     * Reverse authorization will be applied to authorized payments only
     *
     * @param orderNum order with some authorized payments
     */
    public void reverseAuthorizations(final String orderNum) {

        super.reverseAuthorizations(orderNum);

    }

    /**
     * {@inheritDoc}
     */
    public String shipmentComplete(final CustomerOrder order, final String orderShipmentNumber) {
        return shipmentComplete(order, orderShipmentNumber, BigDecimal.ZERO);
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
    public String shipmentComplete(final CustomerOrder order, final String orderShipmentNumber, final BigDecimal addToPayment) {

        return shipmentComplete(order, orderShipmentNumber, new HashMap() {{
            put("forceManualProcessing", false);
            put("forceManualProcessingMessage", null);
            put("forceAddToEveryPaymentAmount", addToPayment);
        }});


    }


    /**
     * {@inheritDoc}
     */
    public String cancelOrder(final CustomerOrder order, final boolean useRefund) {

        return cancelOrder(order, new HashMap() {{
            put("forceManualProcessing", false);
            put("forceManualProcessingMessage", null);
            put("forceAutoProcessingOperation", useRefund ? PaymentGateway.REFUND : PaymentGateway.VOID_CAPTURE);
        }});

    }


}
