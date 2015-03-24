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

package org.yes.cart.promotion;

import java.io.Serializable;
import java.util.Map;

/**
 * Promotion condition is object that is capable of evaluation original promotion
 * eligibility condition into executable function.
 *
 * User: denispavlov
 * Date: 13-10-19
 * Time: 1:51 PM
 */
public interface PromotionCondition extends Serializable {

    String VAR_ACTION_CONTEXT = "actionContext";
    String VAR_PROMOTION = "promotion";
    String VAR_PROMOTION_CODE = "promotionCode";
    String VAR_REGISTERED = "registered";
    String VAR_CUSTOMER = "customer";
    String VAR_CUSTOMER_TAGS = "customerTags";
    String VAR_CART = "shoppingCart";
    String VAR_CART_ITEM = "shoppingCartItem";
    String VAR_SHIPPING = "shipping";
    String VAR_CART_ITEM_TOTAL = "shoppingCartItemTotal";
    String VAR_CART_ORDER_TOTAL = "shoppingCartOrderTotal";
    String VAR_TMP_TOTAL = "tmpTotal";

    /**
     * @return PK of promotion for this condition
     */
    long getPromotionId();

    /**
     * @return promotion code for this condition
     */
    String getPromotionCode();

    /**
     * Evaluate eligibility condition on given context objects.
     *
     * @param context context object
     *
     * @return true if this context satisfied the condition
     */
    boolean isEligible(Map<String, Object> context);

}
