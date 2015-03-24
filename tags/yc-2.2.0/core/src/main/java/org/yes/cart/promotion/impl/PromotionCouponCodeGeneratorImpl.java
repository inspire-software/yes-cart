package org.yes.cart.promotion.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.yes.cart.promotion.PromotionCouponCodeGenerator;

/**
 * User: denispavlov
 * Date: 06/06/2014
 * Time: 10:11
 */
public class PromotionCouponCodeGeneratorImpl implements PromotionCouponCodeGenerator {

    private final int length;

    public PromotionCouponCodeGeneratorImpl(final int length) {
        this.length = length;
    }

    /** {@inheritDoc} */
    public String generate() {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
