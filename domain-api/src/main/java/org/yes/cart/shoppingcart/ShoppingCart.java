package org.yes.cart.shoppingcart;

import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.entity.CustomerOrderDelivery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * Container class that represents Shopping Cart business object.
 * <p/>
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 10:18:38 PM
 */
public interface ShoppingCart extends Serializable {

    int NOT_LOGGED = 0;
    int SESSION_EXPIRED = 1;
    int LOGGED_IN = 2;


    /**
     * Set amount calculation strategy.
     * @param calculationStrategy {@link AmountCalculationStrategy}
     */
    void setCalculationStrategy(AmountCalculationStrategy calculationStrategy);


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
     * Get current currency from shopping cart.
     *
     * @return current currency code
     */
    String getCurrencyCode();


    /**
     * Get the last modified date.
     *
     * @return last modified date.
     */
    Date getModifiedDate();


    /**
     * Get customer name.
     *
     * @return customer name or null if customer is anonymous
     */
    String getCustomerName();


    /**
     * Get customer email.
     *
     * @return customer name or null if customer is anonymous
     */
    String getCustomerEmail();


    /**
     * Is billing address different from shipping adress.
     *
     * @return true is billing and shipping address are different.
     */
    boolean isSeparateBillingAddress();

    /**
     * Get carrier shipping SLA.
     *
     * @return carries sla id.
     */
    Integer getCarrierSlaId();

    /**
     * Get order message.
     *
     * @return order message
     */
    String getOrderMessage();

    /**
     * Get logon state.
     *
     * @return Logon state
     */
    int getLogonState();


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
     * Accept shopping cart visitor that potentially will make modifications
     * to the cart.
     *
     * @param command the modification visitor
     * @return true in case if command was accepted
     */
    boolean accept(final ShoppingCartCommand command);

    /**
     * Get shopping context
     *
     * @return instance of {@link ShoppingContext}
     */
    ShoppingContext getShoppingContext();

    /**
     * GEt order info.
     *
     * @return order infornmation.
     */
    OrderInfo getOrderInfo();

    /**
     * Get current shopping cart locale. Preferred to work with generic locale -
     * en instead of en_US, etc.
     * @return   current locale
     */
    String getCurrentLocale();


    /**
     * Get date when this cart was processed through cycle.
     * @return date.
     */
    Date getProcessingStartDate();

    /**
     * Set processing start date.
     * @param processingStartDate start date.
     */
    void setProcessingStartDate(Date processingStartDate);




    /**
     * Get cart sub total without any tax calculation.
     *
     * @return total amount for all items in the shopping cart.
     */
    BigDecimal getCartSubTotal();

    /**
     * Calculate taxes and amount withing current cart shopping context.
     * @param items items to perform calculation on.
     * @param orderDelivery optional delivery
     * @return {@link AmountCalculationResult} calculation result.
     */
    AmountCalculationResult getCartSubTotal(List<? extends CartItem> items, CustomerOrderDelivery orderDelivery);



}
