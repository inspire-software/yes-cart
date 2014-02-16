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

import org.yes.cart.promotion.PromotionCondition;
import org.yes.cart.shoppingcart.CartItem;

import java.util.Map;

/**
 * Template for item level promotions.
 *
 * User: denispavlov
 * Date: 13-10-31
 * Time: 8:11 AM
 */
public abstract class AbstractItemPromotionAction extends AbstractPromotionAction {

    /**
     * Get promotion action context variable.
     *
     * @param context evaluation context
     *
     * @return action context string
     */
    protected CartItem getShoppingCartItem(final Map<String, Object> context) {
        return (CartItem) context.get(PromotionCondition.VAR_CART_ITEM);
    }

}
