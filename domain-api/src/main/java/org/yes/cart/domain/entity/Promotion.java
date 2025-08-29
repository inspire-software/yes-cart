/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.domain.entity;

import org.yes.cart.domain.i18n.I18NModel;

import java.time.LocalDateTime;

/**
 * User: denispavlov
 * Date: 13-10-18
 * Time: 5:04 PM
 */
public interface Promotion extends Auditable, Codable, Taggable, Rankable, Nameable {

    String TYPE_ORDER           = "O";
    String TYPE_SHIPPING        = "S";
    String TYPE_ITEM            = "I";
    String TYPE_CUSTOMER_TAG    = "C";

    String ACTION_FIXED_AMOUNT_OFF           = "F";
    String ACTION_PERCENT_DISCOUNT           = "P";
    String ACTION_PERCENT_DISCOUNT_NON_SALE  = "S";
    String ACTION_GIFT                       = "G";
    String ACTION_TAG                        = "T";
    String ACTION_FIXED_SURCHARGE            = "C";

    /**
     * @return promotion PK
     */
    long getPromotionId();

    /**
     * @param promotionId promotion PK
     */
    void setPromotionId(long promotionId);

    /**
     * @return shop code
     */
    String getShopCode();

    /**
     * @param shopCode shop code
     */
    void setShopCode(String shopCode);

    /**
     * @return currency
     */
    String getCurrency();

    /**
     * @param currency currency
     */
    void setCurrency(String currency);

    /**
     * @return promotion type
     */
    String getPromoType();

    /**
     * @param promoType promo type
     */
    void setPromoType(String promoType);

    /**
     * @return promo action
     */
    String getPromoAction();

    /**
     * @param promoAction promo action
     */
    void setPromoAction(String promoAction);

    /**
     * Context object that is applicable to given promo action.
     * This can be:
     * - Fixed amount off in a given currency
     * - Percentage for percentage discount
     * - Tags to be applied
     * - SkuCode of gift to be applied
     *
     * Each context has its own scope of function:
     * - item context only affects final price per unit
     * - order context only affects final sub total
     * - shipping context only affects shipping cost
     *
     * E.g. item gift - will be applied for each
     *
     * @return context object
     */
    String getPromoActionContext();

    /**
     * @param promoActionContext promotion context
     */
    void setPromoActionContext(String promoActionContext);

    /**
     * Eligibility condition context depends on the promo type.
     * Promotion engine will use this expression and inject corresponding
     * context object which will be checked against this condition.
     *
     * E.g. customer tagging promotion will inject customer as root
     * object of the expression and thus will make all properties available
     * to be checked. If the condition is satisfied tagging action
     * will be performed.
     *
     * @return eligibility condition expression
     */
    String getEligibilityCondition();

    /**
     * @param eligibilityCondition eligibility condition expression
     */
    void setEligibilityCondition(String eligibilityCondition);

    /**
     * @return display name for this promo
     */
    I18NModel getDisplayName();

    /**
     * @param displayName display name for this promo
     */
    void setDisplayName(I18NModel displayName);

    /**
     * @return default description for this promo
     */
    String getDescription();

    /**
     * @param description default description for this promo
     */
    void setDescription(String description);

    /**
     * @return display description for this promo
     */
    I18NModel getDisplayDescription();

    /**
     * @param displayDescription display description for this promo
     */
    void setDisplayDescription(I18NModel displayDescription);

    /**
     * @return flag that denotes if this promotion is triggered by
     *         a coupon code.
     */
    boolean isCouponTriggered();

    /**
     * @param couponTriggered flag that denotes if this promotion is
     *                        triggered by a coupon code.
     */
    void setCouponTriggered(boolean couponTriggered);

    /**
     * @return flag that denotes if this promotion can be applied
     *         in conjunction with other "canBeCombined" promotions.
     */
    boolean isCanBeCombined();

    /**
     * @param canBeCombined flag that denotes if this promotion can
     *                      be applied in conjunction with other
     *                      "canBeCombined" promotions.
     */
    void setCanBeCombined(boolean canBeCombined);

    /**
     * @return on/off switch
     */
    boolean isEnabled();

    /**
     * @param enabled on/off switch
     */
    void setEnabled(boolean enabled);

    /**
     * @return promotion start time
     */
    LocalDateTime getEnabledFrom();

    /**
     * @param enabledFrom promotion start time
     */
    void setEnabledFrom(LocalDateTime enabledFrom);

    /**
     * @return promotion finish time
     */
    LocalDateTime getEnabledTo();

    /**
     * @param enabledTo promotion finish time
     */
    void setEnabledTo(LocalDateTime enabledTo);


    /**
     * Returns true if promotion is enabled and now is within from/to date range.
     *
     * @param now    time now
     *
     * @return true if the product is available now
     */
    boolean isAvailable(LocalDateTime now);

}
