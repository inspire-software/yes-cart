package org.yes.cart.payment.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.CustomerOrderPaymentService;

import java.util.Iterator;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PayPalNvpPaymentGatewayImplTest extends CappPaymentModuleDBTestCase {

    private PaymentProcessorSurrogate paymentProcessor;
    private PayPalNvpPaymentGatewayImpl payPalNvpPaymentGateway;
    private CustomerOrderPaymentService customerOrderPaymentService;

    private boolean isTestAllowed() {
        return "true".equals(System.getProperty("testPgPayPal"));
    }


    @Before
    public void setUp() throws Exception {
        if (isTestAllowed()) {
            customerOrderPaymentService = (CustomerOrderPaymentService) ctx.getBean("customerOrderPaymentService");
            payPalNvpPaymentGateway = (PayPalNvpPaymentGatewayImpl) ctx.getBean("payPalNvpPaymentGateway");
            paymentProcessor = new PaymentProcessorSurrogate(customerOrderPaymentService, payPalNvpPaymentGateway);
        }
    }

    @Test
    public void testGetPaymentGatewayParameters() {
        if (isTestAllowed()) {
            for (PaymentGatewayParameter parameter : payPalNvpPaymentGateway.getPaymentGatewayParameters()) {
                assertEquals("payPalNvpPaymentGateway", parameter.getPgLabel());
            }
        }
    }

    @Test
    public void testAuthPlusReverseAuthorization() {
        if (isTestAllowed()) {


            try {
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
            } finally {
                //dumpDataBase("testAuthPlusReverseAuthorization", new String[]{"TCUSTOMERORDERPAYMENT"});
            }


        }
    }


    @Test
    public void testAuthPlusCapture() {
        if (isTestAllowed()) {

            try {
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
            } finally {
                //dumpDataBase("testAuthPlusCapture", new String[]{"TCUSTOMERORDERPAYMENT"});
            }


        }
    }


    @Test
    public void testAuthPlusCapturePlusVoidCapture() {
        if (isTestAllowed()) {

            try {
                orderCancelationFlow(false);
            } finally {
                //dumpDataBase("void", new String[]{"TCUSTOMERORDERPAYMENT"});
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
                //dumpDataBase("refund", new String[]{"TCUSTOMERORDERPAYMENT"});
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
                        useRefund ? PaymentGateway.REFUND : PaymentGateway.VOID_CAPTURE).size());

        assertEquals(
                6,
                customerOrderPaymentService.findBy(
                        orderNum,
                        null,
                        Payment.PAYMENT_STATUS_OK,
                        null).size());
    }


    @Test
    public void testAuthCapture() {
        if (isTestAllowed()) {


            try {
                final String orderNum = UUID.randomUUID().toString();

                final CustomerOrder customerOrder = createCustomerOrder(orderNum);

                // The whole operation is completed successfully
                assertEquals
                        (Payment.PAYMENT_STATUS_OK,
                                paymentProcessor.authorizeCapture(
                                        customerOrder,
                                        createCardParameters()));

                assertEquals(
                        2,
                        customerOrderPaymentService.findBy(
                                orderNum,
                                null,
                                Payment.PAYMENT_STATUS_OK,
                                PaymentGateway.AUTH_CAPTURE).size());

            } finally {
                //dumpDataBase("testAuthCapture", new String[]{"TCUSTOMERORDERPAYMENT"});

            }


        }
    }


    public String getVisaCardNumber() {
        return "4444750601380435";  //this is from test account
    }
}
