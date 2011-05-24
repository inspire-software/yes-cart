package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.domain.dto.ShoppingCart;

import java.util.Collections;

import static junit.framework.Assert.assertEquals;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetCarrierSlaCartCommandImplTest {

    @Test
    public void testExecute() {

        ShoppingCart shoppingCart = new ShoppingCartImpl();



        new ChangeCurrencyEventCommandImpl(
                null,
                Collections.singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, "EUR")
                ).execute(shoppingCart);

        new SetShopCartCommandImpl(null, Collections.singletonMap(SetShopCartCommandImpl.CMD_KEY, 10))
                .execute(shoppingCart);

        SetCarrierSlaCartCommandImpl slaChangeCmd = new SetCarrierSlaCartCommandImpl(
                null,
                Collections.singletonMap(SetCarrierSlaCartCommandImpl.CMD_KEY, "123")
                );

        slaChangeCmd.execute(shoppingCart);

        assertEquals(123, shoppingCart.getCarrierSlaId().intValue());

    }

}
