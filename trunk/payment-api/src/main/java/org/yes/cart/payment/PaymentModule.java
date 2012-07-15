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
