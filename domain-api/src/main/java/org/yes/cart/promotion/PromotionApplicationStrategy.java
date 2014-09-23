package org.yes.cart.promotion;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 06/06/2014
 * Time: 08:58
 */
public interface PromotionApplicationStrategy {

    /**
     * Test all applicable promotions for best cumulative discount value and apply it
     * to given context.
     *
     * @param promoBuckets promotion buckets containing groups of promotions that can be
     *                     combined
     * @param context all variables necessary for the application of promotions.
     */
    void applyPromotions(List<List<PromoTriplet>> promoBuckets,
                         Map<String, Object> context);

}
