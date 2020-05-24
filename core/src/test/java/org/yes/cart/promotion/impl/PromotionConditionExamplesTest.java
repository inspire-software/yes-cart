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

package org.yes.cart.promotion.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.promotion.PromotionCondition;
import org.yes.cart.promotion.PromotionConditionParser;
import org.yes.cart.promotion.PromotionConditionSupport;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 01/03/2018
 * Time: 18:29
 */
public class PromotionConditionExamplesTest extends BaseCoreDBTestCase {

    @Test
    public void testExamples() throws Exception {

        final PromotionConditionParser parser = ctx().getBean("promotionConditionParser", PromotionConditionParser.class);

        final Customer customer = createCustomer();
        final ShoppingCart cart = getShoppingCart(false);


        // Registered check example

        final Promotion registeredPositive = createPromotion(77000L, "REGISTERED", "registered");
        final PromotionCondition registeredPositiveCondition = parser.parse(registeredPositive);
        assertTrue(registeredPositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion registeredNegative = createPromotion(77001L, "NOT-REGISTERED", "!registered");
        final PromotionCondition registeredNegativeCondition = parser.parse(registeredNegative);
        assertFalse(registeredNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        // Customer tags example

        final Promotion tagsPositive = createPromotion(77010L, "TAGS", "customerTags.contains('testtag')");
        final PromotionCondition tagsPositiveCondition = parser.parse(tagsPositive);
        assertTrue(tagsPositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion tagsNegative = createPromotion(77011L, "NO-TAGS", "customerTags.contains('othertag')");
        final PromotionCondition tagsNegativeCondition = parser.parse(tagsNegative);
        assertFalse(tagsNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        // Customer type example

        final Promotion typePositive = createPromotion(77020L, "TYPE", "customerType == 'B2C'");
        final PromotionCondition typePositiveCondition = parser.parse(typePositive);
        assertTrue(typePositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion typeNegative = createPromotion(77021L, "NO-TYPE", "customerType != 'B2C'");
        final PromotionCondition typeNegativeCondition = parser.parse(typeNegative);
        assertFalse(typeNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        // Customer pricing policy example

        final Promotion policyPositive = createPromotion(77030L, "POLICY", "pricingPolicy.contains('P1')");
        final PromotionCondition policyPositiveCondition = parser.parse(policyPositive);
        assertTrue(policyPositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion policyNegative = createPromotion(77031L, "NO-POLICY", "pricingPolicy.contains('P2')");
        final PromotionCondition policyNegativeCondition = parser.parse(policyNegative);
        assertFalse(policyNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        // Cart total example

        final Promotion totalIncTaxPositive = createPromotion(77040L, "GROSS-TOTAL", "shoppingCartOrderTotal.subTotalAmount > 5000.00");
        final PromotionCondition totalIncTaxPositiveCondition = parser.parse(totalIncTaxPositive);
        assertTrue(totalIncTaxPositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion totalIncTaxNegative = createPromotion(77041L, "NO-GROSS-TOTAL", "shoppingCartOrderTotal.subTotalAmount > 20000.00");
        final PromotionCondition totalIncTaxNegativeCondition = parser.parse(totalIncTaxNegative);
        assertFalse(totalIncTaxNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion totalExclTaxPositive = createPromotion(77042L, "NET-TOTAL", "(shoppingCartOrderTotal.subTotalAmount - shoppingCartOrderTotal.subTotalTax) > 4000.00");
        final PromotionCondition totalExclTaxPositiveCondition = parser.parse(totalExclTaxPositive);
        assertTrue(totalExclTaxPositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion totalExclTaxNegative = createPromotion(77043L, "NO-NET-TOTAL", "(shoppingCartOrderTotal.subTotalAmount - shoppingCartOrderTotal.subTotalTax) > 5000.00");
        final PromotionCondition totalExclTaxNegativeCondition = parser.parse(totalExclTaxNegative);
        assertFalse(totalExclTaxNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        // Product example

        final Promotion productShippablePositive = createPromotion(77050L, "SHIPPABLE", "product(SKU).producttype.shippable");
        final PromotionCondition productShippablePositiveCondition = parser.parse(productShippablePositive);
        assertTrue(productShippablePositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion productDigitalNegative = createPromotion(77051L, "NO-DIGITAL", "product(SKU).producttype.digital");
        final PromotionCondition productDigitalNegativeCondition = parser.parse(productDigitalNegative);
        assertFalse(productDigitalNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        // Brand example

        final Promotion brandNameExactPositive = createPromotion(77060L, "BRAND", "brand(SKU).name == 'cc tests'");
        final PromotionCondition brandNameExactPositiveCondition = parser.parse(brandNameExactPositive);
        assertTrue(brandNameExactPositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion brandNameExactDigitalNegative = createPromotion(77061L, "NO-BRAND", "brand(SKU).name == 'CC tests'");
        final PromotionCondition brandNameExactDigitalNegativeCondition = parser.parse(brandNameExactDigitalNegative);
        assertFalse(brandNameExactDigitalNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion brandNameIlikePositive = createPromotion(77062L, "IS-BRAND", "isSKUofBrand(SKU, 'CC tests', 'Samsung')");
        final PromotionCondition brandNameIlikePositiveCondition = parser.parse(brandNameIlikePositive);
        assertTrue(brandNameIlikePositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion brandNameIlikeDigitalNegative = createPromotion(77063L, "NO-IS-BRAND", "isSKUofBrand(SKU, 'Sony', 'Samsung')");
        final PromotionCondition brandNameIlikeDigitalNegativeCondition = parser.parse(brandNameIlikeDigitalNegative);
        assertFalse(brandNameIlikeDigitalNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        // Category example

        final Promotion categoryCodeIlikePositive = createPromotion(77070L, "IS-CATEGORY", "isSKUinCategory(SKU, '313', '304')");
        final PromotionCondition categoryCodeIlikePositiveCondition = parser.parse(categoryCodeIlikePositive);
        assertTrue(categoryCodeIlikePositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion categoryCodeIlikeNegative = createPromotion(77071L, "NO-IS-CATEGORY", "isSKUinCategory(SKU, '304', '302')");
        final PromotionCondition categoryCodeIlikeNegativeCondition = parser.parse(categoryCodeIlikeNegative);
        assertFalse(categoryCodeIlikeNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        // Check product attribute value example

        final Promotion hasAttributePositive = createPromotion(77080L, "HAS-ATTRIBUTE", "hasProductAttribute('BENDER-ua', 'WEIGHT')");
        final PromotionCondition hasAttributePositiveCondition = parser.parse(hasAttributePositive);
        assertTrue(hasAttributePositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion hasAttributeNegative = createPromotion(77081L, "NO-HAS-ATTRIBUTE", "hasProductAttribute('BENDER-ua', 'INEXISTENT')");
        final PromotionCondition hasAttributeNegativeCondition = parser.parse(hasAttributeNegative);
        assertFalse(hasAttributeNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion hasAttributeSkuPositive = createPromotion(77082L, "SKU-ATTRIBUTE", "productAttributeValue('BENDER-ua', 'WEIGHT') == '1.16'");
        final PromotionCondition hasAttributeSkuPositiveCondition = parser.parse(hasAttributeSkuPositive);
        assertTrue(hasAttributeSkuPositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion hasAttributeSkuNegative = createPromotion(77083L, "NO-SKU-ATTRIBUTE", "productAttributeValue('BENDER-ua', 'WEIGHT') == '1.15'");
        final PromotionCondition hasAttributeSkuNegativeCondition = parser.parse(hasAttributeSkuNegative);
        assertFalse(hasAttributeSkuNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion hasAttributeProdPositive = createPromotion(77082L, "PROD-ATTRIBUTE", "productAttributeValue('BENDER', 'WEIGHT') == '1.11'");
        final PromotionCondition hasAttributeProdPositiveCondition = parser.parse(hasAttributeProdPositive);
        assertTrue(hasAttributeProdPositiveCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));

        final Promotion hasAttributeProdNegative = createPromotion(77083L, "NO-PROD-ATTRIBUTE", "productAttributeValue('BENDER', 'WEIGHT') == '1.15'");
        final PromotionCondition hasAttributeProdNegativeCondition = parser.parse(hasAttributeProdNegative);
        assertFalse(hasAttributeProdNegativeCondition.isEligible(prepareContext(customer, cart, cart.getCartItemList().get(0))));


    }

    private Map<String, Object> prepareContext(final Customer customer,
                                               final ShoppingCart cart,
                                               final CartItem item) {
        return prepareContext(customer, "testtag", "B2C","P1", cart, item);
    }


    private Map<String, Object> prepareContext(final Customer customer,
                                               final String customerTag,
                                               final String customerType,
                                               final String pricingPolicy,
                                               final ShoppingCart cart,
                                               final CartItem item) {

        final Map<String, Object> context = new HashMap<>();
        context.put(PromotionCondition.VAR_CONDITION_SUPPORT, ctx().getBean("promotionConditionSupport", PromotionConditionSupport.class));
        context.put(PromotionCondition.VAR_REGISTERED, customer != null && !customer.isGuest());
        context.put(PromotionCondition.VAR_CUSTOMER, customer);
        context.put(PromotionCondition.VAR_CUSTOMER_TAGS, StringUtils.isNotBlank(customerTag) ? Collections.singletonList(customerTag) : Collections.emptyList());
        context.put(PromotionCondition.VAR_CUSTOMER_TYPE, customerType);
        context.put(PromotionCondition.VAR_CUSTOMER_PRICING_POLICY, StringUtils.isNotBlank(pricingPolicy) ? Collections.singletonList(pricingPolicy) : Collections.emptyList());
        context.put(PromotionCondition.VAR_CART, cart);
        context.put(PromotionCondition.VAR_CART_ORDER_TOTAL, cart.getTotal());
        context.put(PromotionCondition.VAR_CART_ITEM, item);
        return context;

    }

    private Promotion createPromotion(final long id, final String code, final String condition) {

        final Promotion promotion = ctx().getBean("promotionDao", GenericDAO.class).getEntityFactory().getByIface(Promotion.class);
        promotion.setPromotionId(id);
        promotion.setCode(code);
        promotion.setEligibilityCondition(condition);
        return promotion;

    }

}
