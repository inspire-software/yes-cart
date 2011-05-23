package org.yes.cart.web.support.shoppingcart.impl;


import org.yes.cart.domain.dto.*;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.support.shoppingcart.VisitableShoppingCart;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Cookie aware shopping cart implementation that wraps core {@link ShoppingCart}
 * object in order to mark it with {@link org.yes.cart.web.support.util.cookie.annotations.PersistentCookie}
 * is used with {@link org.yes.cart.web.support.util.cookie.CookieTuplizer}.
 * TODO must be dynamic proxy with small addons like isChanged
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 2:03:06 PM
 */
public class CookieAwareShoppingCartImpl implements VisitableShoppingCart {

    private static final long serialVersionUID = 20110303L;

    private final ShoppingCart cart;

    //private transient boolean isChanged = false;


    /**
     * Default constructor.
     *
     * @param cart core cart implementation.
     */
    public CookieAwareShoppingCartImpl(final ShoppingCart cart) {
        this.cart = cart;
    }

    /**
     * Clean current cart and prepare it to reuse.
     */
    public void clean() {
        cart.clean();
    }


    /**
     * {@inheritDoc}
     */
    public List<CartItem> getCartItemList() {
        return cart.getCartItemList();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSeparateBillingAddress() {
        return cart.isSeparateBillingAddress();
    }


    /**
     * {@inheritDoc}
     */
    public String getGuid() {
        return cart.getGuid();
    }

    /**
     * {@inheritDoc}
     */
    public boolean addProductSkuToCart(final ProductSkuDTO sku, final BigDecimal quantity) {
        return cart.addProductSkuToCart(sku, quantity);
    }

    /**
     * {@inheritDoc}
     */
    public boolean setProductSkuToCart(final ProductSkuDTO sku, final BigDecimal quantity) {
        return cart.setProductSkuToCart(sku, quantity);
    }


    /**
     * {@inheritDoc}
     */
    public boolean removeCartItem(final ProductSkuDTO productSku) {
        return cart.removeCartItem(productSku);
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeCartItemQuantity(final ProductSkuDTO productSku, final BigDecimal quantity) {
        return cart.removeCartItemQuantity(productSku, quantity);
    }

    /**
     * {@inheritDoc}
     */
    public int getCartItemsCount() {
        return cart.getCartItemsCount();
    }


    /**
     * {@inheritDoc}
     */
    public BigDecimal getCartSubTotal(List<? extends CartItem> items) {
        return cart.getCartSubTotal(items);
    }

    /**
     * Set product sku price
     *
     * @param skuCode product sku
     * @param price   price to set
     * @return true if price has been set
     */
    public boolean setProductSkuPrice(final String skuCode, final BigDecimal price) {
        return cart.setProductSkuPrice(skuCode, price);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(final ShoppingCartCommand command) {
        command.execute(cart);
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrencyCode() {
        return cart.getCurrencyCode();
    }


    /**
     * {@inheritDoc}
     */
    public long getShopId() {
        return cart.getShopId();
    }

    /**
     * {@inheritDoc}
     */
    public void setShopId(final long shopId) {
        cart.setShopId(shopId);
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderMessage() {
        return cart.getOrderMessage();
    }

    /**
     * Is sku code present in cart
     *
     * @param skuCode product sku code
     * @return true if sku code present in cart
     */
    public boolean contains(final String skuCode) {
        return cart.contains(skuCode);
    }

    /**
     * @param skuCode sku code
     * @return idex of cart item for this sku
     */
    public int indexOf(final String skuCode) {
        return cart.indexOf(skuCode);
    }

    /**
     * {@inheritDoc}
     */
    public String getCustomerName() {
        return cart.getCustomerName();
    }


    /**
     * {@inheritDoc}
     */
    public Date getModifiedDate() {
        return cart.getModifiedDate();
    }

    /**
     * {@inheritDoc}
     */
    public void setModifiedDate(final Date modified) {
        cart.setModifiedDate(modified);
    }

    /**
     * {@inheritDoc}
     */
    public String getCustomerEmail() {
        return cart.getCustomerEmail();
    }


    /**
     * {@inheritDoc}
     */
    public Integer getCarrierSlaId() {
        return cart.getCarrierSlaId();
    }

    /**
     * Get logon state.
     *
     * @return Logon state
     */
    public int getLogonState() {
        return cart.getLogonState();
    }


    public ShoppingContext getShoppingContext() {
        return cart.getShoppingContext();
    }

    public OrderInfo getOrderInfo() {
        return cart.getOrderInfo();
    }
}
