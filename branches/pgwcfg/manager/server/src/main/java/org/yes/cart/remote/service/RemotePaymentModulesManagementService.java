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
