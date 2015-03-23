package org.yes.cart.promotion;

import org.yes.cart.domain.entity.Promotion;

import java.io.Serializable;

/**
 * Triplet that unites three parts of the promotion processing.
 *
 * User: denispavlov
 * Date: 06/06/2014
 * Time: 08:56
 */
public interface PromoTriplet extends Serializable {

    /**
     * @return promotion
     */
    Promotion getPromotion();

    /**
     * @return condition
     */
    PromotionCondition getCondition();

    /**
     * @return action
     */
    PromotionAction getAction();
}
