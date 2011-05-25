package org.yes.cart.shoppingcart.impl;


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
     * @return product sku code.
     */
    public String getProductSkuCode() {
        return cartItem.getProductSkuCode();
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getQty() {
        return cartItem.getQty();
    }

    /**
     * {@inheritDoc}
     */    
    public BigDecimal getPrice() {
        return cartItem.getPrice();
    }
}
