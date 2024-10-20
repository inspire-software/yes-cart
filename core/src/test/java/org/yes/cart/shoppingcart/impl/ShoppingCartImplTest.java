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

package org.yes.cart.shoppingcart.impl;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: dogma
 * Date: Jan 16, 2011
 * Time: 1:19:22 AM
 */
public class ShoppingCartImplTest {

    private Mockery mockery = new JUnit4Mockery();

    private ShoppingCartImpl cart = new ShoppingCartImpl();

    @Test
    public void testIndexOfSkuInexistent() {
        assertEquals("Size should be 0", 0, cart.getCartItemsCount());
        assertFalse("non-existent sku",
                cart.getCartItemList().stream().anyMatch(
                        item -> "sku".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
    }

    @Test
    public void testAddProductSkuDTOToCartInexistent() {
        boolean newItem = cart.addProductSkuToCart("s01","sku", "SKU name", BigDecimal.TEN, null, false, false);
        assertTrue("Must create new item", newItem);
        assertEquals("Size should be 10", 10, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertEquals("Items must have 1 element", 1, cart.getCartItemList().size());
        assertEquals("1st element must be sku", "sku", cart.getCartItemList().get(0).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuDTOToCartExistent() {
        boolean newItem1 = cart.addProductSkuToCart("s01", "sku", "SKU name", BigDecimal.TEN, null, false, false);
        boolean newItem2 = cart.addProductSkuToCart("s01", "sku", "SKU name", BigDecimal.TEN, null, false, false);
        assertTrue("Must create new item", newItem1);
        assertFalse("Must not create new item", newItem2);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertEquals("Items must have 1 element", 1, cart.getCartItemList().size());
        assertEquals("1st element must be sku", "sku", cart.getCartItemList().get(0).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuDTOToCartExistentDifferentGroups() {
        boolean newItem1 = cart.addProductSkuToCart("s01", "sku", "SKU name", BigDecimal.TEN, null, false, false);
        boolean newItem2 = cart.addProductSkuToCart("s01", "sku", "SKU name", BigDecimal.TEN, "grp1", false, false);
        assertTrue("Must create new item", newItem1);
        assertTrue("Must create new item", newItem2);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertEquals("Items must have 1 element", 2, cart.getCartItemList().size());
        assertEquals("1st element must be sku", "sku", cart.getCartItemList().get(0).getProductSkuCode());
        assertEquals("2nd element must be sku", "sku", cart.getCartItemList().get(1).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuDTOToCartExistentDifferentSupplier() {
        boolean newItem1 = cart.addProductSkuToCart("s01", "sku", "SKU name", BigDecimal.TEN, null, false, false);
        boolean newItem2 = cart.addProductSkuToCart("s02", "sku", "SKU name", BigDecimal.TEN, null, false, false);
        assertTrue("Must create new item", newItem1);
        assertTrue("Must create new item", newItem2);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku".equals(item.getProductSkuCode()) && "s02".equals(item.getSupplierCode())));
        assertEquals("Items must have 2 elements", 2, cart.getCartItemList().size());
        assertEquals("1st element must be sku", "sku", cart.getCartItemList().get(0).getProductSkuCode());
        assertEquals("2nd element must be sku", "sku", cart.getCartItemList().get(1).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuDTOToCartExistentDifferentSupplierDifferentGroup() {
        boolean newItem1 = cart.addProductSkuToCart("s01", "sku", "SKU name", BigDecimal.TEN, null, false, false);
        boolean newItem2 = cart.addProductSkuToCart("s02", "sku", "SKU name", BigDecimal.TEN, null, false, false);
        boolean newItem3 = cart.addProductSkuToCart("s02", "sku", "SKU name", BigDecimal.TEN, "grp1", false, false);
        assertTrue("Must create new item", newItem1);
        assertTrue("Must create new item", newItem2);
        assertTrue("Must create new item", newItem3);
        assertEquals("Size should be 20", 30, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku".equals(item.getProductSkuCode()) && "s02".equals(item.getSupplierCode())));
        assertEquals("Items must have 3 elements", 3, cart.getCartItemList().size());
        assertEquals("1st element must be sku", "sku", cart.getCartItemList().get(0).getProductSkuCode());
        assertEquals("2nd element must be sku", "sku", cart.getCartItemList().get(1).getProductSkuCode());
        assertEquals("3rd element must be sku", "sku", cart.getCartItemList().get(2).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuToCartExistentAndInexistent() {
        boolean newItem1 = cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        boolean newItem2 = cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        boolean newItem3 = cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, null, false, false);
        assertTrue("Must create new item", newItem1);
        assertFalse("Must not create new item", newItem2);
        assertTrue("Must create new item", newItem3);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku01".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku02".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertEquals("Items must have 2 elements", 2, cart.getCartItemList().size());
        assertEquals("1st element must be sku01", "sku01", cart.getCartItemList().get(0).getProductSkuCode());
        assertEquals("1st element must be sku02", "sku02", cart.getCartItemList().get(1).getProductSkuCode());
    }

    @Test
    public void testAddGiftToCart() throws Exception {
        boolean newItem1 = cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        boolean newItem2 = cart.addGiftToCart("s01", "sku01", "SKU name", BigDecimal.ONE, "TEST01");
        boolean newItem3 = cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, null, false, false);
        boolean newItem4 = cart.addGiftToCart("s01", "sku01", "SKU name", BigDecimal.ONE, "TEST02");
        assertTrue("Must create new item", newItem1);
        assertTrue("Must not create new item", newItem2);
        assertTrue("Must create new item", newItem3);
        assertFalse("Must not create new item", newItem4);
        assertEquals("Size should be 30", 22, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku01".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertEquals("Index must be 0 for sku01", "sku01", cart.getGifts().get(0).getProductSkuCode()); // gift index is separate from product
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku02".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertEquals("Items must have 2 elements", 3, cart.getCartItemList().size());
        assertEquals("1st element must be sku01", "sku01", cart.getCartItemList().get(0).getProductSkuCode());
        assertEquals("2nd element must be sku02", "sku02", cart.getCartItemList().get(1).getProductSkuCode());
        assertEquals("3rd element must be sku01", "sku01", cart.getCartItemList().get(2).getProductSkuCode()); // index = count(prod) + giftIndex
        assertTrue("3rd element must be gift", cart.getCartItemList().get(2).isGift());
        assertEquals("3rd element must have promos", "TEST01,TEST02", cart.getCartItemList().get(2).getAppliedPromo());
    }

    @Test
    public void testRemoveCartItemInexistent() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, null, false, false);
        boolean removed = cart.removeCartItem("s01", "sku03", null);
        assertFalse("Must not be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
    }

    @Test
    public void testRemoveCartItemExistent() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, null, false, false);
        boolean removed = cart.removeCartItem("s01", "sku02", null);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertFalse("sku removed", cart.getCartItemList().stream().anyMatch(
                item -> "sku02".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
    }

    @Test
    public void testRemoveCartItemExistentWithGroup() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, "grp1", false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, "grp1", false, false);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, "grp1", false, false);
        boolean removed = cart.removeCartItem("s01", "sku02", null);
        assertFalse("Must not be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
        removed = cart.removeCartItem("s01", "sku02", "grp1");
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertFalse("sku removed", cart.getCartItemList().stream().anyMatch(
                item -> "sku02".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
    }

    @Test
    public void testRemoveCartItemExistentWrongSupplier() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, null, false, false);
        boolean removed = cart.removeCartItem("s02", "sku02", null);
        assertFalse("Must be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku02".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
    }

    @Test
    public void testRemoveCartItemExistentWrongSupplierWithGroup() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, "grp1", false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, "grp1", false, false);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, "grp1", false, false);
        boolean removed = cart.removeCartItem("s02", "sku02", "grp1");
        assertFalse("Must be not removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku02".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
    }

    @Test
    public void testRemoveCartItemQuantityInexistent() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, null, false, false);
        boolean removed = cart.removeCartItemQuantity("s01", "sku03", BigDecimal.TEN, null);
        assertFalse("Must not be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
    }

    @Test
    public void testRemoveCartItemQuantityExistent() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, null, false, false);
        boolean removed = cart.removeCartItemQuantity("s01", "sku02", BigDecimal.ONE, null);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 29", 29, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku02".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertTrue("Quantity should change to 9", MoneyUtils.isFirstEqualToSecond(new BigDecimal(9), cart.getCartItemList().get(1).getQty()));
    }

    @Test
    public void testRemoveCartItemQuantityExistentWithGroup() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, "grp1", false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, "grp2", false, false);
        boolean removed = cart.removeCartItemQuantity("s01", "sku01", BigDecimal.ONE, null);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 29", 29, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku01".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertTrue("Quantity should change to 9", MoneyUtils.isFirstEqualToSecond(new BigDecimal(9), cart.getCartItemList().get(0).getQty()));
        removed = cart.removeCartItemQuantity("s01", "sku01", BigDecimal.ONE, "grp1");
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 28", 28, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku01".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertTrue("Quantity should change to 9", MoneyUtils.isFirstEqualToSecond(new BigDecimal(9), cart.getCartItemList().get(1).getQty()));
        removed = cart.removeCartItemQuantity("s01", "sku01", BigDecimal.ONE, "grp2");
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 27", 27, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku01".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
        assertTrue("Quantity should change to 9", MoneyUtils.isFirstEqualToSecond(new BigDecimal(9), cart.getCartItemList().get(2).getQty()));

    }

    @Test
    public void testRemoveCartItemQuantityExistentWrongSupplier() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, null, false, false);
        boolean removed = cart.removeCartItemQuantity("s02", "sku02", BigDecimal.ONE, null);
        assertFalse("Must not be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
        assertTrue("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku02".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
    }

    @Test
    public void testRemoveCartItemQuantityExistentFull() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, null, false, false);
        boolean removed = cart.removeCartItemQuantity("s01", "sku02", BigDecimal.TEN, null);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertFalse("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku02".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
    }

    @Test
    public void testRemoveCartItemQuantityExistentMoreThanInCart() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN, null, false, false);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN, null, false, false);
        boolean removed = cart.removeCartItemQuantity("s01", "sku02", new BigDecimal(100), null);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertFalse("sku exists", cart.getCartItemList().stream().anyMatch(
                item -> "sku02".equals(item.getProductSkuCode()) && "s01".equals(item.getSupplierCode())));
    }

