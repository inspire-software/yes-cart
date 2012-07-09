package org.yes.cart.service.payment;

/**
 *
 * Create {@link PaymentProcessor} for client and inject partucular PaymentGateway.
 * Payment gateway specified by his label in descriptor. To get the partucular payment gateway use the
 * {@link org.yes.cart.service.payment.PaymentModulesManager}
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface PaymentProcessorFactory {


    /**
     * Create a new, thread safe, payment processor. 
     * @param paymentGatewayLabel label , that specify a payment gateway.
     * @return {@link PaymentProcessor}
     */
    PaymentProcessor create(String paymentGatewayLabel);



}
