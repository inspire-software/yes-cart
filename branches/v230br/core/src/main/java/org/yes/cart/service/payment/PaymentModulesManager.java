/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
     *
     * @return list of payment modules.
     */
    Collection<PaymentModule> getPaymentModules();

    /**
     * Get all payment gateways in specified module by given label.
     *
     * @param paymentModuleLabel given label of payment module
     *
     * @return list of payment gateways in module.
     */
    Collection<PaymentGatewayDescriptor> getPaymentGatewaysDescriptors(String paymentModuleLabel);

    /**
     * Get all payment gateways in all modules.
     *
     * @param includeDisabled false will fetch all PG, true - enabled only
     * @param shopCode shop code
     *
     * @return list of payment gateways in all modules.
     */
    List<PaymentGatewayDescriptor> getPaymentGatewaysDescriptors(boolean includeDisabled, String shopCode);

    /**
     * Get payment gateway by given pg label.
     *
     * @param paymentGatewayLabel gateway label
     * @param shopCode shop code
     *
     * @return list of payment gateways in module.
     */
    PaymentGateway getPaymentGateway(String paymentGatewayLabel, String shopCode);

    /**
     * Put available payment gateway into allowed.
     *
     * @param label   payment gateway label
     */
    void allowPaymentGateway(String label);

    /**
     * Put available payment gateway into allowed.
     *
     * @param label   payment gateway label
     * @param shopCode shop code
     */
    void allowPaymentGatewayForShop(String label, String shopCode);

    /**
     * Remove available payment gateway from allowed and put it into allowed.
     *
     * @param label   payment gateway label
     */
    void disallowPaymentGateway(String label);

    /**
     * Remove available payment gateway from allowed and put it into allowed.
     *
     * @param label   payment gateway label
     * @param shopCode shop code
     */
    void disallowPaymentGatewayForShop(String label, String shopCode);



}