    @Test
    public void testAddCoupons() throws Exception {

        cart.addCoupon("ABC");

        List<String> coupons, applied;

        coupons = cart.getCoupons();
        assertNotNull(coupons);
        assertEquals(1, coupons.size());
        assertEquals("ABC", coupons.get(0));

        applied = cart.getAppliedCoupons();
        assertNotNull(applied);
        assertEquals(0, applied.size());

        cart.addCoupon("CDE");

        coupons = cart.getCoupons();
        assertNotNull(coupons);
        assertEquals(2, coupons.size());
        assertEquals("ABC", coupons.get(0));
        assertEquals("CDE", coupons.get(1));

        applied = cart.getAppliedCoupons();
        assertNotNull(applied);
        assertEquals(0, applied.size());

        cart.addGiftToCart("s01", "gift-001", "SKU name", BigDecimal.ONE, "PROMO-001:ABC");

        applied = cart.getAppliedCoupons();
        assertNotNull(applied);
        assertEquals(1, applied.size());
        assertEquals("ABC", applied.get(0));

        cart.addGiftToCart("s01", "gift-002", "SKU name", BigDecimal.ONE, "PROMO-001:CDE");

        applied = cart.getAppliedCoupons();
        assertNotNull(applied);
        assertEquals(2, applied.size());
        assertEquals("ABC", applied.get(0));
        assertEquals("CDE", applied.get(1));

        cart.removeItemPromotions();

        coupons = cart.getCoupons();
        assertNotNull(coupons);
        assertEquals(2, coupons.size());
        assertEquals("ABC", coupons.get(0));
        assertEquals("CDE", coupons.get(1));

        applied = cart.getAppliedCoupons();
        assertNotNull(applied);
        assertEquals(0, applied.size());

    }

