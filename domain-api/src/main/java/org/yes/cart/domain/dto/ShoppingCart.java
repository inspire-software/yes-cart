package org.yes.cart.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//import org.springframework.security.core.context.SecurityContext;

/**
 * TODO most of the setters MUST be removed from this interface.
 * TODO Only commands can modify cart !!!
 * <p/>
 * Container class that represents Shopping Cart business object.
 * <p/>
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 10:18:38 PM
 */
public interface ShoppingCart extends  Serializable {

    public final static int NOT_LOGGED = 0;
    public final static int SESSION_EXPIRED = 1;
    public final static int LOGGED_IN = 2;


    /**
     * Clean current cart and prepare it to reuse.
     */
    void clean();




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
     * Add product sku to cart.
     *
     * @param sku      product sku to add
     * @param quantity the quantity to add
     * @return true if item has been added to the cart as a separate cart item,
     *         false if adding this item cause only quantity update of already present in cart
     *         product sku.
     */
    boolean addProductSkuToCart(ProductSkuDTO sku, BigDecimal quantity);

    /**
     * Set sku quantity, in case if sku not present in cart it will be added.
     *
     * @param sku      product sku to add
     * @param quantity the quantity to add
     * @return true if item has been added to the cart as a separate cart item,
     *         false if adding this item cause only quantity update of already present in cart
     *         product sku.
     */
    boolean setProductSkuToCart(ProductSkuDTO sku, BigDecimal quantity);


    /**
     * Removes the cart item from shopping cart.
     *
     * @param productSku product sku
     * @return true if item has been removed, false if item was not present in the cart.
     */
    boolean removeCartItem(ProductSkuDTO productSku);

    /**
     * Removes a specified quantity from the shopping cart item
     *
     * @param productSku product sku
     * @param quantity   quantity to remove
     * @return true if quantity has been removed, false if item was not present in the cart.
     */
    boolean removeCartItemQuantity(ProductSkuDTO productSku, BigDecimal quantity);

    /**
     * Set product sku price
     *
     * @param productSkuCode product sku
     * @param price          price to set
     * @return true if price has been set
     */
    boolean setProductSkuPrice(String productSkuCode, BigDecimal price);

    /**
     * @return number of cart items currently in the shopping cart.
     */
    int getCartItemsCount();

    /**
     * TODO need total refactoring about money calculation
     * Get cart sub total by given item list.
     *
     * @param items given items
     * @return total amount for all items in the shopping cart.
     */
    BigDecimal getCartSubTotal(List<? extends CartItem> items);

    /**
     * Get current currency from shopping cart.
     *
     * @return current currency code
     */
    String getCurrencyCode();

    /**
     * Set cart currency.
     *
     * @param currencyCode currency code.
     */
    void setCurrencyCode(String currencyCode);


    /**
     * Get the last modified date.
     *
     * @return last modified date.
     */
    Date getModifiedDate();


    /**
     * Set last modified date.
     *
     * @param modified last modified date.
     */
    void setModifiedDate(Date modified);

    /**
     * Get customer name.
     *
     * @return customer name or null if customer is anonymous
     */
    String getCustomerName();

    /**
     * Set customer name.
     *
     * @param customerName customer name.
     */
    void setCustomerName(String customerName);


    /**
     * Get customer email. In fact custome is logged on if email not null
     * and cart modification date not more that 30 minutes (configurable TODO config param name)
     *
     * @return customer name or null if customer is anonymous
     */
    String getCustomerEmail();

///////////////////////////////////////////////////////// remove to order info end //////////////////////////////////////////////////////



    /**
     * Is billing address different from shipping adress.
     *
     * @return true is billing and shipping address are different.
     */
    boolean isSeparateBillingAddress();

    /**
     * Set billilnd address different from shipping address flag.
     *
     * @param separateBillingAddress flag.
     */
    void setSeparateBillingAddress(boolean separateBillingAddress);
    /**
     * Get carrier shipping SLA.
     *
     * @return carries sla id.
     */
    Integer getCarrierSlaId();

    /**
     * Set carrier shipping SLA.
     *
     * @param carrierSlaId selected sla id.
     */
    void setCarrierSlaId(Integer carrierSlaId);

    /**
     * Get order message.
     *
     * @return order message
     */
    String getOrderMessage();

    /**
     * Set order message.
     *
     * @param orderMessage order message.
     */
    void setOrderMessage(String orderMessage);
///////////////////////////////////////////////////////// remove to order info end //////////////////////////////////////////////////////

    /**
     * Get logon state.
     *
     * @return Logon state
     */
    int getLogonState();

    /**
     * Get current shop id
     *
     * @return current shop id.
     */
    long getShopId();

    /**
     * Set current shop id.
     *
     * @param shopId current shop id.
     */
    void setShopId(long shopId);



    /**
     * Is sku code present in cart
     *
     * @param skuCode product sku code
     * @return true if sku code present in cart
     */
    boolean contains(String skuCode);

    /**
     * @param skuCode sku code
     * @return idex of cart item for this sku
     */
    int indexOf(final String skuCode);

    /**
     * Get shopping context
     * @return instance of {@link ShoppingContext}
     */
    ShoppingContext getShoppingContext();

    /**
     * GEt order info.
     * @return order infornmation.
     */
    OrderInfo getOrderInfo();



}
