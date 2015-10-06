package org.yes.cart.domain.entity;

import org.yes.cart.domain.i18n.I18NModel;

import java.io.Serializable;
import java.util.Date;

/**
 * User: denispavlov
 * Date: 06/10/2015
 * Time: 11:11
 */
public interface ProductPromotionModel extends Serializable {

    /**
     * @return promo code
     */
    String getCode();

    /**
     * @return get coupon code (or null if not a coupon promo)
     */
    String getCouponCode();

    /**
     * @return type as specified in {@link Promotion}
     */
    String getType();

    /**
     * @return action as specified in {@link Promotion}
     */
    String getAction();

    /**
     * @return context parameter for promo action
     */
    String getContext();

    /**
     * @return promotion name
     */
    I18NModel getName();

    /**
     * @return promotion description
     */
    I18NModel getDescription();

    /**
     * @return active from date
     */
    Date getActiveFrom();

    /**
     * @return active to date
     */
    Date getActiveTo();

}
