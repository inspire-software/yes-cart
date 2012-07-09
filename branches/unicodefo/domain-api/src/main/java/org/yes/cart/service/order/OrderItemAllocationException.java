package org.yes.cart.service.order;

import org.yes.cart.domain.entity.ProductSku;

import java.math.BigDecimal;

/**
 * Will be thrown when items quantity can not be allocated.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 4/21/12
 * Time: 8:53 PM
 */
public class OrderItemAllocationException extends OrderException {

    private final ProductSku productSku;

    private final  BigDecimal quantity;


    /**
     *
     * @param productSku sku code, which can not be allocated.
     * @param quantity which can not be allocated.
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     */
    public OrderItemAllocationException(final  ProductSku productSku, final  BigDecimal quantity, final String message) {
        super(message);
        this.productSku = productSku;
        this.quantity = quantity;
    }

    /**
     *
     * @return product sku code
     */
    public ProductSku getProductSku() {
        return productSku;
    }

    /**
     * Get quantity which can not be allocated.
     * Two will be returned in case if shopper has 10 items in cart, but only 8 items available
     * @return quantity which can not be allocated.
     */
    public BigDecimal getQuantity() {
        return quantity;
    }
}