    @Test
    public void testModificaion() throws Exception {

        final AmountCalculationStrategy strategy = mockery.mock(AmountCalculationStrategy.class);

        cart.initialise(strategy);
        assertFalse(cart.isModified());
        cart.markDirty();
        assertTrue(cart.isModified());

    }

    /**
     * Test shows that only promotions are removed.
     */
    @Test
    public void testRemoveItemPromotions() throws Exception {

        cart.addProductSkuToCart("s01", "ABC001", "SKU name", BigDecimal.ONE, null, false, false);
        cart.setProductSkuPrice("s01", "ABC001", new BigDecimal("9.99"), new BigDecimal("10.99"));
        cart.setProductSkuPromotion("s01", "ABC001", new BigDecimal("5.99"), "-50");

        cart.addGiftToCart("s01", "G001", "SKU name", BigDecimal.ONE, "G001");
        cart.setGiftPrice("s01", "G001", new BigDecimal("4.99"), new BigDecimal("4.99"));

        cart.addProductSkuToCart("s01", "ABC002", "SKU name", BigDecimal.ONE, null, false, false);
        cart.setProductSkuPrice("s01", "ABC002", new BigDecimal("9.99"), new BigDecimal("10.99"));
        cart.setProductSkuOffer("s01", "ABC002", new BigDecimal("5.99"), null, "AUTH001");

        assertEquals(3, cart.getCartItemList().size());

        final CartItem abc001 = cart.getCartItemList().get(0);
        assertNotNull(abc001);
        assertFalse(abc001.isGift());
        assertTrue(abc001.isPromoApplied());
        assertFalse(abc001.isFixedPrice());
        assertEquals("-50", abc001.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc001.getPrice());

        final CartItem abc002 = cart.getCartItemList().get(1);
        assertNotNull(abc002);
        assertFalse(abc002.isGift());
        assertFalse(abc002.isPromoApplied());
        assertTrue(abc002.isFixedPrice());
        assertEquals("AUTH001", abc002.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc002.getPrice());

        final CartItem gift = cart.getCartItemList().get(2);
        assertNotNull(gift);
        assertTrue(gift.isGift());
        assertTrue(gift.isPromoApplied());
        assertFalse(gift.isFixedPrice());
        assertEquals("G001", gift.getAppliedPromo());
        assertEquals(new BigDecimal("0.00"), gift.getPrice());

        cart.removeItemPromotions();

        assertEquals(2, cart.getCartItemList().size());

        final CartItem abc001_2 = cart.getCartItemList().get(0);
        assertNotNull(abc001_2);
        assertFalse(abc001_2.isGift());
        assertFalse(abc001_2.isPromoApplied());
        assertFalse(abc001_2.isFixedPrice());
        assertNull(abc001_2.getAppliedPromo());
        assertEquals(new BigDecimal("9.99"), abc001_2.getPrice());

        final CartItem abc002_2 = cart.getCartItemList().get(1);
        assertNotNull(abc002_2);
        assertFalse(abc002_2.isGift());
        assertFalse(abc002_2.isPromoApplied());
        assertTrue(abc002_2.isFixedPrice());
        assertEquals("AUTH001", abc002_2.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc002_2.getPrice());


    }


