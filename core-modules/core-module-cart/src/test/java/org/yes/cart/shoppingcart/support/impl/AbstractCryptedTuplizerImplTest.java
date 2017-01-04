package org.yes.cart.shoppingcart.support.impl;

import org.junit.Test;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 15/03/2016
 * Time: 12:17
 */
public class AbstractCryptedTuplizerImplTest {

    @Test
    public void testCrypto() throws Exception {

        final ShoppingCartImpl cart = new ShoppingCartImpl();

        cart.addProductSkuToCart("SKU001", "SKU001", BigDecimal.ONE);
        cart.setProductSkuPrice("SKU001", new BigDecimal("9.99"), new BigDecimal("9.99"));

        final AbstractCryptedTuplizerImpl tup = new AbstractCryptedTuplizerImpl(
                "CHANGE_PASSWORD",
                "DES",
                "DES/ECB/PKCS5Padding"
        ) {

        };


        final String base64 = tup.toToken(cart);
        assertFalse("Splitting happens manually as it is media dependent", base64.contains("\n"));

        final ShoppingCart restored = (ShoppingCart) tup.toObject(base64);

        assertNotNull(restored);
        assertEquals(1, restored.getCartItemList().size());

    }

}