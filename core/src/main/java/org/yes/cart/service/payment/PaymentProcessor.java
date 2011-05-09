package org.yes.cart.service.payment;


import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGateway;

import java.util.Map;

/**
 *
 * Payment processor delegate payment calls to payment gateway and persists
 * information about calls. The spring scope of implementor bean must be an prototype,
 * because of underlaying payment gateways are not thread safe. 
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface PaymentProcessor {


    /**
     * Authorize a payment. The response from a card issuing bank to a
     * merchant's transaction authorization request indicating that payment information is valid
     * and funds are available on the customer's credit card. In case if authorize operation ot supported by
     * payment gateway the auth_capture operation will be use instead.
     *
     * @param order to authorize payments.
     * @param params for payment gateway to create template from
     * @return status of operation.
     */
    String authorize(CustomerOrder order, Map params);


    /**
     * Cancel order. All authorized payments will be reversed, captured funds will be void of refunded.
     * No rollback operations will be performed if order already has cancel state
     * @param order order to cancel.
     * @return status of operation.
     */
    String cancelOrder(CustomerOrder order);


    /**
     * Particular shimment is complete. Funds can be captured.
     * @param order order
     * @param orderShipmentNumber internal shipment number.
     * Each order has at least one delivery.
     * @return status of operation.
     */
    String shipmentComplete(CustomerOrder order, String orderShipmentNumber);


    /**
     * Set payment gateway to use.
     * @param paymentGateway {see PaymentGatewayInternalForm to use.
     */
    void setPaymentGateway(PaymentGateway paymentGateway);

    /**
     * Get current payment gateway to use.
     * @return {@link PaymentGateway}
     */
    PaymentGateway getPaymentGateway();

}
