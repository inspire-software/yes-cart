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

package org.yes.cart.promotion.impl;

import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.promotion.PromotionCondition;
import org.yes.cart.promotion.PromotionConditionParser;
import org.yes.cart.util.ShopCodeContext;

/**
 * Groovy backed promotion parser. promotion.getEligibilityCondition() is assumed to be
 * groovy script. The
 *
 * User: denispavlov
 * Date: 13-10-28
 * Time: 8:47 AM
 */
public class GroovyPromotionConditionParser implements PromotionConditionParser {

    private final GroovyClassLoader gcl = new GroovyClassLoader();

    /** {@inheritDoc} */
    @Cacheable(value = "promotionService-groovyCache", key = "#promotion.promotionId")
    public PromotionCondition parse(final Promotion promotion) {

        PromotionCondition condition;

        try {
            final Class cl = parseGroovyCondition(promotion.getPromotionId(),
                                              promotion.getCode(),
                                              promotion.getEligibilityCondition());

            condition = (PromotionCondition) cl.newInstance();
        } catch (Exception exp) {
            ShopCodeContext.getLog(this).error("Unable to create class for promo: " + promotion.getCode(), exp);
            condition = new NullPromotionCondition(promotion.getPromotionId(), promotion.getCode());
        }

        return condition;
    }

    Class parseGroovyCondition(final long promoId, final String promoCode, final String condition) {

        final StringBuilder script = new StringBuilder();

        appendImports(script);
        appendStartClass(script, promoId, promoCode);
        appendBody(script, condition);
        appendEndClass(script);

        return gcl.parseClass(script.toString());

    }

    String clensePromoCode(final String promoCode) {
        return promoCode.replaceAll("([^a-zA-Z0-9])", "_");
    }

    /*
     * Declare all necessary imports to compile groovy class
     */
    void appendImports(final StringBuilder script) {
        script.append("import org.yes.cart.promotion.PromotionCondition;\n");
    }

    /*
     * Declare PromotionCondition class and assign top level variables to be available in
     * eligibility conditions.
     *
     * shoppingCart = context.shoppingCart;
     * shoppingCartItem = context.shoppingCartItem;
     * order = context.order;
     */
    void appendStartClass(final StringBuilder script, final long promoId, final String promoCode) {
        script.append("public class Promotion").append(clensePromoCode(promoCode)).append(" implements PromotionCondition {\n");
        script.append("public long getPromotionId() {\n");
        script.append("return ").append(promoId).append(";\n");
        script.append("}\n");
        script.append("public String getPromotionCode() {\n");
        script.append("return '").append(promoCode).append("';\n");
        script.append("}\n");
        script.append("public boolean isEligible(Map<String, Object> context) {\n");
        script.append("def registered = context.registered;\n");
        script.append("def customer = context.customer;\n");
        script.append("def customerTags = context.customerTags;\n");
        script.append("def shoppingCart = context.shoppingCart;\n");
        script.append("def shoppingCartItem = context.shoppingCartItem;\n");
        script.append("def shoppingCartItemTotal = context.shoppingCartItemTotal;\n");
        script.append("def shoppingCartOrderTotal = context.shoppingCartOrderTotal;\n");
    }

    /*
     * Append eligibility condition as body of the method. The return statement is assumed to
     * be inside the eligibility condition
     */
    void appendBody(final StringBuilder script, final String body) {
        if (StringUtils.isBlank(body)) {
            script.append("return true;\n");
        } else {
            script.append(body);
        }
    }

    /*
     * Closing brackets.
     */
    void appendEndClass(final StringBuilder script) {
        script.append("}\n}");
    }

}
