/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import java.util.Date;

/**
 * User: denispavlov
 * Date: 13-10-18
 * Time: 5:04 PM
 */
public interface Promotion extends Auditable, Codable, Taggable {

    String TYPE_ORDER           = "O";
    String TYPE_SHIPPING        = "S";
    String TYPE_ITEM            = "I";
    String TYPE_CUSTOMER_TAG    = "C";

    String ACTION_FIXED_AMOUNT_OFF  = "F";
    String ACTION_PERCENT_DISCOUNT  = "P";
    String ACTION_GIFT              = "G";
    String ACTION_TAG               = "T";

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
     * @return default name for this promo
     */
    String getName();

    /**
     * @param name default name for this promo
     */
    void setName(String name);

    /**
     * @return display name for this promo
     */
    String getDisplayName();

    /**
     * @param displayName display name for this promo
     */
    void setDisplayName(String displayName);

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
    String getDisplayDescription();

    /**
     * @param displayDescription display description for this promo
     */
    void setDisplayDescription(String displayDescription);

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
    Date getEnabledFrom();

    /**
     * @param enabledFrom promotion start time
     */
    void setEnabledFrom(Date enabledFrom);

    /**
     * @return promotion finish time
     */
    Date getEnabledTo();

    /**
     * @param enabledTo promotion finish time
     */
    void setEnabledTo(Date enabledTo);


    /**
     * Get promotion rank when combined.
     *
     * @return promotion rank.
     */
    int getRank();

    /**
     * Set promotion rank.
     *
     * @param rank promotion rank
     */
    void setRank(int rank);


}
