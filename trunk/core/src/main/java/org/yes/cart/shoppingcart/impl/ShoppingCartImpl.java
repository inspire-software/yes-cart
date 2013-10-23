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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shopping cart default implementation.
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 10:39:12 PM
 */
public class ShoppingCartImpl implements ShoppingCart {

    private static final long serialVersionUID =  20110509L;

    private List<CartItemImpl> items = new ArrayList<CartItemImpl>();

    private String guid = java.util.UUID.randomUUID().toString();

    private String currentLocale;

    private String currencyCode;

    private long modifiedTimestamp;

    private long processingStartTimestamp;

    private ShoppingContext shoppingContext;

    private OrderInfo orderInfo;

    private Total total;

    private transient AmountCalculationStrategy calculationStrategy;


    private AmountCalculationStrategy getCalculationStrategy() {
        if (calculationStrategy == null) {
            ShopCodeContext.getLog(this).error("Cart amount calculation strategy is not configured. Please configure \"calculationStrategy\" and set it to cart");
            throw new RuntimeException("Cart amount calculation strategy is not configured. Please configure \"calculationStrategy\" and set it to cart");
        }
        return calculationStrategy;
    }

    /**
     * Initialise this cart.
     *
     * @param calculationStrategy {@link AmountCalculationStrategy}
     */
    public void initialise(final AmountCalculationStrategy calculationStrategy) {
        this.calculationStrategy = calculationStrategy;
        this.processingStartTimestamp = System.currentTimeMillis();
    }

    /** {@inheritDoc} */
    public void markDirty() {
        this.modifiedTimestamp = System.currentTimeMillis();
    }

    /** {@inheritDoc} */
    public void clean() {
        guid = java.util.UUID.randomUUID().toString();
        items = new ArrayList<CartItemImpl>();
        orderInfo = null;
        modifiedTimestamp = System.currentTimeMillis();
    }

    /** {@inheritDoc} */
    public void recalculate() {
        total = getCalculationStrategy().calculate(this);
    }

    /** {@inheritDoc} */
    public boolean isSeparateBillingAddress() {
        return getOrderInfo().isSeparateBillingAddress();
    }

    /** {@inheritDoc} */
    public String getOrderMessage() {
        return getOrderInfo().getOrderMessage();
    }

    /** {@inheritDoc} */
    public String getGuid() {
        return guid;
    }

    /** {@inheritDoc} */
    public Long getCarrierSlaId() {
        return getOrderInfo().getCarrierSlaId();
    }

    /** {@inheritDoc} */
    public List<CartItem> getCartItemList() {
        final List<CartItem> immutableItems = new ArrayList<CartItem>(getItems().size());
        for (CartItem item : getItems()) {
            immutableItems.add(new ImmutableCartItemImpl(item));
        }
        return Collections.unmodifiableList(immutableItems);
    }

    /** {@inheritDoc} */
    public boolean addProductSkuToCart(final String sku, final BigDecimal quantity) {

        final int skuIndex = indexOf(sku);
        if (skuIndex != -1) {
            getItems().get(skuIndex).addQuantity(quantity);
            return false;
        }

        final CartItemImpl newItem = new CartItemImpl();
        newItem.setProductSkuCode(sku);
        newItem.setQuantity(quantity);
        getItems().add(newItem);
        return true;
    }

    /** {@inheritDoc} */
    public boolean setProductSkuToCart(final String sku, final BigDecimal quantity) {

        final CartItemImpl newItem = new CartItemImpl();
        newItem.setProductSkuCode(sku);
        newItem.setQuantity(quantity);

        final int skuIndex = indexOf(sku);
        if (skuIndex == -1) { //not found
            getItems().add(newItem);
        } else {
            getItems().set(skuIndex, newItem);
        }
        return true;
    }

    /** {@inheritDoc} */
    public boolean removeCartItem(final String productSku) {
        final int skuIndex = indexOf(productSku);
        if (skuIndex != -1) {
            getItems().remove(skuIndex);
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    public boolean removeCartItemQuantity(final String productSku, final BigDecimal quantity) {
        final int skuIndex = indexOf(productSku);
        if (skuIndex != -1) {
            try {
                getItems().get(skuIndex).removeQuantity(quantity);
            } catch (final CartItemRequiresDeletion cartItemRequiresDeletion) {
                getItems().remove(skuIndex);
            }
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    public boolean setProductSkuPrice(final String skuCode, final BigDecimal price, final BigDecimal listPrice) {
        final int skuIndex = indexOf(skuCode);
        if (skuIndex != -1) {
            final CartItemImpl cartItem = getItems().get(skuIndex);
            cartItem.setPrice(price);
            cartItem.setListPrice(listPrice);
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    public int getCartItemsCount() {
        BigDecimal quantity = BigDecimal.ZERO;
        final List<? extends CartItem> items = getItems();
        for (CartItem cartItem : items) {
            quantity = quantity.add(cartItem.getQty());
        }
        return quantity.intValue();
    }

    /** {@inheritDoc} */
    public int indexOf(final String skuCode) {
        for (int index = 0; index < getItems().size(); index++) {
            final CartItem item = getItems().get(index);
            if (item.getProductSkuCode().equals(skuCode)) {
                return index;
            }
        }
        return -1;
    }


    /** {@inheritDoc} */
    public boolean contains(final String skuCode) {
        return (indexOf(skuCode) != -1);
    }


    /** {@inheritDoc} */
    List<CartItemImpl> getItems() {
        return items;
    }

    /** {@inheritDoc} */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Set currency.
     *
     * @param currencyCode new currency to use
     */
    void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /** {@inheritDoc} */
    public String getCustomerName() {
        return getShoppingContext().getCustomerName();
    }

    /** {@inheritDoc} */
    public long getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    /** {@inheritDoc} */
    public boolean isModified() {
        return processingStartTimestamp < modifiedTimestamp;
    }

    /** {@inheritDoc} */
    public String getCustomerEmail() {
        return  getShoppingContext().getCustomerEmail();
    }

    /** {@inheritDoc} */
    public int getLogonState() {
        if (StringUtils.isBlank(getCustomerEmail())
                   && StringUtils.isNotBlank(getCustomerName())) {
            return ShoppingCart.SESSION_EXPIRED;
        } else if (StringUtils.isNotBlank(getCustomerEmail())
                   && StringUtils.isNotBlank(getCustomerName())) {
            return ShoppingCart.LOGGED_IN;
        }
        return ShoppingCart.NOT_LOGGED;
    }

    /** {@inheritDoc} */
    public ShoppingContext getShoppingContext() {
        if (shoppingContext == null) {
            shoppingContext = new ShoppingContextImpl();
        }
        return shoppingContext;
    }

    /** {@inheritDoc} */
    public String getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Set shopping cart generic locale.
     *
     * @param currentLocale current locale
     */
    void setCurrentLocale(final String currentLocale) {
        this.currentLocale = currentLocale;
    }

    /** {@inheritDoc} */
    public OrderInfo getOrderInfo() {
        if (orderInfo == null) {
            orderInfo = new OrderInfoImpl();
        }
        return orderInfo;
    }

    /** {@inheritDoc} */
    public long getProcessingStartTimestamp() {
        return processingStartTimestamp;
    }

    /** {@inheritDoc} */
    public Total getTotal() {
        if (total == null) {
            ShopCodeContext.getLog(this).error("Total requested before cart was recalculated - revise page flow to ensure that recalculation happens");
            total = new TotalImpl();
        }
        return total;
    }
}
