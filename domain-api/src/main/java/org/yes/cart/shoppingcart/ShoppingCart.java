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

import org.yes.cart.service.order.DeliveryBucket;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * Container class that represents Shopping Cart business object.
 * <p/>
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 10:18:38 PM
 */
public interface ShoppingCart extends Serializable {

    int NOT_LOGGED = 0;
    int SESSION_EXPIRED = 1;
    int LOGGED_IN = 2;
    int INACTIVE_FOR_SHOP = 3;


    /**
     * Get shopping cart guid.
     *
     * @return shopping cart guid.
     */
    String getGuid();


    /**
     * @return immutable list of shopping cart items.
     */
    List<CartItem> getCartItemList();


    /**
     * @return immutable map of shopping cart items.
     */
    Map<DeliveryBucket, List<CartItem>> getCartItemMap();


    /**
     * @return immutable list of shipping cart items.
     */
    List<CartItem> getShippingList();

    /**
     * @return immutable map of shipping choices.
     */
    Map<DeliveryBucket, List<CartItem>> getShippingListMap();

    /**
     * Get quantity of given SKU in cart (excluding gifts).
     *
     * @param sku sku to check
     * @return quantity of given sku
     */
    BigDecimal getProductSkuQuantity(String sku);


    /**
     * @return number of cart items currently in the shopping cart.
     */
    int getCartItemsCount();

    /**
     * @return show cart items suppliers (cartItem.supplierCode)
     */
    List<String> getCartItemsSuppliers();

    /**
     * @return coupon codes added to this cart
     */
    List<String> getCoupons();

    /**
     * @return coupon codes that triggered promotion
     */
    List<String> getAppliedCoupons();

    /**
     * Get current currency from shopping cart.
     *
     * @return current currency code
     */
    String getCurrencyCode();


    /**
     * Get the last modified date.
     *
     * @return last modified date.
     */
    long getModifiedTimestamp();

    /**
     * Returns true if cart has been updated.
     *
     * @return true if was updated
     */
    boolean isModified();


    /**
     * Get customer name.
     *
     * @return customer name or null if customer is anonymous
     */
    String getCustomerName();


    /**
     * Get customer email.
     *
     * @return customer name or null if customer is anonymous
     */
    String getCustomerEmail();


    /**
     * Is billing address different from shipping address.
     *
     * @return true is billing and shipping address are different.
     */
    boolean isSeparateBillingAddress();

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
     * Get carrier shipping SLA.
     *
     * @return carries sla id.
     */
    Map<String, Long> getCarrierSlaId();

    /**
     * Flag to determine if SLA had been selected for all buckets.
     *
     * @return true if all delivery buckets has SLA selection
     */
    boolean isAllCarrierSlaSelected();

    /**
     * Check if all items in this cart have been assigned a bucket.
     * If not this indicated that order splitting command must be run
     * before shipping step.
     *
     * @return true if all items and gifts have delivery bucket
     */
    boolean isAllCartItemsBucketed();

    /**
     * Get order message.
     *
     * @return order message
     */
    String getOrderMessage();

    /**
     * Get logon state.
     *
     * @return Logon state
     */
    int getLogonState();


    /**
     * Is sku code present in cart
     *
     * @param skuCode product sku code
     * @return true if sku code present in cart
     */
    boolean contains(String skuCode);

    /**
     * This method only searches for shipping indexes.
     *
     * @param carrierSlaGUID sku code
     * @param deliveryBucket delivery bucket
     * @return index of cart item for this sku
     */
    int indexOfShipping(final String carrierSlaGUID, DeliveryBucket deliveryBucket);

    /**
     * This method only searches for non-gift items indexes.
     *
     * @param skuCode sku code
     * @return index of cart item for this sku
     */
    int indexOfProductSku(final String skuCode);

    /**
     * This method only searches for gift items indexes.
     *
     * @param skuCode sku code
     * @return index of cart item for this sku
     */
    int indexOfGift(final String skuCode);

    /**
     * Get shopping context
     *
     * @return instance of {@link ShoppingContext}
     */
    ShoppingContext getShoppingContext();

    /**
     * Get order info.
     *
     * @return order information.
     */
    OrderInfo getOrderInfo();

    /**
     * Get current shopping cart locale. Preferred to work with generic locale -
     * en instead of en_US, etc.
     *
     * @return current locale
     */
    String getCurrentLocale();


    /**
     * Get date when this cart was processed through cycle.
     *
     * @return date.
     */
    long getProcessingStartTimestamp();

    /**
     * Total calculated for this cart.
     *
     * @return cart total
     */
    Total getTotal();

    /**
     * Order number for order amendment cart.
     *
     * @return order number
     */
    String getOrdernum();

}
