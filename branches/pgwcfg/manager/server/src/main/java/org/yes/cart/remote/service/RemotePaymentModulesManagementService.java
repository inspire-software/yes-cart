package org.yes.cart.remote.service;

import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/22/12
 * Time: 8:57 PM
 */
public interface RemotePaymentModulesManagementService {

    /**
     * Get allowed payment gateways in all modules.
     * @param lang ui lang
     * @return list of payment gateways in module.
     */
    List<PaymentGatewayDescriptor> getAllowedPaymentGatewaysDescriptors(String lang);

    /**
     * Get available payment gateways in all modules.
     * @param lang ui lang
     * @return list of payment gateways in module.
     */
    List<PaymentGatewayDescriptor> getAvailablePaymentGatewaysDescriptors(String lang);

    /**
     * Put available payment gateway into allowed.
     *
     * @param label   payment gateway label
     */
    void allowPaymentGateway(String label);

    /**
     * Remove available payment gateway from allowed and put it into allowed.
     *
     * @param label   payment gateway label
     */
    void disallowPaymentGateway(String label);


}
