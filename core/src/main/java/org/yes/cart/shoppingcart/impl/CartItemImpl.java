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

package org.yes.cart.shoppingcart.impl;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.service.order.impl.DeliveryBucketImpl;
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
    private String productName;

    @JsonProperty("qty")
    private BigDecimal quantity = DEFAULT_QUANTITY;
    private String supplierCode;
    private String deliveryGroup;

    private BigDecimal price = BigDecimal.ZERO;
    private BigDecimal salePrice = BigDecimal.ZERO;
    private BigDecimal listPrice = BigDecimal.ZERO;

    private BigDecimal netPrice = BigDecimal.ZERO;
    private BigDecimal grossPrice = BigDecimal.ZERO;
    private BigDecimal taxRate = BigDecimal.ZERO;
    private String taxCode;
    private boolean taxExclusiveOfPrice;

    private boolean gift;
    private boolean promoApplied;
    private boolean fixedPrice;
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
    void setProductSkuCode(final String productSkuCode) {
        this.productSkuCode = productSkuCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Set SKU name for this item (locale depends on cart locale)
     *
     * @param productName product name for this cart item
     */
    public void setProductName(final String productName) {
        this.productName = productName;
    }

    /**
     * {@inheritDoc}
     */
    public String getSupplierCode() {
        return supplierCode;
    }

    /**
     * Set supplier code for this item.
     *
     * @param supplierCode supplier code
     */
    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getDeliveryGroup() {
        return deliveryGroup;
    }

    /**
     * Set delivery group for this item.
     *
     * @param deliveryGroup delivery group
     */
    public void setDeliveryGroup(final String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
    }

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    public DeliveryBucket getDeliveryBucket() {

        return ShoppingCartUtils.getDeliveryBucket(this);

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
    public BigDecimal getNetPrice() {
        return netPrice;
    }

    /**
     * Set net price (price before tax).
     *
     * @param netPrice price before tax
     */
    public void setNetPrice(final BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    /** {@inheritDoc} */
    public BigDecimal getGrossPrice() {
        return grossPrice;
    }

    /**
     * Set net price (price after tax).
     *
     * @param grossPrice price after tax
     */
    public void setGrossPrice(final BigDecimal grossPrice) {
        this.grossPrice = grossPrice;
    }

    /** {@inheritDoc} */
    public String getTaxCode() {
        return taxCode;
    }

    /**
     * Set tax code reference.
     *
     * @param taxCode tax code
     */
    public void setTaxCode(final String taxCode) {
        this.taxCode = taxCode;
    }

    /** {@inheritDoc} */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /**
     * Set tax rate used (0-99).
     *
     * @param taxRate tax rate
     */
    public void setTaxRate(final BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /** {@inheritDoc} */
    public boolean isTaxExclusiveOfPrice() {
        return taxExclusiveOfPrice;
    }

    /**
     * Set whether this tax is included or excluded from price.
     *
     * @param taxExclusiveOfPrice tax flag
     */
    public void setTaxExclusiveOfPrice(final boolean taxExclusiveOfPrice) {
        this.taxExclusiveOfPrice = taxExclusiveOfPrice;
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
    public boolean isFixedPrice() {
        return fixedPrice;
    }

    /**
     * Fixed price flag.
     *
     * @param fixedPrice fixed price
     */
    public void setFixedPrice(final boolean fixedPrice) {
        this.fixedPrice = fixedPrice;
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

    @Override
    public String toString() {
        return "CartItemImpl{" +
                "productSkuCode='" + productSkuCode + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
