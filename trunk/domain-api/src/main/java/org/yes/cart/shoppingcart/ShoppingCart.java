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

import org.yes.cart.domain.entity.CustomerOrderDelivery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


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

    /**
     * Set amount calculation strategy.
     *
     * @param calculationStrategy {@link AmountCalculationStrategy}
     */
    void initialise(AmountCalculationStrategy calculationStrategy);

    /**
     * Mark this cart as dirty thus eligible for persisting.
     */
    void markDirty();


    /**
     * Clean current cart and prepare it to reuse.
     */
    void clean();

    /**
     * Recalculate totals, promotions and shipping cost for this cart.
     */
    void recalculate();


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
     * Add product sku to cart.
     *
     * @param sku      product sku to add
     * @param quantity the quantity to add
     * @return true if item has been added to the cart as a separate cart item,
     *         false if adding this item cause only quantity update of already present in cart
     *         product sku.
     */
    boolean addProductSkuToCart(String sku, BigDecimal quantity);

    /**
     * Set sku quantity, in case if sku not present in cart it will be added.
     *
     * @param sku      product sku to add
     * @param quantity the quantity to add
     * @return true if item has been added to the cart as a separate cart item,
     *         false if adding this item cause only quantity update of already present in cart
     *         product sku.
     */
    boolean setProductSkuToCart(String sku, BigDecimal quantity);


    /**
     * Removes the cart item from shopping cart.
     *
     * @param productSku product sku
     * @return true if item has been removed, false if item was not present in the cart.
     */
    boolean removeCartItem(String productSku);

    /**
     * Removes a specified quantity from the shopping cart item
     *
     * @param productSku product sku
     * @param quantity   quantity to remove
     * @return true if quantity has been removed, false if item was not present in the cart.
     */
    boolean removeCartItemQuantity(String productSku, BigDecimal quantity);

    /**
     * Set product sku price
     *
     * @param productSkuCode product sku
     * @param price          price to set
     * @param listPrice      list price - without discounts, promos, etc.
     * @return true if price has been set
     */
    boolean setProductSkuPrice(String productSkuCode, BigDecimal price, BigDecimal listPrice);

    /**
     * @return number of cart items currently in the shopping cart.
     */
    int getCartItemsCount();


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
     * Get carrier shipping SLA.
     *
     * @return carries sla id.
     */
    Long getCarrierSlaId();

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
     * @param skuCode sku code
     * @return index of cart item for this sku
     */
    int indexOf(final String skuCode);

    /**
     * Get shopping context
     *
     * @return instance of {@link ShoppingContext}
     */
    ShoppingContext getShoppingContext();

    /**
     * GEt order info.
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

}
