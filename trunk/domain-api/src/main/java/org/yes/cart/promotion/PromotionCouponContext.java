package org.yes.cart.promotion;

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;

/**
 * Coupons are very specific and hence require separate context which work "backwards"
 * by looking up the coupon first and then determining if promotion applied.
 *
 * This is reverse of the process that promotion engine uses which gets all active
 * promotions and then evaluates them.
 *
 * User: denispavlov
 * Date: 05/06/2014
 * Time: 15:26
 */
public interface PromotionCouponContext {

    /**
     * Apply coupon promotion.
     *
     * @param customer customer
     * @param cart cart
     * @param orderTotal current total after order and shipping
     *
     * @return  order total (does not include shipping promotions)
     */
    Total applyCouponPromo(Customer customer, ShoppingCart cart, Total orderTotal);

}
