/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 13-10-18
 * Time: 5:04 PM
 */
public interface SkuPriceRule extends Auditable, Codable, Taggable, Rankable, Nameable {

    String ACTION_SKIP               = "S";
    String ACTION_PRICE_UPON_REQUEST = "R";
    String ACTION_PRICE_CALCULATE    = "C";

    /**
     * @return sku price rule PK
     */
    long getSkuPriceRuleId();

    /**
     * @param skuPriceRuleId sku price rule PK
     */
    void setSkuPriceRuleId(long skuPriceRuleId);

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
     * @return rule action
     */
    String getRuleAction();

    /**
     * @param ruleAction rule action
     */
    void setRuleAction(String ruleAction);

    /**
     * Margin percent context (can be positive for margin, negative for discount or zero).
     *
     * @return context object
     */
    BigDecimal getMarginPercent();

    /**
     * Margin percent context.
     *
     * @param marginPercent margin percent context
     */
    void setMarginPercent(BigDecimal marginPercent);

    /**
     * Margin amount context (can be positive for margin, negative for discount or zero).
     *
     * @return context object
     */
    BigDecimal getMarginAmount();

    /**
     * Margin percent context.
     *
     * @param marginAmount margin amount context
     */
    void setMarginAmount(BigDecimal marginAmount);

    /**
     * Rounding unit context. Rounding unit specifies the commercial rounding for the generated price. Default rounding
     * is “0.01”, i.e. to one cent. For whole prices “1.00” can be used. For nearest 5 cents “0.05” and so on.
     *
     * @return context object
     */
    BigDecimal getRoundingUnit();

    /**
     * Rounding unit context.
     *
     * @param roundingUnit rounding unit context
     */
    void setRoundingUnit(BigDecimal roundingUnit);

    /**
     * Price rule engine will use this expression and inject corresponding
     * context object which will be checked against this condition.
     *
     * @return eligibility condition expression
     */
    String getEligibilityCondition();

    /**
     * @param eligibilityCondition eligibility condition expression
     */
    void setEligibilityCondition(String eligibilityCondition);

    /**
     * Optional price tag to add to generated price.
     *
     * @return price tag
     */
    String getPriceTag();

    /**
     * Set price tag to add.
     *
     * @param priceTag price tag
     */
    void setPriceTag(String priceTag);

    /**
     * Optional price ref to add to generated price.
     *
     * @return price ref
     */
    String getPriceRef();

    /**
     * Set price ref to add.
     *
     * @param priceRef price ref
     */
    void setPriceRef(String priceRef);

    /**
     * @return default description for this rule
     */
    String getDescription();

    /**
     * @param description default description for this rule
     */
    void setDescription(String description);

    /**
     * @return on/off switch
     */
    boolean isEnabled();

    /**
     * @param enabled on/off switch
     */
    void setEnabled(boolean enabled);


}
