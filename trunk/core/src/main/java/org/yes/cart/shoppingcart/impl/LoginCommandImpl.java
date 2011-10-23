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
public class LoginCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20101026L;

    public static final String CMD_KEY = "loginCmd";
    public static final String EMAIL = "email";
    public static final String NAME = "name";

    private final Map parameters;

    /**
     * {@inheritDoc}
     */
    public void execute(final ShoppingCart shoppingCart) {

        shoppingCart.getShoppingContext().setCustomerEmail((String) parameters.get(EMAIL));
        shoppingCart.getShoppingContext().setCustomerName((String) parameters.get(NAME));
        setModifiedDate(shoppingCart);
    }

    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_KEY;
    }

    /**
     * @param applicationContext application context
     * @param parameters         page parameters
     */
    public LoginCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super();
        this.parameters = parameters;
    }


}
