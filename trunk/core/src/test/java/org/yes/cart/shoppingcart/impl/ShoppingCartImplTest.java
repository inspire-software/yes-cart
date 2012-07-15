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
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.impl.ProductSkuDTOImpl;
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
        ProductSkuDTO sku = getProductSkuDTO("sku");
        assertEquals("Size should be 0", 0, cart.getCartItemsCount());
        assertEquals("Index must be -1 for inexistent sku", -1, cart.indexOf(sku));
    }

    @Test
    public void testAddProductSkuDTOToCartInexistent() {
        ProductSkuDTO sku = getProductSkuDTO("sku");
        boolean newItem = cart.addProductSkuToCart(sku, BigDecimal.TEN);
        assertTrue("Must create new item", newItem);
        assertEquals("Size should be 10", 10, cart.getCartItemsCount());
        assertEquals("Index must be 0 for sku01", 0, cart.indexOf(sku));
        assertEquals("Items must have 1 element", 1, cart.getCartItemList().size());
        assertEquals("1st element must be sku01", sku.getCode(), cart.getCartItemList().get(0).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuDTOToCartExistent() {
        ProductSkuDTO sku = getProductSkuDTO("sku");
        boolean newItem1 = cart.addProductSkuToCart(sku, BigDecimal.TEN);
        boolean newItem2 = cart.addProductSkuToCart(sku, BigDecimal.TEN);
        assertTrue("Must create new item", newItem1);
        assertFalse("Must not create new item", newItem2);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index must be 0 for sku01", 0, cart.indexOf(sku));
        assertEquals("Items must have 1 element", 1, cart.getCartItemList().size());
        assertEquals("1st element must be sku01", sku.getCode(), cart.getCartItemList().get(0).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuToCartExistentAndInexistent() {
        ProductSkuDTO sku = getProductSkuDTO("sku01");
        ProductSkuDTO sku2 = getProductSkuDTO("sku02");
        boolean newItem1 = cart.addProductSkuToCart(sku, BigDecimal.TEN);
        boolean newItem2 = cart.addProductSkuToCart(sku, BigDecimal.TEN);
        boolean newItem3 = cart.addProductSkuToCart(sku2, BigDecimal.TEN);
        assertTrue("Must create new item", newItem1);
        assertFalse("Must not create new item", newItem2);
        assertTrue("Must create new item", newItem3);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
        assertEquals("Index must be 0 for sku01", 0, cart.indexOf(sku));
        assertEquals("Index must be 1 for sku02", 1, cart.indexOf(sku2));
        assertEquals("Items must have 2 elements", 2, cart.getCartItemList().size());
        assertEquals("1st element must be sku01", sku.getCode(), cart.getCartItemList().get(0).getProductSkuCode());
        assertEquals("1st element must be sku02", sku2.getCode(), cart.getCartItemList().get(1).getProductSkuCode());
    }

    @Test
    public void testRemoveCartItemInexistent() {
        ProductSkuDTO sku = getProductSkuDTO("sku01");
        ProductSkuDTO sku2 = getProductSkuDTO("sku02");
        ProductSkuDTO sku3 = getProductSkuDTO("sku03");
        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku2, BigDecimal.TEN);
        boolean removed = cart.removeCartItem(sku3);
        assertFalse("Must not be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
    }

    @Test
    public void testRemoveCartItemExistent() {
        ProductSkuDTO sku = getProductSkuDTO("sku01");
        ProductSkuDTO sku2 = getProductSkuDTO("sku02");
        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku2, BigDecimal.TEN);
        boolean removed = cart.removeCartItem(sku2);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index of removed should be -1", -1, cart.indexOf(sku2));
    }

    @Test
    public void testRemoveCartItemQuantityInexistent() {
        ProductSkuDTO sku = getProductSkuDTO("sku01");
        ProductSkuDTO sku2 = getProductSkuDTO("sku02");
        ProductSkuDTO sku3 = getProductSkuDTO("sku03");

        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku2, BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity(sku3, BigDecimal.TEN);
        assertFalse("Must not be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
    }

    @Test
    public void testRemoveCartItemQuantityExistent() {
        ProductSkuDTO sku = getProductSkuDTO("sku01");
        ProductSkuDTO sku2 = getProductSkuDTO("sku02");
        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku2, BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity(sku2, BigDecimal.ONE);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 29", 29, cart.getCartItemsCount());
        assertEquals("Index of removed should be 1", 1, cart.indexOf(sku2));
        assertTrue("Quantity should change to 9", MoneyUtils.isFirstEqualToSecond(new BigDecimal(9), cart.getCartItemList().get(1).getQty()));
    }

    @Test
    public void testRemoveCartItemQuantityExistentFull() {
        ProductSkuDTO sku = getProductSkuDTO("sku01");
        ProductSkuDTO sku2 = getProductSkuDTO("sku02");
        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku2, BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity(sku2, BigDecimal.TEN);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index of removed should be -1", -1, cart.indexOf(sku2));
    }

    @Test
    public void testRemoveCartItemQuantityExistentMoreThanInCart() {
        ProductSkuDTO sku = getProductSkuDTO("sku01");
        ProductSkuDTO sku2 = getProductSkuDTO("sku02");
        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku, BigDecimal.TEN);
        cart.addProductSkuToCart(sku2, BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity(sku2, new BigDecimal(100));
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index of removed should be -1", -1, cart.indexOf(sku2));
    }

    private ProductSkuDTO getProductSkuDTO(String skuCode) {
        ProductSkuDTO productSkuDTO = new ProductSkuDTOImpl();
        productSkuDTO.setCode(skuCode);
        return productSkuDTO;
    }
}
