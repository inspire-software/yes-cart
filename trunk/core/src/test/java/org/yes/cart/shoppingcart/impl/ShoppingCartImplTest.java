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

package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: dogma
 * Date: Jan 16, 2011
 * Time: 1:19:22 AM
 */
public class ShoppingCartImplTest {

    private ShoppingCartImpl cart = new ShoppingCartImpl();

    @Test
    public void testIndexOfSkuInexistent() {
        assertEquals("Size should be 0", 0, cart.getCartItemsCount());
        assertEquals("Index must be -1 for inexistent sku", -1, cart.indexOf("sku"));
    }

    @Test
    public void testAddProductSkuDTOToCartInexistent() {
        boolean newItem = cart.addProductSkuToCart("sku", BigDecimal.TEN);
        assertTrue("Must create new item", newItem);
        assertEquals("Size should be 10", 10, cart.getCartItemsCount());
        assertEquals("Index must be 0 for sku", 0, cart.indexOf("sku"));
        assertEquals("Items must have 1 element", 1, cart.getCartItemList().size());
        assertEquals("1st element must be sku", "sku", cart.getCartItemList().get(0).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuDTOToCartExistent() {
        boolean newItem1 = cart.addProductSkuToCart("sku", BigDecimal.TEN);
        boolean newItem2 = cart.addProductSkuToCart("sku", BigDecimal.TEN);
        assertTrue("Must create new item", newItem1);
        assertFalse("Must not create new item", newItem2);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index must be 0 for sku", 0, cart.indexOf("sku"));
        assertEquals("Items must have 1 element", 1, cart.getCartItemList().size());
        assertEquals("1st element must be sku", "sku", cart.getCartItemList().get(0).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuToCartExistentAndInexistent() {
        boolean newItem1 = cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        boolean newItem2 = cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        boolean newItem3 = cart.addProductSkuToCart("sku02", BigDecimal.TEN);
        assertTrue("Must create new item", newItem1);
        assertFalse("Must not create new item", newItem2);
        assertTrue("Must create new item", newItem3);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
        assertEquals("Index must be 0 for sku01", 0, cart.indexOf("sku01"));
        assertEquals("Index must be 1 for sku02", 1, cart.indexOf("sku02"));
        assertEquals("Items must have 2 elements", 2, cart.getCartItemList().size());
        assertEquals("1st element must be sku01", "sku01", cart.getCartItemList().get(0).getProductSkuCode());
        assertEquals("1st element must be sku02", "sku02", cart.getCartItemList().get(1).getProductSkuCode());
    }

    @Test
    public void testRemoveCartItemInexistent() {
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku02", BigDecimal.TEN);
        boolean removed = cart.removeCartItem("sku03");
        assertFalse("Must not be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
    }

    @Test
    public void testRemoveCartItemExistent() {
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku02", BigDecimal.TEN);
        boolean removed = cart.removeCartItem("sku02");
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index of removed should be -1", -1, cart.indexOf("sku02"));
    }

    @Test
    public void testRemoveCartItemQuantityInexistent() {
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku02", BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity("sku03", BigDecimal.TEN);
        assertFalse("Must not be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
    }

    @Test
    public void testRemoveCartItemQuantityExistent() {
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku02", BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity("sku02", BigDecimal.ONE);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 29", 29, cart.getCartItemsCount());
        assertEquals("Index of removed should be 1", 1, cart.indexOf("sku02"));
        assertTrue("Quantity should change to 9", MoneyUtils.isFirstEqualToSecond(new BigDecimal(9), cart.getCartItemList().get(1).getQty()));
    }

    @Test
    public void testRemoveCartItemQuantityExistentFull() {
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku02", BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity("sku02", BigDecimal.TEN);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index of removed should be -1", -1, cart.indexOf("sku02"));
    }

    @Test
    public void testRemoveCartItemQuantityExistentMoreThanInCart() {
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku01", BigDecimal.TEN);
        cart.addProductSkuToCart("sku02", BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity("sku02", new BigDecimal(100));
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index of removed should be -1", -1, cart.indexOf("sku02"));
    }

}
