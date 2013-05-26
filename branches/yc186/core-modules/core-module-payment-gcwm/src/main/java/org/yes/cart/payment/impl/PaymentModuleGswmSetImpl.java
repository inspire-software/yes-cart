package org.yes.cart.payment.impl;

import org.yes.cart.payment.PaymentModule;
import org.yes.cart.payment.persistence.entity.Descriptor;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;

import java.util.Collection;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/2/12
 * Time: 3:20 PM
 */
public class PaymentModuleGswmSetImpl  implements PaymentModule {

     private Descriptor descriptor;
    private Map<String, PaymentGatewayDescriptor> gateways;

    /** {@inheritDoc} */
    public Descriptor getPaymentModuleDescriptor() {
        return descriptor;
    }


    /** {@inheritDoc} */
    public Collection<PaymentGatewayDescriptor> getPaymentGateways() {
        return gateways.values();
    }

    /** {@inheritDoc} */
    public PaymentGatewayDescriptor getPaymentGateway(final String label) {
        return gateways.get(label);
    }



    /**
     * IoC module descriptor.
     * @param descriptor     to use
     */
    public void setDescriptor(final Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     *  Get payment gateway gateways.
     * @return  payment gateway gateways.
     */
    public Map<String, PaymentGatewayDescriptor> getGateways() {
        return gateways;
    }

    /**
     * IoC payment gateways.
     * @param gateways  gateways.
     */
    public void setGateways(Map<String, PaymentGatewayDescriptor> gateways) {
        this.gateways = gateways;
    }

}
