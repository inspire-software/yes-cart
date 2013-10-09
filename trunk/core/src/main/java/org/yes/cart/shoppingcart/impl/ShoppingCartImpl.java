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
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    private Date modifiedDate;

    private Date processingStartDate;

    private ShoppingContext shoppingContext;

    private OrderInfo orderInfo;

    private transient AmountCalculationStrategy calculationStrategy;


    private AmountCalculationStrategy getCalculationStrategy() {
        if (calculationStrategy == null) {
            calculationStrategy = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true);
            ShopCodeContext.getLog(this).error("Default amount calculation strategy used with 0% tax. Please configure \"calculationStrategy\" and set it to cart");
        }
        return calculationStrategy;
    }

    /**
     * Set amount calculation strategy.
     * @param calculationStrategy {@link AmountCalculationStrategy}
     */
    public void setCalculationStrategy(final AmountCalculationStrategy calculationStrategy) {
        this.calculationStrategy = calculationStrategy;
    }

    /**
     * Clean current cart and prepare it to reuse.
     */
    public void clean() {
        guid = java.util.UUID.randomUUID().toString();
        items = new ArrayList<CartItemImpl>();
        orderInfo = null;
        modifiedDate = new Date();
    }


    /**
     * Is billing address different from shipping adress.
     * @return true is billing and shipping address are different.
     */
    public boolean isSeparateBillingAddress() {
        return getOrderInfo().isSeparateBillingAddress();
    }


    /**
     * Get order message.
     * @return order message
     */
    public String getOrderMessage() {
        return getOrderInfo().getOrderMessage();
    }



    /**
     * Get shopping cart guid.
     * @return shopping cart guid.
     */
    public String getGuid() {
        return guid;
    }


    /**
     * Get carrier shipping SLA.
     * @return carries sla id.
     */
    public Long getCarrierSlaId() {
        return getOrderInfo().getCarrierSlaId();
    }


    /**
     * {@inheritDoc}
     */
    public List<CartItem> getCartItemList() {
        final List<CartItem> immutableItems = new ArrayList<CartItem>(getItems().size());
        for (CartItem item : getItems()) {
            immutableItems.add(new ImmutableCartItemImpl(item));
        }
        return Collections.unmodifiableList(immutableItems);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     *
     * @param productSku
     */
    public boolean removeCartItem(final String productSku) {
        final int skuIndex = indexOf(productSku);
        if (skuIndex != -1) {
            getItems().remove(skuIndex);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public int getCartItemsCount() {
        BigDecimal quantity = BigDecimal.ZERO;
        final List<? extends CartItem> items = getItems();
        for (CartItem cartItem : items) {
            quantity = quantity.add(cartItem.getQty());
        }
        return quantity.intValue();
    }



    /**
     * {@inheritDoc}
     */
    public BigDecimal getCartSubTotal() {

        return getCalculationStrategy().calculateSubTotal(this.getCartItemList());

    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getCartSubTotal(final boolean useListPrice) {

        return getCalculationStrategy().calculateSubTotal(this.getCartItemList(), useListPrice);

    }

    /**
     * {@inheritDoc}
     */
    public AmountCalculationResult getCartAmount(final List<? extends CartItem> items,
                                                 final CustomerOrderDelivery orderDelivery) {
        return getCalculationStrategy().calculate(
                getShoppingContext(),
                orderDelivery
        );
    }

    /**
     * @param  skuCode sku code
     * @return idex of cart item for this sku
     */
    public int indexOf(final String skuCode) {
        for (int index = 0; index < getItems().size(); index++) {
            final CartItem item = getItems().get(index);
            if (item.getProductSkuCode().equals(skuCode)) {
                return index;
            }
        }
        return -1;
    }


    /**
     * Is sku code present in cart
     * @param skuCode product sku code
     * @return true if sku code present in cart
     */
    public boolean contains(final String skuCode) {
        return (indexOf(skuCode) != -1);
    }


    /**
     * @return list of items (testing convenience method)
     */
    List<CartItemImpl> getItems() {
        return items;
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * {@inheritDoc}
     */
    void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getCustomerName() {
        return getShoppingContext().getCustomerName();
    }


    /**
     * {@inheritDoc}
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isModified() {
        return getProcessingStartDate().getTime() <= getModifiedDate().getTime();
    }

    /**
     * Set last modified date.
     *
     * @param modifiedDate last modified date.
     */
    void setModifiedDate(final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }



    /** {@inheritDoc} */
    public String getCustomerEmail() {
        return  getShoppingContext().getCustomerEmail();
    }


    /**
     *
     * Get logon state.
     *
     * @return Logon state
     */
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
     * Set shopping cart generic locale
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

    /**
     * {@inheritDoc}
     */
    public Date getProcessingStartDate() {
        return processingStartDate;
    }

    /**
     * {@inheritDoc}
     */
    public void setProcessingStartDate(final Date processingStartDate) {
        this.processingStartDate = processingStartDate;
    }


}
