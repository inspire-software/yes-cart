package org.yes.cart.shoppingcart.impl;


import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ChangeCurrencyEventCommandImpl extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20101702L;

    public static final String CMD_KEY = "changeCurrencyCmd";

    private final String currencyCode;

    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_KEY;
    }


    public ChangeCurrencyEventCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super(applicationContext, parameters);
        currencyCode = (String) parameters.get(CMD_KEY);
    }


    /**
     * {@inheritDoc}
     */
    public void execute(final ShoppingCart shoppingCart) {
        if (currencyCode != null) {
            ((ShoppingCartImpl) shoppingCart).setCurrencyCode(currencyCode);
            recalculatePrice(shoppingCart);
            setModifiedDate(shoppingCart);
        }
    }
}
