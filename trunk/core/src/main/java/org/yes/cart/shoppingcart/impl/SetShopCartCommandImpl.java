package org.yes.cart.shoppingcart.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.context.ApplicationContext;
import org.yes.cart.domain.dto.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 22-May-2011
 * Time: 14:12:54
 */
public class SetShopCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 2010522L;

    public static String CMD_KEY = "setShopIdCmd";

    private final long value;

    /**
     * Execute command on shopping cart to perform changes.
     *
     * @param shoppingCart the shopping cart
     */
    public void execute(final ShoppingCart shoppingCart) {
        shoppingCart.getShoppingContext().setShopId(value);
    }

    /**
     * @return command key
     */
    public String getCmdKey() {
        return CMD_KEY;
    }

    /**
     * @param applicationContext application context
     * @param parameters         page parameters
     */
    public SetShopCartCommandImpl(
            final ApplicationContext applicationContext, final Map parameters) {
        super();
        value = NumberUtils.createLong(String.valueOf(parameters.get(CMD_KEY)));
    }

}
