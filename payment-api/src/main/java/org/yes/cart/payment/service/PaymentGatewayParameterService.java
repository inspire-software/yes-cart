package org.yes.cart.payment.service;


import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface PaymentGatewayParameterService extends PaymentModuleGenericService<PaymentGatewayParameter> {


    /**
     * Delete parameter
     *
     * @param parameterLabel      label of parameter to delete.
     * @param paymentGatewayLabel label of payment gateway.
     */
    void deleteByLabel(String paymentGatewayLabel, String parameterLabel);

    /**
     * Get payment gateway parametrs.
     *
     * @param label payment gaeway label
     * @return all PG parameters
     */
    Collection<PaymentGatewayParameter> findAll(String label);
}
