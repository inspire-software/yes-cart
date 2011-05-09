package org.yes.cart.shoppingcart.impl;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;

import static junit.framework.Assert.*;

/**
 * CartItemImpl test.
 * <p/>
 * User: dogma
 * Date: Jan 16, 2011
 * Time: 12:28:31 AM
 */
@RunWith(org.jmock.integration.junit4.JMock.class)
public class CartItemImplTest {

    private CartItemImpl item;
    private final Mockery mockery = new JUnit4Mockery();

    @Before
    public void setUp() {
        item = new CartItemImpl();
    }

    /* -- ProductSku ----------------------------------- */

    /*@Test(expected = IllegalArgumentException.class)
    public void testSetProductSkuWithNull() {
        item.setProductSku(null);
    }  */

    /*@Test
    public void testSetProductSku() {
        final ProductSkuDTO sku = mockery.mock(ProductSkuDTO.class);
        item.setProductSkuCode(sku.getCode());
    } */

    /* -- Quantity ----------------------------------- */

    @Test
    public void testGetQuantityPreventsModificationsToQuantity() {
        final BigDecimal original = BigDecimal.TEN;

        item.setQuantity(original);
        final BigDecimal read = item.getQty();

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

    /* -- Adding quantity ----------------------------------- */

    @Test
    public void testAddQuantityNullSafe() {
        final BigDecimal original = item.getQty();
        final BigDecimal newValue = item.addQuantity(null);
        assertTrue("must be of equal value to original when null is added", MoneyUtils.isFirstEqualToSecond(original, newValue));
        assertTrue("must be of equal value to original when null is added", MoneyUtils.isFirstEqualToSecond(original, item.getQty()));

    }

    @Test
    public void testAddQuantityNegativeSafe() {
        final BigDecimal original = item.getQty();
        final BigDecimal newValue = item.addQuantity(BigDecimal.ZERO.subtract(BigDecimal.TEN));
        assertTrue("must be of equal value to original when negative is added", MoneyUtils.isFirstEqualToSecond(original, newValue));
        assertTrue("must be of equal value to original when negative is added", MoneyUtils.isFirstEqualToSecond(newValue, item.getQty()));

    }

    @Test
    public void testAddQuantity() {
        final BigDecimal original = item.getQty();
        final BigDecimal newValue = item.addQuantity(BigDecimal.TEN);
        assertTrue("must add value correctly", MoneyUtils.isFirstEqualToSecond(original.add(BigDecimal.TEN), newValue));
        assertTrue("must add value correctly", MoneyUtils.isFirstEqualToSecond(newValue, item.getQty()));

    }

    /* -- Remove quantity ----------------------------------- */

    @Test
    public void testRemoveQuantityNullSafe() throws CartItemRequiresDeletion {
        final BigDecimal original = item.getQty();
        final BigDecimal newValue = item.removeQuantity(null);
        assertTrue("must be of equal value to original when null is removed", MoneyUtils.isFirstEqualToSecond(original, newValue));
        assertTrue("must be of equal value to original when null is removed", MoneyUtils.isFirstEqualToSecond(original, item.getQty()));

    }

    @Test
    public void testRemoveQuantityNegativeSafe() throws CartItemRequiresDeletion {
        final BigDecimal original = item.getQty();
        final BigDecimal newValue = item.removeQuantity(BigDecimal.ZERO.subtract(BigDecimal.ONE));
        assertTrue("must be of equal value to original when negative is removed", MoneyUtils.isFirstEqualToSecond(original, newValue));
        assertTrue("must be of equal value to original when negative is removed", MoneyUtils.isFirstEqualToSecond(newValue, item.getQty()));

    }

    @Test(expected = CartItemRequiresDeletion.class)
    public void testRemoveQuantityWithCartItemRequiresDeletionException() throws CartItemRequiresDeletion {
        item.removeQuantity(BigDecimal.TEN);

    }

    //@Test(expected = CartItemRequiresDeletion.class)
    @Test
    public void testRemoveQuantity() throws CartItemRequiresDeletion {
        final BigDecimal original = item.addQuantity(new BigDecimal(100));
        final BigDecimal newValue = item.removeQuantity(BigDecimal.TEN);
        assertFalse("must be of equal value to original when negative is removed", MoneyUtils.isFirstEqualToSecond(original, newValue));
        assertTrue("must be of equal value to original when negative is removed", MoneyUtils.isFirstEqualToSecond(newValue, item.getQty()));
        assertTrue("must be of equal value to original when negative is removed", MoneyUtils.isFirstEqualToSecond(newValue, new BigDecimal(91)));
    }
}
