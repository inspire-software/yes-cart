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
 * Hold specific order information.
 * Just remove from shopping cart to keep it simple as possible.
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 19-May-2011
 * Time: 17:27:32
 */
public interface OrderInfo extends Serializable {

    /**
     * Get order message.
     *
     * @return order message
     */
    String getOrderMessage();

    /**
     * Get carrier shipping SLA.
     *
     * @return carries sla id.
     */
    Map<String, Long> getCarrierSlaId();

    /**
     * Is billing address different from shipping address.
     *
     * @return true is billing and shipping address are different.
     */
    boolean isSeparateBillingAddress();

    /**
     * Get delivery address.
     *
     * @return delivery address.
     */
    Long getDeliveryAddressId();

    /**
     * Get billing address.
     *
     * @return billing address.
     */
    Long getBillingAddressId();

    /**
     * Is billing address not required for this order.
     *
     * @return true is  address not required for this order.
     */
    boolean isBillingAddressNotRequired();

    /**
     * Is delivery address not required for this order.
     *
     * @return true is  address not required for this order.
     */
    boolean isDeliveryAddressNotRequired();


    /**
     * Get selected payment gateway.
     *
     * @return selected payment gateway
     */
    String getPaymentGatewayLabel();

    /**
     * Does order need multiple delivery.
     *
     * @return true if multiple delivery has been selected.
     */
    boolean isMultipleDelivery();

    /**
     * Can order be split into multiple deliveries (some items dispatched sooner, e.g. in stock VS backorder).
     *
     * @return true if multiple delivery is available.
     */
    Map<String, Boolean> getMultipleDeliveryAvailable();

    /**
     * Get additional details stored on this cart.
     *
     * @return additional details
     */
    Map<String, String> getDetails();

    /**
     * Get additional details stored on this cart by key.
     *
     * @param key key
     *
     * @return additional detail
     */
    String getDetailByKey(String key);

    /**
     * Get additional details stored on this cart by key.
     *
     * @param key key for boolean detail
     *
     * @return true if additional detail is true
     */
    boolean isDetailByKeyTrue(String key);

}
