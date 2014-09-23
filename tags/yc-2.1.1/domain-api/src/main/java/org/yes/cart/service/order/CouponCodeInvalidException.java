package org.yes.cart.service.order;

/**
 * User: denispavlov
 * Date: 05/06/2014
 * Time: 18:37
 */
public class CouponCodeInvalidException extends OrderAssemblyException {

    private final String coupon;

    public CouponCodeInvalidException(final String coupon) {
        super("Coupon invalid");
        this.coupon = coupon;
    }

    public String getCoupon() {
        return coupon;
    }
}
