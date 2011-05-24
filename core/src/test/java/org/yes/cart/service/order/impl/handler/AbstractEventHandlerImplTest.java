package org.yes.cart.service.order.impl.handler;

import org.springframework.context.ApplicationContext;
import org.yes.cart.domain.dto.ShoppingCart;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.shoppingcart.impl.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractEventHandlerImplTest  extends BaseCoreDBTestCase {


    /**
     * Simple card with two sku, three items, standard availability, one payment
     * @param context spring context
     * @return cart
     */
    protected ShoppingCart getStdCard(final ApplicationContext context, final String customerEmail) {

        ShoppingCart shoppingCart = getEmptyCart(context, customerEmail);

        Map<String, String> param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST1");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "2.00");

        new SetSkuQuantityToCartEventCommandImpl(context,
                param)
                .execute(shoppingCart);


        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST2");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");

        new SetSkuQuantityToCartEventCommandImpl(context,
                param)
                .execute(shoppingCart);

        return shoppingCart;

    }

    protected ShoppingCart getEmptyCart(final ApplicationContext context, final String customerEmail) {
        ShoppingCart shoppingCart = new ShoppingCartImpl();

        Map<String,String> params = new HashMap<String,String>();
        params.put(LoginCommandImpl.EMAIL,customerEmail);
        params.put(LoginCommandImpl.NAME,"John Doe");


        new SetShopCartCommandImpl(ctx, Collections.singletonMap(SetShopCartCommandImpl.CMD_KEY, 10))
                .execute(shoppingCart);

        new ChangeCurrencyEventCommandImpl( context, Collections.singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, "USD"))
                .execute(shoppingCart);

        new LoginCommandImpl(null, params)
                .execute(shoppingCart);

        new SetCarrierSlaCartCommandImpl(null, Collections.singletonMap(SetCarrierSlaCartCommandImpl.CMD_KEY, "1"))
                .execute(shoppingCart);

        return shoppingCart;
    }

}
