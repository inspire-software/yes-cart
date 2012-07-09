package org.yes.cart.shoppingcart;

import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.Shop;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 *
 * This strategy responsible for following kind of calculation:
 *
 * Cart sub total
 * Shipping amount
 * Cart Total
 * Taxes
 *
 * Calculation may varying in different countries and counties because of
 * different regulation policy.
 *
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 27/11/11
 * Time: 18:57
 */
public interface AmountCalculationStrategy {


    /**
     * Get tax value.
     * @return tax value.
     */
    BigDecimal getTax();

    /**
     * Is tax included in items prices .
     * @return true in case if price included in item price, otherwise false.
     */
    boolean isTaxIncluded();


    /**
     * Calculate shopping cart amount.
     * @param shoppingContext  current {@link ShoppingContext}
     * @param orderDelivery  {@link CustomerOrderDelivery}
     * @return {@link AmountCalculationResult}
     */
    AmountCalculationResult calculate(ShoppingContext shoppingContext,
                                      CustomerOrderDelivery orderDelivery);

    /**
     * Calculate shopping cart amount.
     * @param shoppingContext  current {@link ShoppingContext}
     * @param orderDelivery list of {@link CustomerOrderDelivery}
     * @return {@link AmountCalculationResult}
     */
    AmountCalculationResult calculate(ShoppingContext shoppingContext,
                                      Collection<CustomerOrderDelivery> orderDelivery);


    /**
     * Calculate sub total of shopping cart by given list of {@link CartItem}.
     *
     * @param items given list of cart items.
     * @return cart sub total.
     */
    BigDecimal calculateSubTotal(List<CartItem> items);

}
