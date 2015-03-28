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

package org.yes.cart.shoppingcart;

import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 05/11/2014
 * Time: 09:50
 */
public interface MutableOrderInfo extends OrderInfo, Serializable {


    /**
     * Set order message.
     *
     * @param orderMessage order message.
     */
    void setOrderMessage(String orderMessage);

    /**
     * Set carrier shipping SLA.
     *
     * @param carrierSlaId selected sla id.
     */
    void setCarrierSlaId(Long carrierSlaId);

    /**
     * Set billing address different from shipping address flag.
     *
     * @param separateBillingAddress flag.
     */
    void setSeparateBillingAddress(boolean separateBillingAddress);

    /**
     * Set delivery address.
     *
     * @param deliveryAddressId delivery address.
     */
    void setDeliveryAddressId(Long deliveryAddressId);

    /**
     * Set billing address.
     *
     * @param billingAddressId billing address.
     */
    void setBillingAddressId(Long billingAddressId);

    /**
     * Set billing address not required for this order flag.
     *
     * @param billingAddressRequired flag.
     */
    void setBillingAddressNotRequired(boolean billingAddressRequired);

    /**
     * Set delivery address not required for this order flag.
     *
     * @param deliveryAddressRequired flag.
     */
    void setDeliveryAddressNotRequired(boolean deliveryAddressRequired);

    /**
     * Set selected payment gateway.
     *
     * @param paymentGatewayLabel selected payment gateway.
     */
    void setPaymentGatewayLabel(String paymentGatewayLabel);

    /**
     * Set multiple delivery for order.
     *
     * @param multipleDelivery multiple delivery for order.
     */
    void setMultipleDelivery(boolean multipleDelivery);
}