    /**
     * Test shows that only offers are removed.
     */
    @Test
    public void testRemoveItemOffers() throws Exception {

        cart.addProductSkuToCart("s01", "ABC001", "SKU name", BigDecimal.ONE, null, false, false);
        cart.setProductSkuPrice("s01", "ABC001", new BigDecimal("9.99"), new BigDecimal("10.99"));
        cart.setProductSkuPromotion("s01", "ABC001", new BigDecimal("5.99"), "-50");

        cart.addGiftToCart("s01", "G001", "SKU name", BigDecimal.ONE, "G001");
        cart.setGiftPrice("s01", "G001", new BigDecimal("4.99"), new BigDecimal("4.99"));

        cart.addProductSkuToCart("s01", "ABC002", "SKU name", BigDecimal.ONE, null, false, false);
        cart.setProductSkuPrice("s01", "ABC002", new BigDecimal("9.99"), new BigDecimal("10.99"));
        cart.setProductSkuOffer("s01", "ABC002", new BigDecimal("5.99"), null, "AUTH001");

        assertEquals(3, cart.getCartItemList().size());

        final CartItem abc001 = cart.getCartItemList().get(0);
        assertNotNull(abc001);
        assertFalse(abc001.isGift());
        assertTrue(abc001.isPromoApplied());
        assertFalse(abc001.isFixedPrice());
        assertEquals("-50", abc001.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc001.getPrice());

        final CartItem abc002 = cart.getCartItemList().get(1);
        assertNotNull(abc002);
        assertFalse(abc002.isGift());
        assertFalse(abc002.isPromoApplied());
        assertTrue(abc002.isFixedPrice());
        assertEquals("AUTH001", abc002.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc002.getPrice());

        final CartItem gift = cart.getCartItemList().get(2);
        assertNotNull(gift);
        assertTrue(gift.isGift());
        assertTrue(gift.isPromoApplied());
        assertFalse(gift.isFixedPrice());
        assertEquals("G001", gift.getAppliedPromo());
        assertEquals(new BigDecimal("0.00"), gift.getPrice());

        cart.removeItemOffers();

        assertEquals(3, cart.getCartItemList().size());

        final CartItem abc001_2 = cart.getCartItemList().get(0);
        assertNotNull(abc001_2);
        assertFalse(abc001_2.isGift());
        assertTrue(abc001_2.isPromoApplied());
        assertFalse(abc001_2.isFixedPrice());
        assertEquals("-50", abc001_2.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc001_2.getPrice());

        final CartItem abc002_2 = cart.getCartItemList().get(1);
        assertNotNull(abc002_2);
        assertFalse(abc002_2.isGift());
        assertFalse(abc002_2.isPromoApplied());
        assertFalse(abc002_2.isFixedPrice());
        assertNull("AUTH001", abc002_2.getAppliedPromo());
        assertEquals(new BigDecimal("9.99"), abc002_2.getPrice());

        final CartItem gift_2 = cart.getCartItemList().get(2);
        assertNotNull(gift_2);
        assertTrue(gift_2.isGift());
        assertTrue(gift_2.isPromoApplied());
        assertFalse(gift_2.isFixedPrice());
        assertEquals("G001", gift_2.getAppliedPromo());
        assertEquals(new BigDecimal("0.00"), gift_2.getPrice());


    }


