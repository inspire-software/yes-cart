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
    int INACTIVE_FOR_SHOP = 3;

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
     * Get quantity of given SKU in cart (excluding gifts).
     *
     * @param sku sku to check
     * @return quantity of given sku
     */
    BigDecimal getProductSkuQuantity(String sku);

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
     * Add product sku to cart.
     *
     * @param sku      product sku to add
     * @param quantity the quantity to add
     * @param promotionCode promotion code fof promotion that resulted in this gift
     * @return true if item has been added to the cart as a separate cart item,
     *         false if adding this item cause only quantity update of already present in cart
     *         product sku.
     */
    boolean addGiftToCart(String sku, BigDecimal quantity, String promotionCode);

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
     * Remove all cart promotions, which effectively removes promotion prices and reinstates
     * sale price as final price and clear all promotion data and gift items.
     *
     * This method only acts upon promotions at the item level. Note that this method should
     * not be used manually as it puts the cart total in out of sync. The purpose of this method
     * is clearing up cart by amount calculation strategy before it starts calculating cart.
     *
     * @return  true if promotions have been removed, false if item was not present in the cart.
     */
    boolean removeItemPromotions();

    /**
     * Set product sku price and clear all promotion details
     *
     * @param productSkuCode product sku
     * @param salePrice      price to set - sale price without promos
     * @param listPrice      list price - without discounts, promos, etc.
     * @return true if price has been set
     */
    boolean setProductSkuPrice(String productSkuCode, BigDecimal salePrice, BigDecimal listPrice);

    /**
     * Set product sku price and clear all promotion details
     *
     * @param productSkuCode product sku
     * @param salePrice      price to set - sale price without promos
     * @param listPrice      list price - without discounts, promos, etc.
     * @return true if price has been set
     */
    boolean setGiftPrice(String productSkuCode, BigDecimal salePrice, BigDecimal listPrice);

    /**
     * Set product sku price and clear all promotion details
     *
     * @param productSkuCode product sku
     * @param promoPrice     price to set
     * @param promoCode      promotion code that activated this discount
     * @return true if price has been set
     */
    boolean setProductSkuPromotion(String productSkuCode, BigDecimal promoPrice, String promoCode);

    /**
     * @return number of cart items currently in the shopping cart.
     */
    int getCartItemsCount();

    /**
     * @return coupon codes added to this cart
     */
    List<String> getCoupons();

    /**
     * @return coupon codes that triggered promotion
     */
    List<String> getAppliedCoupons();

    /**
     * @param coupon coupon code
     */
    boolean addCoupon(String coupon);

    /**
     * @param coupon remove coupon from the cart
     */
    boolean removeCoupon(String coupon);

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
