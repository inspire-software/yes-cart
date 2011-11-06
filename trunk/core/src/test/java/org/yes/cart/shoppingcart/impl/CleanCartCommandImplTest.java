package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.domain.dto.impl.ProductSkuDTOImpl;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CleanCartCommandImplTest {

    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.addProductSkuToCart(new ProductSkuDTOImpl(), BigDecimal.ONE);
        shoppingCart.getOrderInfo().setOrderMessage("hi im cart");
        String oldGuid = shoppingCart.getGuid();
        new CleanCartCommandImpl(null, null)
                .execute(shoppingCart);
        assertNull(shoppingCart.getOrderMessage());
        assertNotNull(shoppingCart.getModifiedDate());
        assertTrue(shoppingCart.getCartItemList().isEmpty());
        assertNotSame(oldGuid, shoppingCart.getGuid());
    }
}