    @Test
    public void testSetProductSkuPrice() throws Exception {

        cart.addProductSkuToCart("s01", "ABC", "SKU name", BigDecimal.ONE, null, false, false);

        final CartItem noPrice = cart.getCartItemList().get(0);

        assertEquals(BigDecimal.ZERO, noPrice.getListPrice());
        assertEquals(BigDecimal.ZERO, noPrice.getSalePrice());
        assertEquals(BigDecimal.ZERO, noPrice.getPrice());
        assertFalse(noPrice.isPromoApplied());
        assertFalse(noPrice.isFixedPrice());
        assertNull(noPrice.getAppliedPromo());

        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("9.99"), new BigDecimal("10.99"));

        final CartItem hasPrice = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), hasPrice.getListPrice());
        assertEquals(new BigDecimal("9.99"), hasPrice.getSalePrice());
        assertEquals(new BigDecimal("9.99"), hasPrice.getPrice());
        assertFalse(hasPrice.isPromoApplied());
        assertFalse(hasPrice.isFixedPrice());
        assertNull(hasPrice.getAppliedPromo());

        // Do promotion
        cart.setProductSkuPromotion("s01", "ABC", new BigDecimal("4.99"), "50OFF");

        final CartItem promoPrice = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), promoPrice.getListPrice());
        assertEquals(new BigDecimal("9.99"), promoPrice.getSalePrice());
        assertEquals(new BigDecimal("4.99"), promoPrice.getPrice());
        assertTrue(promoPrice.isPromoApplied());
        assertFalse(promoPrice.isFixedPrice());
        assertEquals("50OFF", promoPrice.getAppliedPromo());

        // Perform set price
        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("9.99"), new BigDecimal("10.99"));

        final CartItem promoPriceAfterReset = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), promoPriceAfterReset.getListPrice());
        assertEquals(new BigDecimal("9.99"), promoPriceAfterReset.getSalePrice());
        assertEquals(new BigDecimal("9.99"), promoPriceAfterReset.getPrice());
        assertFalse(promoPriceAfterReset.isPromoApplied());
        assertFalse(promoPriceAfterReset.isFixedPrice());
        assertNull(promoPriceAfterReset.getAppliedPromo());

        // Do offer
        cart.setProductSkuOffer("s01", "ABC", new BigDecimal("8.99"), null, "AUTH001");

        final CartItem offerPrice = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), offerPrice.getListPrice());
        assertEquals(new BigDecimal("9.99"), offerPrice.getSalePrice());
        assertEquals(new BigDecimal("8.99"), offerPrice.getPrice());
        assertFalse(offerPrice.isPromoApplied());
        assertTrue(offerPrice.isFixedPrice());
        assertEquals("AUTH001", offerPrice.getAppliedPromo());

        // Perform set price higher than offer
        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("11.99"), new BigDecimal("12.99"));

        final CartItem offerPriceAfterResetHigher = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("12.99"), offerPriceAfterResetHigher.getListPrice());
        assertEquals(new BigDecimal("11.99"), offerPriceAfterResetHigher.getSalePrice());
        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetHigher.getPrice());
        assertFalse(offerPriceAfterResetHigher.isPromoApplied());
        assertTrue(offerPriceAfterResetHigher.isFixedPrice());
        assertEquals("AUTH001", offerPriceAfterResetHigher.getAppliedPromo());

        // Perform set price lower than offer
        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("4.99"), new BigDecimal("5.99"));

        final CartItem offerPriceAfterResetLower = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetLower.getListPrice());
        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetLower.getSalePrice());
        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetLower.getPrice());
        assertFalse(offerPriceAfterResetLower.isPromoApplied());
        assertTrue(offerPriceAfterResetLower.isFixedPrice());
        assertEquals("AUTH001", offerPriceAfterResetLower.getAppliedPromo());

    }


    @Test
    public void testSetProductSkuPriceMultiGroup() throws Exception {

        cart.addProductSkuToCart("s01", "ABC", "SKU name", BigDecimal.ONE, "G1", false, false);

        final CartItem noPrice1 = cart.getCartItemList().get(0);

        assertEquals(BigDecimal.ZERO, noPrice1.getListPrice());
        assertEquals(BigDecimal.ZERO, noPrice1.getSalePrice());
        assertEquals(BigDecimal.ZERO, noPrice1.getPrice());
        assertFalse(noPrice1.isPromoApplied());
        assertFalse(noPrice1.isFixedPrice());
        assertNull(noPrice1.getAppliedPromo());

        cart.addProductSkuToCart("s01", "ABC", "SKU name", BigDecimal.ONE, "G2", false, false);

        final CartItem noPrice2 = cart.getCartItemList().get(1);

        assertEquals(BigDecimal.ZERO, noPrice2.getListPrice());
        assertEquals(BigDecimal.ZERO, noPrice2.getSalePrice());
        assertEquals(BigDecimal.ZERO, noPrice2.getPrice());
        assertFalse(noPrice2.isPromoApplied());
        assertFalse(noPrice2.isFixedPrice());
        assertNull(noPrice2.getAppliedPromo());

        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("9.99"), new BigDecimal("10.99"));

        final CartItem hasPrice1 = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), hasPrice1.getListPrice());
        assertEquals(new BigDecimal("9.99"), hasPrice1.getSalePrice());
        assertEquals(new BigDecimal("9.99"), hasPrice1.getPrice());
        assertFalse(hasPrice1.isPromoApplied());
        assertFalse(hasPrice1.isFixedPrice());
        assertNull(hasPrice1.getAppliedPromo());

        final CartItem hasPrice2 = cart.getCartItemList().get(1);

        assertEquals(new BigDecimal("10.99"), hasPrice2.getListPrice());
        assertEquals(new BigDecimal("9.99"), hasPrice2.getSalePrice());
        assertEquals(new BigDecimal("9.99"), hasPrice2.getPrice());
        assertFalse(hasPrice2.isPromoApplied());
        assertFalse(hasPrice2.isFixedPrice());
        assertNull(hasPrice2.getAppliedPromo());

        // Do promotion
        cart.setProductSkuPromotion("s01", "ABC", new BigDecimal("4.99"), "50OFF");

        final CartItem promoPrice1 = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), promoPrice1.getListPrice());
        assertEquals(new BigDecimal("9.99"), promoPrice1.getSalePrice());
        assertEquals(new BigDecimal("4.99"), promoPrice1.getPrice());
        assertTrue(promoPrice1.isPromoApplied());
        assertFalse(promoPrice1.isFixedPrice());
        assertEquals("50OFF", promoPrice1.getAppliedPromo());

        final CartItem promoPrice2 = cart.getCartItemList().get(1);

        assertEquals(new BigDecimal("10.99"), promoPrice2.getListPrice());
        assertEquals(new BigDecimal("9.99"), promoPrice2.getSalePrice());
        assertEquals(new BigDecimal("4.99"), promoPrice2.getPrice());
        assertTrue(promoPrice2.isPromoApplied());
        assertFalse(promoPrice2.isFixedPrice());
        assertEquals("50OFF", promoPrice2.getAppliedPromo());

        // Perform set price
        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("9.99"), new BigDecimal("10.99"));

        final CartItem promoPriceAfterReset1 = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), promoPriceAfterReset1.getListPrice());
        assertEquals(new BigDecimal("9.99"), promoPriceAfterReset1.getSalePrice());
        assertEquals(new BigDecimal("9.99"), promoPriceAfterReset1.getPrice());
        assertFalse(promoPriceAfterReset1.isPromoApplied());
        assertFalse(promoPriceAfterReset1.isFixedPrice());
        assertNull(promoPriceAfterReset1.getAppliedPromo());

        final CartItem promoPriceAfterReset2 = cart.getCartItemList().get(1);

        assertEquals(new BigDecimal("10.99"), promoPriceAfterReset2.getListPrice());
        assertEquals(new BigDecimal("9.99"), promoPriceAfterReset2.getSalePrice());
        assertEquals(new BigDecimal("9.99"), promoPriceAfterReset2.getPrice());
        assertFalse(promoPriceAfterReset2.isPromoApplied());
        assertFalse(promoPriceAfterReset2.isFixedPrice());
        assertNull(promoPriceAfterReset2.getAppliedPromo());

        // Do offer fr G2 only
        cart.setProductSkuOffer("s01", "ABC", new BigDecimal("8.99"), "G2", "AUTH001");

        final CartItem offerPrice1 = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), offerPrice1.getListPrice());
        assertEquals(new BigDecimal("9.99"), offerPrice1.getSalePrice());
        assertEquals(new BigDecimal("9.99"), offerPrice1.getPrice());
        assertFalse(offerPrice1.isPromoApplied());
        assertFalse(offerPrice1.isFixedPrice());
        assertNull(offerPrice1.getAppliedPromo());

        final CartItem offerPrice2 = cart.getCartItemList().get(1);

        assertEquals(new BigDecimal("10.99"), offerPrice2.getListPrice());
        assertEquals(new BigDecimal("9.99"), offerPrice2.getSalePrice());
        assertEquals(new BigDecimal("8.99"), offerPrice2.getPrice());
        assertFalse(offerPrice2.isPromoApplied());
        assertTrue(offerPrice2.isFixedPrice());
        assertEquals("AUTH001", offerPrice2.getAppliedPromo());

        // Perform set price higher than offer
        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("11.99"), new BigDecimal("12.99"));

        final CartItem offerPriceAfterResetHigher1 = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("12.99"), offerPriceAfterResetHigher1.getListPrice());
        assertEquals(new BigDecimal("11.99"), offerPriceAfterResetHigher1.getSalePrice());
        assertEquals(new BigDecimal("11.99"), offerPriceAfterResetHigher1.getPrice());
        assertFalse(offerPriceAfterResetHigher1.isPromoApplied());
        assertFalse(offerPriceAfterResetHigher1.isFixedPrice());
        assertNull(offerPriceAfterResetHigher1.getAppliedPromo());

        final CartItem offerPriceAfterResetHigher2 = cart.getCartItemList().get(1);

        assertEquals(new BigDecimal("12.99"), offerPriceAfterResetHigher2.getListPrice());
        assertEquals(new BigDecimal("11.99"), offerPriceAfterResetHigher2.getSalePrice());
        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetHigher2.getPrice());
        assertFalse(offerPriceAfterResetHigher2.isPromoApplied());
        assertTrue(offerPriceAfterResetHigher2.isFixedPrice());
        assertEquals("AUTH001", offerPriceAfterResetHigher2.getAppliedPromo());

        // Perform set price lower than offer
        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("4.99"), new BigDecimal("5.99"));

        final CartItem offerPriceAfterResetLower1 = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("5.99"), offerPriceAfterResetLower1.getListPrice());
        assertEquals(new BigDecimal("4.99"), offerPriceAfterResetLower1.getSalePrice());
        assertEquals(new BigDecimal("4.99"), offerPriceAfterResetLower1.getPrice());
        assertFalse(offerPriceAfterResetLower1.isPromoApplied());
        assertFalse(offerPriceAfterResetLower1.isFixedPrice());
        assertNull(offerPriceAfterResetLower1.getAppliedPromo());

        final CartItem offerPriceAfterResetLower2 = cart.getCartItemList().get(1);

        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetLower2.getListPrice());
        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetLower2.getSalePrice());
        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetLower2.getPrice());
        assertFalse(offerPriceAfterResetLower2.isPromoApplied());
        assertTrue(offerPriceAfterResetLower2.isFixedPrice());
        assertEquals("AUTH001", offerPriceAfterResetLower2.getAppliedPromo());

    }
}
