package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * CartItemImpl test.
 * <p/>
 * User: dogma
 * Date: Jan 16, 2011
 * Time: 12:28:31 AM
 */
public class CartItemImplTest {

    private CartItemImpl item = new CartItemImpl();

    @Test
    public void testGetQuantityPreventsModificationsToQuantity() {
        BigDecimal original = BigDecimal.TEN;
        item.setQuantity(original);
        BigDecimal read = item.getQty();
        assertNotSame("must be different big decimal instances to prevent modifications", original, read);
        assertTrue("must be of equal value", MoneyUtils.isFirstEqualToSecond(original, read));
    }

    @Test
    public void testSetQuantityIsNullSafe() {
        item.setQuantity(null);
        assertEquals("Must be valid quantity (default is one)", BigDecimal.ONE, item.getQty());
    }

    @Test
    public void testSetQuantityIsZeroSafe() {
        item.setQuantity(BigDecimal.ZERO);
        assertEquals("Must be valid quantity (default is one)", BigDecimal.ONE, item.getQty());
    }

    @Test
    public void testSetQuantityIsNegativeSafe() {
        item.setQuantity(BigDecimal.ZERO.subtract(BigDecimal.ONE));
        assertEquals("Must be valid quantity (default is one)", BigDecimal.ONE, item.getQty());
    }

    @Test
    public void testAddQuantityNullSafe() {
        BigDecimal original = item.getQty();
        BigDecimal newValue = item.addQuantity(null);
        assertTrue("must be of equal value to original when null is added", MoneyUtils.isFirstEqualToSecond(original, newValue));
        assertTrue("must be of equal value to original when null is added", MoneyUtils.isFirstEqualToSecond(original, item.getQty()));
    }

    @Test
    public void testAddQuantityNegativeSafe() {
        BigDecimal original = item.getQty();
        BigDecimal newValue = item.addQuantity(BigDecimal.ZERO.subtract(BigDecimal.TEN));
        assertTrue("must be of equal value to original when negative is added", MoneyUtils.isFirstEqualToSecond(original, newValue));
        assertTrue("must be of equal value to original when negative is added", MoneyUtils.isFirstEqualToSecond(newValue, item.getQty()));
    }

    @Test
    public void testAddQuantity() {
        BigDecimal original = item.getQty();
        BigDecimal newValue = item.addQuantity(BigDecimal.TEN);
        assertTrue("must add value correctly", MoneyUtils.isFirstEqualToSecond(original.add(BigDecimal.TEN), newValue));
        assertTrue("must add value correctly", MoneyUtils.isFirstEqualToSecond(newValue, item.getQty()));
    }

    @Test
    public void testRemoveQuantityNullSafe() throws CartItemRequiresDeletion {
        BigDecimal original = item.getQty();
        BigDecimal newValue = item.removeQuantity(null);
        assertTrue("must be of equal value to original when null is removed", MoneyUtils.isFirstEqualToSecond(original, newValue));
        assertTrue("must be of equal value to original when null is removed", MoneyUtils.isFirstEqualToSecond(original, item.getQty()));
    }

    @Test
    public void testRemoveQuantityNegativeSafe() throws CartItemRequiresDeletion {
        BigDecimal original = item.getQty();
        BigDecimal newValue = item.removeQuantity(BigDecimal.ZERO.subtract(BigDecimal.ONE));
        assertTrue("must be of equal value to original when negative is removed", MoneyUtils.isFirstEqualToSecond(original, newValue));
        assertTrue("must be of equal value to original when negative is removed", MoneyUtils.isFirstEqualToSecond(newValue, item.getQty()));
    }

    @Test(expected = CartItemRequiresDeletion.class)
    public void testRemoveQuantityWithCartItemRequiresDeletionException() throws CartItemRequiresDeletion {
        item.removeQuantity(BigDecimal.TEN);
    }

    @Test
    public void testRemoveQuantity() throws CartItemRequiresDeletion {
        BigDecimal original = item.addQuantity(new BigDecimal(100));
        BigDecimal newValue = item.removeQuantity(BigDecimal.TEN);
        assertFalse("must be of equal value to original when negative is removed", MoneyUtils.isFirstEqualToSecond(original, newValue));
        assertTrue("must be of equal value to original when negative is removed", MoneyUtils.isFirstEqualToSecond(newValue, item.getQty()));
        assertTrue("must be of equal value to original when negative is removed", MoneyUtils.isFirstEqualToSecond(newValue, new BigDecimal(91)));
    }
}
