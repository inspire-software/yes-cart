

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.shoppingcart.VisitableShoppingCart;


import java.math.BigDecimal;
import java.util.List;
import java.util.Date;

import org.yes.cart.domain.dto.ShoppingCart;
import org.yes.cart.domain.dto.CartItem;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.ShoppingContext;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

/**
 * Cookie aware shopping cart implementation that wraps core {@link ShoppingCart}
 * object in order to mark it with {@link org.yes.cart.util.cookie.annotations.PersistentCookie}
 * is used with {@link org.yes.cart.util.cookie.CookieTuplizer}.
 * TODO must be dynamic proxy with small addons like isChanged
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 2:03:06 PM
 */
public class CookieAwareShoppingCartImpl implements VisitableShoppingCart {

    private static final long serialVersionUID = 20110303L;

    private final ShoppingCart cart;

    private transient boolean isChanged = false;




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
        setChanged(true);
        cart.clean();
    }


    /** {@inheritDoc} */
    public List<CartItem> getCartItemList() {
        return cart.getCartItemList();
    }

    /** {@inheritDoc} */
    public boolean isSeparateBillingAddress() {
        return cart.isSeparateBillingAddress();
    }

    /** {@inheritDoc} */
    public boolean isMultipleDelivery() {
        return cart.isMultipleDelivery();
    }

    /** {@inheritDoc} */
    public void setMultipleDelivery(final boolean multipleDelivery) {
        setChanged(true);
        cart.setMultipleDelivery(multipleDelivery);
    }


    /** {@inheritDoc} */
    public String getPaymentGatewayLabel() {
        return cart.getPaymentGatewayLabel();
    }

    /** {@inheritDoc} */
    public void setPaymentGatewayLabel(String paymentGatewayLabel) {
        cart.setPaymentGatewayLabel(paymentGatewayLabel);
    }

    /** {@inheritDoc} */
    public void setSeparateBillingAddress(final boolean separateBillingAddress) {
        setChanged(true);
        cart.setSeparateBillingAddress(separateBillingAddress);
    }

    /**
     * {@inheritDoc}
     */
    public String getGuid() {
        return cart.getGuid();
    }

    /** {@inheritDoc} */
    public boolean addProductSkuToCart(final ProductSkuDTO sku, final BigDecimal quantity) {
        setChanged(true);
        return cart.addProductSkuToCart(sku, quantity);
    }

    /** {@inheritDoc} */
    public boolean setProductSkuToCart(final ProductSkuDTO sku, final BigDecimal quantity) {
        setChanged(true);
        return cart.setProductSkuToCart(sku, quantity);
    }



    /** {@inheritDoc} */
    public boolean removeCartItem(final ProductSkuDTO productSku) {
        setChanged(true);
        return cart.removeCartItem(productSku);
    }

    /** {@inheritDoc} */
    public boolean removeCartItemQuantity(final ProductSkuDTO productSku, final BigDecimal quantity) {
        setChanged(true);
        return cart.removeCartItemQuantity(productSku, quantity);
    }

    /** {@inheritDoc} */
    public int getCartItemsCount() {
        return cart.getCartItemsCount();
    }


    /** {@inheritDoc} */
    public BigDecimal getCartSubTotal(List<? extends CartItem> items) {
        return cart.getCartSubTotal(items);
    }

    /**
     * Set product sku price
     *
     * @param skuCode  product sku
     * @param price      price to set
     * @return true if price has been set
     */
    public boolean setProductSkuPrice(final String skuCode, final BigDecimal price) {
        return cart.setProductSkuPrice(skuCode, price);
    }

    /** {@inheritDoc} */
    public void accept(final ShoppingCartCommand command) {
        command.execute(cart);
        setChanged(true);
    }

    /** {@inheritDoc} */
    public String getCurrencyCode() {
        return cart.getCurrencyCode();
    }

    /** {@inheritDoc} */
    public void setCurrencyCode(final String currencyCode) {
        setChanged(true);
        cart.setCurrencyCode(currencyCode);
    }

    /** {@inheritDoc} */
    public long getShopId() {
        return cart.getShopId();
    }

    /** {@inheritDoc} */
    public void setShopId(final long shopId) {
        setChanged(true);
        cart.setShopId(shopId);
    }

    /** {@inheritDoc} */
    public String getOrderMessage() {
        return cart.getOrderMessage();
    }

    /** {@inheritDoc} */
    public void setOrderMessage(final String orderMessage) {
        setChanged(true);
        cart.setOrderMessage(orderMessage);
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
    public void setCustomerName(final String customerName) {
        setChanged(true);
        cart.setCustomerName(customerName);
    }



    /** {@inheritDoc} */
    public void setChanged(boolean changed) {
        isChanged = changed;
        if (isChanged) {
            setModifiedDate(new Date());
        }        
    }

    /** {@inheritDoc} */
    public boolean isChanged() {
        return isChanged;
    }

    /** {@inheritDoc} */
    public Date getModifiedDate() {
        return cart.getModifiedDate();
    }

    /** {@inheritDoc} */
    public void setModifiedDate(final Date modified) {
        cart.setModifiedDate(modified);
    }

    /** {@inheritDoc} */
    public String getCustomerEmail() {
        return cart.getCustomerEmail();
    }
    

    /** {@inheritDoc} */
    public Integer getCarrierSlaId() {
        return cart.getCarrierSlaId();
    }

    /** {@inheritDoc} */
    public void setCarrierSlaId(final Integer carrierSlaId) {
        setChanged(true);
        cart.setCarrierSlaId(carrierSlaId);
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
}
