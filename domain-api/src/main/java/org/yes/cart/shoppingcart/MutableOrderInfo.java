/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import java.util.Map;

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
    void setCarrierSlaId(Map<String, Long> carrierSlaId);

    /**
     * Set carrier shipping SLA.
     *
     * @param supplier supplier.
     * @param carrierSlaId selected sla id.
     */
    void putCarrierSlaId(String supplier, Long carrierSlaId);

    /**
     * Set billing address different from shipping address flag.
     *
     * @param separateBillingAddress flag.
     */
    void setSeparateBillingAddress(boolean separateBillingAddress);

    /**
     * Set force billing address different from shipping address flag.
     *
     * @param separateBillingAddressEnabled flag.
     */
    void setSeparateBillingAddressEnabled(boolean separateBillingAddressEnabled);

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


    /**
     * Set if multiple delivery for order available.
     *
     * @param multipleDeliveryAvailable multiple delivery is available for order.
     */
    void setMultipleDeliveryAvailable(Map<String, Boolean> multipleDeliveryAvailable);

    /**
     * Set multiple delivery available flag.
     *
     * @param supplier supplier
     * @param multipleDeliveryAvailable available flag
     */
    void putMultipleDeliveryAvailable(String supplier, Boolean multipleDeliveryAvailable);

    /**
     * Set additional details stored on this cart.
     *
     * @param details additional details
     */
    void setDetails(Map<String, String> details);


    /**
     * Set additional details stored on this cart.
     *
     * @param key key
     * @param detail additional detail
     */
    void putDetail(String key, String detail);

    /**
     * Clean order information.
     */
    void clearInfo();

}
