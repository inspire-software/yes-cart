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

package org.yes.cart.shoppingcart.impl;


import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;

/**
 * Default cart item implementation.
 * <p/>
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 10:42:02 PM
 */
public class CartItemImpl implements CartItem {

    private static final long serialVersionUID = 20100116L;

    private static final BigDecimal DEFAULT_QUANTITY = BigDecimal.ONE; // do not simplify this, because of min quantity pair, triple , etc.

    private String productSkuCode;

    private BigDecimal quantity = DEFAULT_QUANTITY;

    private BigDecimal price = BigDecimal.ZERO;
    private BigDecimal salePrice = BigDecimal.ZERO;
    private BigDecimal listPrice = BigDecimal.ZERO;

    private boolean gift;
    private boolean promoApplied;
    private String appliedPromo;

    /**
     * {@inheritDoc}
     */
    public String getProductSkuCode() {
        return productSkuCode;
    }

    /**
     * Set sku code for this item.
     *
     * @param productSkuCode product sku for this cart item
     *
     * @throws IllegalArgumentException if productSku is null.
     */
    void setProductSkuCode(final String productSkuCode) throws IllegalArgumentException {
        this.productSkuCode = productSkuCode;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getQty() {
        return new BigDecimal(quantity.toString());
    }

    /**
     * Sets the quantity. Null safe. if a null is passed then quantity is set to one.
     *
     * @param quantity quantity of the above sku to be purchased
     */
    public void setQuantity(final BigDecimal quantity) {
        if (MoneyUtils.isFirstBiggerThanOrEqualToSecond( BigDecimal.ZERO, quantity )) { // negative  or 0
            this.quantity = DEFAULT_QUANTITY;
        } else {
            this.quantity = quantity;
        }
    }

    /**
     * Adds quantity to current quantity. Null/Negative value safe, it is converted to zero.
     *
     * @param quantity quantity to add
     *
     * @return current quantity for this cart item
     */
    public BigDecimal addQuantity(final BigDecimal quantity) {

        final BigDecimal notNullQty = MoneyUtils.notNull(quantity, BigDecimal.ZERO);

        if (MoneyUtils.isFirstBiggerThanSecond(notNullQty, BigDecimal.ZERO)) {
            this.quantity = this.quantity.add(notNullQty);
        }
        return getQty();
    }

    /**
     * Removes quantity to current quantity. Null/Negative value safe, it is converted to zero.
     *
     * @param quantity quantity to remove
     *
     * @return current quantity for this cart item
     *
     * @throws CartItemRequiresDeletion thrown when cart item quantity goe down to zero.
     */
    public BigDecimal removeQuantity(final BigDecimal quantity) throws CartItemRequiresDeletion {

        final BigDecimal notNullQty = MoneyUtils.notNull(quantity, BigDecimal.ZERO);

        if (MoneyUtils.isFirstBiggerThanSecond(notNullQty, BigDecimal.ZERO)) {
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(notNullQty, this.quantity)) {
                throw new CartItemRequiresDeletion();
            } else {
                this.quantity = this.quantity.subtract(notNullQty);
            }
        }
        return getQty();
    }

    /** {@inheritDoc} */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Set price including all promotions.
     *
     * @param price price to set.
     */
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }


    /** {@inheritDoc} */
    public BigDecimal getListPrice() {
        return listPrice;
    }

    /**
     * Set list (regular/catalog) price.
     *
     * @param listPrice to set.
     */
    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    /** {@inheritDoc} */
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    /**
     * Sale price before promotions.
     *
     * @param salePrice sale price
     */
    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    /** {@inheritDoc} */
    public boolean isGift() {
        return gift;
    }

    /**
     * Set gift flag.
     *
     * @param gift true if this is a gift
     */
    public void setGift(final boolean gift) {
        this.gift = gift;
    }

    /** {@inheritDoc} */
    public boolean isPromoApplied() {
        return promoApplied;
    }

    /**
     * Set promo flag.
     *
     * @param promoApplied true if promotion has been applied
     */
    public void setPromoApplied(final boolean promoApplied) {
        this.promoApplied = promoApplied;
    }

    /** {@inheritDoc} */
    public String getAppliedPromo() {
        return appliedPromo;
    }

    /**
     * Set comma separated list of promotions.
     *
     * @param appliedPromo promotions
     */
    public void setAppliedPromo(final String appliedPromo) {
        this.appliedPromo = appliedPromo;
    }
}
