package org.yes.cart.payment;

import org.yes.cart.payment.persistence.entity.Descriptor;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * Each module has at least one payment gateway.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 10:22:53
 */
public interface PaymentModule  extends Serializable {

    /**
     * Get module descriptor.
     * @return {@link Descriptor}
     */
    Descriptor getPaymentModuleDescriptor();


    /**
     * Get payment gateways.
     * @return list of payment gateways in module.
     */
    Collection<PaymentGatewayDescriptor> getPaymentGateways();



    /**
     * Get particular payment gateway.
     * @param label gateway label
     * @return instance of {@link PaymentGatewayDescriptor}
     */
    PaymentGatewayDescriptor getPaymentGateway(String label);


}
