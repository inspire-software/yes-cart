package org.yes.cart.payment.service.impl;

import org.junit.Test;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.impl.CustomerOrderPaymentEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:45:45
 */
public class CustomerOrderPaymentServiceImplTest extends BasePaymentModuleDBTestCase {

    @Test
    public void testFindBy() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx.getBean("customerOrderPaymentService");
        CustomerOrderPayment payment = getCustomerOrderPayment(new BigDecimal("123.00"), "123-45", "123-45-0");
        payment = service.create(payment);
        assertTrue(payment.getCustomerOrderPaymentId() > 0);
        assertEquals(1, service.findBy(null, null, null, null).size());
        assertEquals("5678", service.findBy(null, null, null, null).get(0).getCardNumber());
        assertEquals(1, service.findBy(null, null, Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, service.findBy(null, null, null, "AUTH").size());
        assertEquals(1, service.findBy("123-45", null, null, null).size());
        assertEquals(1, service.findBy(null, "123-45-0", null, null).size());
        assertEquals(1, service.findBy("123-45", "123-45-0", Payment.PAYMENT_STATUS_OK, "AUTH").size());

    }

    @Test
    public void testGetOrderAmount() {
        CustomerOrderPaymentService service = (CustomerOrderPaymentService) ctx.getBean("customerOrderPaymentService");
        service.create(getCustomerOrderPayment(new BigDecimal("223.00"), "223-45", "223-45-0"));
        service.create(getCustomerOrderPayment(new BigDecimal("0.45"), "223-45", "223-45-1"));
        assertEquals(new BigDecimal("223.45"), service.getOrderAmount("223-45"));
    }

    /**
     * Get {@link CustomerOrderPayment} for test.
     *
     * @return instance of {@link CustomerOrderPayment}
     */
    private CustomerOrderPayment getCustomerOrderPayment(final BigDecimal amount, final String orderNum, final String shipmentNum) {
        CustomerOrderPayment payment = new CustomerOrderPaymentEntity();

        payment.setCardExpireMonth("02");
        payment.setCardExpireYear("2020");
        payment.setCardHolderName("Bender Rodrigues");
        payment.setCardIsuueNumber("123");
        payment.setCardNumber("41111111115678");
        payment.setCardStartDate(new Date());
        payment.setOrderCurrency("EUR");
        payment.setOrderDate(new Date());
        payment.setPaymentAmount(amount);
        payment.setOrderNumber(orderNum);
        payment.setOrderShipment(shipmentNum);
        payment.setPaymentProcessorBatchSettlement(false);
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        payment.setTransactionAuthorizationCode("0987654321");
        payment.setTransactionGatewayLabel("testPg");
        payment.setTransactionOperation("AUTH");
        payment.setTransactionOperationResultCode("100");
        payment.setTransactionOperationResultMessage("Ok");
        payment.setTransactionReferenceId("123-45-0");
        payment.setTransactionRequestToken("token-0-012340-450-4253023-604536046535-60");
        return payment;
    }
}
