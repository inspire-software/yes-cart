package org.yes.cart.shoppingcart.impl;

import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetBillingSeparateFromShippingAddressCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20101029L;

    public static final String CMD_KEY = "setBillingAddressSeparateCmd";

    private final boolean value;

    /**
     * Execute command on shopping cart to perform changes.
     *
     * @param shoppingCart the shopping cart
     */
    public void execute(final ShoppingCart shoppingCart) {
        shoppingCart.getOrderInfo().setSeparateBillingAddress(value);
        setModifiedDate(shoppingCart);
    }

    /**
     * @return command key
     */
    public String getCmdKey() {
        return CMD_KEY;
    }

    /**
     *
     * @param applicationContext application context
     * @param parameters page parameters
     */
    public SetBillingSeparateFromShippingAddressCommandImpl(
            final ApplicationContext applicationContext, final Map parameters) {
        super();
        value = Boolean.valueOf((String) parameters.get(CMD_KEY));
    }
}
