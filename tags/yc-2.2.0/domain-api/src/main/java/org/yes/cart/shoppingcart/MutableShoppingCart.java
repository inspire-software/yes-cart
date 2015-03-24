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

/**
 * User: denispavlov
 * Date: 05/11/2014
 * Time: 09:55
 */
public interface MutableShoppingCart extends ShoppingCart, Serializable {


    /**
     * Set amount calculation strategy.
     *
     * @param calculationStrategy {@link org.yes.cart.shoppingcart.AmountCalculationStrategy}
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
     * @param carrierSlaId sla pk to add
     * @param quantity     the quantity of this SLA (i.e. number of deliveries)
     * @return true if item has been added to the cart as a separate cart item,
     *         false if adding this item cause only quantity update of already present in cart
     *         shipping SLA.
     */
    boolean addShippingToCart(String carrierSlaId, BigDecimal quantity);

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
     * Removes the shipping from shopping cart.
     *
     * @param carrierSlaId carrier SLA PK
     * @return true if item has been removed, false if item was not present in the cart.
     */
    boolean removeShipping(String carrierSlaId);

    /**
     * Removes all shipping lines from shopping cart.
     *
     * @return true if item has been removed, false if item was not present in the cart.
     */
    boolean removeShipping();

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
     * Set shipping price and clear all promotion details
     *
     * @param carrierSlaId   carrier sla PK
     * @param salePrice      price to set - sale price without promos
     * @param listPrice      list price - without discounts, promos, etc.
     * @return true if price has been set
     */
    boolean setShippingPrice(String carrierSlaId, BigDecimal salePrice, BigDecimal listPrice);

    /**
     * Set gift product sku promotion details
     *
     * @param productSkuCode product sku
     * @param salePrice      price to set - sale price without promos
     * @param listPrice      list price - without discounts, promos, etc.
     * @return true if price has been set
     */
    boolean setGiftPrice(String productSkuCode, BigDecimal salePrice, BigDecimal listPrice);

    /**
     * Set product sku promotion details
     *
     * @param productSkuCode product sku
     * @param promoPrice     price to set
     * @param promoCode      promotion code that activated this discount
     * @return true if price has been set
     */
    boolean setProductSkuPromotion(String productSkuCode, BigDecimal promoPrice, String promoCode);

    /**
     * Set shipping promotion details
     *
     * @param carrierSlaId   carrier sla PK
     * @param promoPrice     price to set
     * @param promoCode      promotion code that activated this discount
     * @return true if price has been set
     */
    boolean setShippingPromotion(String carrierSlaId, BigDecimal promoPrice, String promoCode);

    /**
     * Set product sku taxes
     *
     * @param productSkuCode product sku
     * @param netPrice       price before tax
     * @param grossPrice     price after tax
     * @param rate           tax rate
     * @param taxCode        tax code
     * @param exclPrice      excluded from price
     *
     * @return true if tax has been set
     */
    boolean setProductSkuTax(String productSkuCode, BigDecimal netPrice, BigDecimal grossPrice, BigDecimal rate, String taxCode, boolean exclPrice);

    /**
     * Set shipping taxes
     *
     * @param carrierSlaPk   carrier sla PK
     * @param netPrice       price before tax
     * @param grossPrice     price after tax
     * @param rate           tax rate
     * @param taxCode        tax code
     * @param exclPrice      excluded from price
     *
     * @return true if tax has been set
     */
    boolean setShippingTax(String carrierSlaPk, BigDecimal netPrice, BigDecimal grossPrice, BigDecimal rate, String taxCode, boolean exclPrice);

    /**
     * @param coupon coupon code
     */
    boolean addCoupon(String coupon);

    /**
     * @param coupon remove coupon from the cart
     */
    boolean removeCoupon(String coupon);

    /**
     * Set currency code.
     *
     * @param currencyCode currency code
     */
    void setCurrencyCode(final String currencyCode);

    /**
     * Set shopping cart generic locale.
     *
     * @param currentLocale current locale
     */
    void setCurrentLocale(final String currentLocale);

    /**
     * Get shopping context
     *
     * @return instance of {@link ShoppingContext}
     */
    MutableShoppingContext getShoppingContext();

    /**
     * Get order info.
     *
     * @return order information.
     */
    MutableOrderInfo getOrderInfo();

}
