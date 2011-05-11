package org.yes.cart.payment.impl;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CyberSourcePaymentGatewayImplTest extends CappPaymentModuleDBTestCase {

    private PaymentProcessorSurrogate paymentProcessor = null;
    private CyberSourcePaymentGatewayImpl cyberSourcePaymentGateway = null;
    private CustomerOrderPaymentService customerOrderPaymentService = null;

    private boolean isTestAllowed() {
        return "true".equals(System.getProperty("testPgCyberSource"));
    }

    @Before
    public void setUp() throws Exception {
        if (isTestAllowed()) {
            super.setUp();
            customerOrderPaymentService = (CustomerOrderPaymentService) ctx.getBean("customerOrderPaymentService");
            cyberSourcePaymentGateway = (CyberSourcePaymentGatewayImpl) ctx.getBean("cyberSourcePaymentGateway");
            paymentProcessor = new PaymentProcessorSurrogate(customerOrderPaymentService, cyberSourcePaymentGateway);
        }
    }

    @After
    public void tearDown() throws Exception {
        if (isTestAllowed()) {
            paymentProcessor = null;
            cyberSourcePaymentGateway = null;
            customerOrderPaymentService = null;
            super.tearDown();

        }
    }

    @Test
    public void testGetPaymentGatewayParameters() {
        if (isTestAllowed()) {

            //not sure is proxy will be used or not
            for (PaymentGatewayParameter parameter : cyberSourcePaymentGateway.getPaymentGatewayParameters()) {
                assertEquals("cyberSourcePaymentGateway", parameter.getPgLabel());
            }
        }
    }


    @Test
    public void testAuthPlusReverseAuthorization() {
        if (isTestAllowed()) {


            final String orderNum = UUID.randomUUID().toString();


            final CustomerOrder customerOrder = createCustomerOrder(orderNum);

            // The whole operation is completed successfully
            assertEquals
                    (Payment.PAYMENT_STATUS_OK,
                            paymentProcessor.authorize(
                                    customerOrder,
                                    createCardParameters()));

            assertEquals(
                    2,
                    customerOrderPaymentService.findBy(
                            orderNum,
                            null,
                            Payment.PAYMENT_STATUS_OK,
                            PaymentGateway.AUTH).size());


            //lets perform reverse authorization
            paymentProcessor.reverseAuthorizatios(orderNum);

            //two records for reverse
            assertEquals(
                    2,
                    customerOrderPaymentService.findBy(
                            orderNum,
                            null,
                            Payment.PAYMENT_STATUS_OK,
                            PaymentGateway.REVERSE_AUTH).size());

            //total 54 records
            assertEquals(
                    4,
                    customerOrderPaymentService.findBy(
                            orderNum,
                            null,
                            Payment.PAYMENT_STATUS_OK,
                            null).size());

            dumpDataBase("testAuthPlusReverseAuthorization", new String[]{"TCUSTOMERORDERPAYMENT"});


        }
    }


    @Test
    public void testAuthPlusCapture() {
        if (isTestAllowed()) {

            final String orderNum = UUID.randomUUID().toString();


            final CustomerOrder customerOrder = createCustomerOrder(orderNum);

            // The whole operation is completed successfully
            assertEquals
                    (Payment.PAYMENT_STATUS_OK,
                            paymentProcessor.authorize(
                                    customerOrder,
                                    createCardParameters()));

            assertEquals(
                    2,
                    customerOrderPaymentService.findBy(
                            orderNum,
                            null,
                            Payment.PAYMENT_STATUS_OK,
                            PaymentGateway.AUTH).size());

            //capture on first completed shipment
            Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();

            assertEquals
                    (Payment.PAYMENT_STATUS_OK,
                            paymentProcessor.shipmentComplete(customerOrder, iter.next().getDevileryNum()));
            assertEquals(
                    1,
                    customerOrderPaymentService.findBy(
                            orderNum,
                            null,
                            Payment.PAYMENT_STATUS_OK,
                            PaymentGateway.CAPTURE).size());

            //capture on second completed shipment
            assertEquals
                    (Payment.PAYMENT_STATUS_OK,
                            paymentProcessor.shipmentComplete(customerOrder, iter.next().getDevileryNum()));
            assertEquals(
                    2,
                    customerOrderPaymentService.findBy(
                            orderNum,
                            null,
                            Payment.PAYMENT_STATUS_OK,
                            PaymentGateway.CAPTURE).size());
            dumpDataBase("testAuthPlusCapture", new String[]{"TCUSTOMERORDERPAYMENT"});


        }
    }


    @Test
    public void testAuthPlusCapturePlusVoidCapture() {
        if (isTestAllowed()) {

            try {
                orderCancelationFlow(false);
            } finally {
                dumpDataBase("void", new String[]{"TCUSTOMERORDERPAYMENT"});
            }

        }
    }

    @Test
    public void testAuthPlusCapturePlusRefund() {
        //??? how to submit settlement
        if (isTestAllowed()) {

            try {
                orderCancelationFlow(true);
            } finally {
                dumpDataBase("refund", new String[]{"TCUSTOMERORDERPAYMENT"});
            }

        }
    }

    private void orderCancelationFlow(boolean useRefund) {
        final String orderNum = UUID.randomUUID().toString();


        final CustomerOrder customerOrder = createCustomerOrder(orderNum);

        // The whole operation is completed successfully
        assertEquals
                (Payment.PAYMENT_STATUS_OK,
                        paymentProcessor.authorize(
                                customerOrder,
                                createCardParameters()));

        assertEquals(
                2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.AUTH).size());

        //capture on first completed shipment
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();

        paymentProcessor.shipmentComplete(customerOrder, iter.next().getDevileryNum());
        assertEquals(
                1,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());

        //capture on second completed shipment
        paymentProcessor.shipmentComplete(customerOrder, iter.next().getDevileryNum());
        assertEquals(
                2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        PaymentGateway.CAPTURE).size());

        //lets void capture

        assertEquals
                (Payment.PAYMENT_STATUS_OK,
                        paymentProcessor.cancelOrder(customerOrder, useRefund));
        assertEquals(
                2,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        useRefund? PaymentGateway.REFUND: PaymentGateway.VOID_CAPTURE).size());

        assertEquals(
                6,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        null).size());
    }


   /** {@inheritDoc} */
    public String getVisaCardNumber() {
        return "4111111111111111";
    }


}
