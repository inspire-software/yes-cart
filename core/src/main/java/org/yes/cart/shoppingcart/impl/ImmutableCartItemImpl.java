/*
 * Copyright 2009 Inspire-Software.com
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


import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.CartItem;

import java.math.BigDecimal;

/**
 * Wrapper to prevent modification to cart object.
 * <p/>
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 11:10:28 PM
 */
public class ImmutableCartItemImpl implements CartItem {

    private static final long serialVersionUID = 20100626L;

    private final CartItem cartItem;

    /**
     * Default constructor.
     *
     * @param cartItem cart item to make immutable.
     */
    public ImmutableCartItemImpl(final CartItem cartItem) {
        this.cartItem = cartItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProductSkuCode() {
        return cartItem.getProductSkuCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProductName() {
        return cartItem.getProductName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSupplierCode() {
        return cartItem.getSupplierCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeliveryGroup() {
        return cartItem.getDeliveryGroup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getItemGroup() {
        return cartItem.getItemGroup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeliveryBucket getDeliveryBucket() {
        return cartItem.getDeliveryBucket();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getQty() {
        return cartItem.getQty();
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public BigDecimal getPrice() {
        return cartItem.getPrice();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getListPrice() {
        return cartItem.getListPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getSalePrice() {
        return cartItem.getSalePrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConfigurable() {
        return cartItem.isConfigurable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNotSoldSeparately() {
        return cartItem.isNotSoldSeparately();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGift() {
        return cartItem.isGift();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPromoApplied() {
        return cartItem.isPromoApplied();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFixedPrice() {
        return cartItem.isFixedPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAppliedPromo() {
        return cartItem.getAppliedPromo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getNetPrice() {
        return cartItem.getNetPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getGrossPrice() {
        return cartItem.getGrossPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaxCode() {
        return cartItem.getTaxCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getTaxRate() {
        return cartItem.getTaxRate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTaxExclusiveOfPrice() {
        return cartItem.isTaxExclusiveOfPrice();
    }

    @Override
    public String toString() {
        return "ImmutableCartItemImpl{" +
                "cartItem=" + cartItem +
                '}';
    }
}
