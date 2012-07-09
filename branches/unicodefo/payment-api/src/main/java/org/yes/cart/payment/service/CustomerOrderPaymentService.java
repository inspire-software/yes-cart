package org.yes.cart.payment.service;


import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface CustomerOrderPaymentService extends PaymentModuleGenericService<CustomerOrderPayment> {


    /**
     * Find all payments by given parameters.
     * Warning order number or shipment number must be present.
     *
     * @param orderNumber            given order number. optional
     * @param shipmentNumber         given shipment/delivery number. optional
     * @param paymentProcessorResult status   of payment at payment processor . optional
     * @param transactionOperation   operation name at payment gateway. optional
     * @return list of payments
     */
    List<CustomerOrderPayment> findBy(
            String orderNumber,
            String shipmentNumber,
            String paymentProcessorResult,
            String transactionOperation);

    /**
     * Get order amount
     *
     * @param orderNumber given order number
     * @return order amount
     */
    BigDecimal getOrderAmount(String orderNumber);


}
