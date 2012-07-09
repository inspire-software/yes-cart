package org.yes.cart.service.payment;


import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentModule;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;

import java.util.Collection;
import java.util.List;

/**
 *
 * Payment modules manages. Act as proxy to configured payent modules and gateways.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface PaymentModulesManager {


    /**
     * Get list of payment modules. At least one payment module available in OOTB configuration.
     * @return list of paymen modules.
     */
    Collection<PaymentModule> getPaymentModules();

    /**
     * Get all payment gateways in specified by given label module.
     * @param paymentModuleLabel given label of payment module
     * @return list of payment gateways in module.
     */
    Collection<PaymentGatewayDescriptor> getPaymentGatewaysDescriptors(String paymentModuleLabel);

    /**
     * Get all payment gateways in all modules.
     * @param allModules false will be filtered by allowed
     * @return list of payment gateways in module.
     */
    List<PaymentGatewayDescriptor> getPaymentGatewaysDescriptors(boolean allModules);

    /**
     * Get payment gateway by given pg label.
     * @param paymentGatewayLabel gateway lable
     * @return list of payment gateways in module.
     */
    PaymentGateway getPaymentGateway(String paymentGatewayLabel);


}
