package org.yes.cart.promotion;

/**
 * User: denispavlov
 * Date: 06/06/2014
 * Time: 10:07
 */
public interface PromotionCouponCodeGenerator {

    /**
     * @return generate a random coupon code (must check for uniqueness)
     */
    String generate();

}
