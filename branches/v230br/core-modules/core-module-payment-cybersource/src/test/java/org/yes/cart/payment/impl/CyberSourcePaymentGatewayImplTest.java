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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.CustomerOrderPaymentService;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CyberSourcePaymentGatewayImplTest extends PaymentModuleDBTestCase {

    private PaymentProcessorSurrogate paymentProcessor;
    private CyberSourcePaymentGatewayImpl cyberSourcePaymentGateway;
    private CustomerOrderPaymentService customerOrderPaymentService;

    private boolean isTestAllowed() {
        return "true".equals(System.getProperty("testPgCyberSource"));
    }

    protected String testContextName() {
        return "test-core-module-payment-cybersource.xml";
    }

    @Before
    public void setUp() throws Exception {
        assumeTrue(isTestAllowed());
        if (isTestAllowed()) {
            customerOrderPaymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
            cyberSourcePaymentGateway = (CyberSourcePaymentGatewayImpl) ctx().getBean("cyberSourcePaymentGateway");
            paymentProcessor = new PaymentProcessorSurrogate(customerOrderPaymentService, cyberSourcePaymentGateway);
        }
    }

    @Test
    public void testGetPaymentGatewayParameters() throws Exception {
        assumeTrue(isTestAllowed());
        //not sure is proxy will be used or not
        for (PaymentGatewayParameter parameter : cyberSourcePaymentGateway.getPaymentGatewayParameters()) {
            assertEquals("cyberSourcePaymentGateway", parameter.getPgLabel());
        }

        dumpDataBase("cybdata" , new String[] {"TPAYMENTGATEWAYPARAMETER"});
    }


    @Test
    public void testAuthPlusReverseAuthorization() {
        assumeTrue(isTestAllowed());
        String orderNum = UUID.randomUUID().toString();
        CustomerOrder customerOrder = createCustomerOrder(orderNum);
        // The whole operation is completed successfully
        Assert.assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.authorize(
                        customerOrder,
                        createCardParameters()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.AUTH).size());
        //lets perform reverse authorization
        paymentProcessor.reverseAuthorizations(orderNum);
        //two records for reverse
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.REVERSE_AUTH).size());
        //total 54 records
        assertEquals(4,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        null).size());
    }

    @Test
    public void testAuthPlusCapture() {
        assumeTrue(isTestAllowed());
        String orderNum = UUID.randomUUID().toString();
        CustomerOrder customerOrder = createCustomerOrder(orderNum);
        // The whole operation is completed successfully
        Assert.assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.authorize(
                        customerOrder,
                        createCardParameters()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.AUTH).size());
        //capture on first completed shipment
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        Assert.assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum()));
        assertEquals(1,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
        //capture on second completed shipment
        Assert.assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
    }

    /**
     * Test to check is pw allow capture less, than auth
     */
    @Test
    public void testAuthPlusCaptureLess() {
        assumeTrue(isTestAllowed());
        String orderNum = UUID.randomUUID().toString();
        CustomerOrder customerOrder = createCustomerOrder(orderNum);
        // The whole operation is completed successfully
        Assert.assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.authorize(
                        customerOrder,
                        createCardParameters()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.AUTH).size());
        //capture on first completed shipment
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        Assert.assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum()));
        assertEquals(1,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
        //capture on second completed shipment
        Assert.assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum(), new BigDecimal("-3.34")));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
    }

    @Test
    public void testAuthPlusCapturePlusVoidCapture() {
        assumeTrue(isTestAllowed());
        orderCancelationFlow(false);
    }

    @Test
    public void testAuthPlusCapturePlusRefund() {
        assumeTrue(isTestAllowed());
        //??? how to submit settlement
        orderCancelationFlow(true);
    }

    private void orderCancelationFlow(boolean useRefund) {
        String orderNum = UUID.randomUUID().toString();
        CustomerOrder customerOrder = createCustomerOrder(orderNum);
        // The whole operation is completed successfully
        Assert.assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.authorize(
                        customerOrder,
                        createCardParameters()));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.AUTH).size());
        //capture on first completed shipment
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum());
        assertEquals(1,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
        //capture on second completed shipment
        paymentProcessor.shipmentComplete(customerOrder, iter.next().getDeliveryNum());
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());
        //lets void capture
        Assert.assertEquals(Payment.PAYMENT_STATUS_OK,
                paymentProcessor.cancelOrder(customerOrder, useRefund));
        assertEquals(2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        useRefund ? PaymentGateway.REFUND : PaymentGateway.VOID_CAPTURE).size());
        assertEquals(6,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        null).size());
    }

    public String getVisaCardNumber() {
        return "4111111111111111";
    }
}
