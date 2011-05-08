package org.yes.cart.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Cart item object that hold information about a single shopping item.
 * <p/>
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 10:25:01 PM
 */
public interface CartItem extends Serializable {

    /**
     * @return product sku code.
     */
    String getProductSkuCode();

    /**
     * @return quantity of the above sku to be purchased
     */
    BigDecimal getQty();

    /**
     * Get the sku price price.
     *
     * @return sku price.
     */
    BigDecimal getPrice();

}