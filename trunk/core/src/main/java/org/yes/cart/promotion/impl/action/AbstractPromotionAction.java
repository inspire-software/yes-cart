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

package org.yes.cart.promotion.impl.action;

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.promotion.PromotionAction;
import org.yes.cart.promotion.PromotionCondition;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.List;
import java.util.Map;

/**
 * Template for all promotion actions.
 *
 * User: denispavlov
 * Date: 13-10-31
 * Time: 8:05 AM
 */
public abstract class AbstractPromotionAction implements PromotionAction {


    /**
     * Get promotion action context variable.
     *
     * @param context evaluation context
     *
     * @return action context string
     */
    protected String getRawPromotionActionContext(final Map<String, Object> context) {
        return (String) context.get(PromotionCondition.VAR_ACTION_CONTEXT);
    }

    /**
     * Get current shopping cart.
     *
     * @param context evaluation context
     *
     * @return shopping cart
     */
    protected ShoppingCart getShoppingCart(final Map<String, Object> context) {
        return (ShoppingCart) context.get(PromotionCondition.VAR_CART);
    }

    /**
     * Get promotion object.
     *
     * @param context evaluation context
     *
     * @return promotion
     */
    protected Promotion getPromotion(final Map<String, Object> context) {
        return (Promotion) context.get(PromotionCondition.VAR_PROMOTION);
    }

    /**
     * Get customer object.
     *
     * @param context evaluation context
     *
     * @return customer
     */
    protected Customer getCustomer(final Map<String, Object> context) {
        return (Customer) context.get(PromotionCondition.VAR_CUSTOMER);
    }

    /**
     * Get customer tags.
     *
     * @param context evaluation context
     *
     * @return customer tags
     */
    protected List<String> getCustomerTags(final Map<String, Object> context) {
        return (List<String>) context.get(PromotionCondition.VAR_CUSTOMER_TAGS);
    }

}
