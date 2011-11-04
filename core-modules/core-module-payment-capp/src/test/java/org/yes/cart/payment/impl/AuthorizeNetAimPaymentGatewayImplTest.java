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
public class AuthorizeNetAimPaymentGatewayImplTest extends CappPaymentModuleDBTestCase {

    private PaymentProcessorSurrogate paymentProcessor = null;
    private AuthorizeNetAimPaymentGatewayImpl authorizeNetAimPaymentGateway = null;
    private CustomerOrderPaymentService customerOrderPaymentService = null;

    private boolean isTestAllowed() {
        return "true".equals(System.getProperty("testPgAuthorizeNetAim"));
    }

    @Before
    public void setUp() throws Exception {
        if (isTestAllowed()) {
            customerOrderPaymentService = (CustomerOrderPaymentService) ctx.getBean("customerOrderPaymentService");
            authorizeNetAimPaymentGateway = (AuthorizeNetAimPaymentGatewayImpl) ctx.getBean("authorizeNetAimPaymentGateway");
            paymentProcessor = new PaymentProcessorSurrogate(customerOrderPaymentService, authorizeNetAimPaymentGateway);
        }
    }

    @Test
    public void testGetPaymentGatewayParameters() {
        if (isTestAllowed()) {
            for (PaymentGatewayParameter parameter : authorizeNetAimPaymentGateway.getPaymentGatewayParameters()) {
                assertEquals("authorizeNetAimPaymentGateway", parameter.getPgLabel());
            }
        }
    }


    @Test
    public void testAuthPlusReverseAuthorization() {
        if (isTestAllowed()) {
            final String orderNum = UUID.randomUUID().toString();
            final CustomerOrder customerOrder = createCustomerOrder(orderNum);
            // The whole operation is completed successfully
            assertEquals(Payment.PAYMENT_STATUS_OK,
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
            paymentProcessor.reverseAuthorizatios(orderNum);
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
    }

    @Test
    public void testAuthPlusCapture() {
        if (isTestAllowed()) {
            final String orderNum = UUID.randomUUID().toString();
            final CustomerOrder customerOrder = createCustomerOrder(orderNum);
            // The whole operation is completed successfully
            assertEquals(Payment.PAYMENT_STATUS_OK,
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
            assertEquals(Payment.PAYMENT_STATUS_OK,
                    paymentProcessor.shipmentComplete(customerOrder, iter.next().getDevileryNum()));
            assertEquals(1,
                    customerOrderPaymentService.findBy(
                            orderNum,
                            null,
                            Payment.PAYMENT_STATUS_OK,
                            PaymentGateway.CAPTURE).size());
            //capture on second completed shipment
            assertEquals(Payment.PAYMENT_STATUS_OK,
                    paymentProcessor.shipmentComplete(customerOrder, iter.next().getDevileryNum()));
            assertEquals(2,
                    customerOrderPaymentService.findBy(
                            orderNum,
                            null,
                            Payment.PAYMENT_STATUS_OK,
                            PaymentGateway.CAPTURE).size());
        }
    }


    @Test
    public void testAuthPlusCapturePlusVoidCapture() {
        if (isTestAllowed()) {
            orderCancelationFlow(false);
        }
    }

    /* public void testAuthPlusCapturePlusRefund() {
       //??? how to submit settlement
       // during this test i have got  error no 53 - The referenced transaction does not meet the criteria for issuing a credit.
       // explanation
       ///Refunds cannot be tested while the payment gateway is in Test Mode
       //If you authorize or capture a transaction, and the transaction is not yet settled by the payment gateway, you cannot issue a refund.
       if (isTestAllowed()) {
           orderCancelationFlow(true);
       }
   } */

    private void orderCancelationFlow(boolean useRefund) {
        final String orderNum = UUID.randomUUID().toString();
        final CustomerOrder customerOrder = createCustomerOrder(orderNum);
        // The whole operation is completed successfully
        assertEquals(Payment.PAYMENT_STATUS_OK,
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
        paymentProcessor.shipmentComplete(customerOrder, iter.next().getDevileryNum());
        assertEquals(1,
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
        assertEquals(Payment.PAYMENT_STATUS_OK,
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

    @Test
    public void testAuthCapture() {
        if (isTestAllowed()) {
            final String orderNum = UUID.randomUUID().toString();
            final CustomerOrder customerOrder = createCustomerOrder(orderNum);
            // The whole operation is completed successfully
            assertEquals(Payment.PAYMENT_STATUS_OK,
                    paymentProcessor.authorizeCapture(
                            customerOrder,
                            createCardParameters()));
            assertEquals(2,
                    customerOrderPaymentService.findBy(
                            orderNum,
                            null,
                            Payment.PAYMENT_STATUS_OK,
                            PaymentGateway.AUTH_CAPTURE).size());
        }
    }

    public String getVisaCardNumber() {
        return "4007000000027"; // Second Visa Test Card: 4012888818888
    }
}