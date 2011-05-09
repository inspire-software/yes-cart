package org.yes.cart.shoppingcart.impl;


import org.springframework.context.ApplicationContext;
import org.yes.cart.domain.dto.ShoppingCart;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ChangeCurrencyEventCommandImpl extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20101702L;

    public static String CMD_KEY = "changeCurrencyCmd";

    /** {@inheritDoc} */
    public String getCmdKey() {
        return CMD_KEY;
    }

    private String currencyCode = null;

    public ChangeCurrencyEventCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super(applicationContext, parameters);
        currencyCode = (String) parameters.get(getCmdKey());
    }


    /** {@inheritDoc} */
    public void execute(final ShoppingCart shoppingCart) {
        if (currencyCode != null) {
            shoppingCart.setCurrencyCode(currencyCode);
            recalculatePrice(shoppingCart);
        }
    }
}
