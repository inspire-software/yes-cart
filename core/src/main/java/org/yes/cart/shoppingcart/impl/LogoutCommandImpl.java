package org.yes.cart.shoppingcart.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class LogoutCommandImpl  implements ShoppingCartCommand {

    private static final long serialVersionUID = 20101025L;

    public static String CMD_KEY = "logoutCmd";

    /**
     * Execute command on shopping cart to perform changes.
     *
     * @param shoppingCart the shopping cart
     */
    public void execute(final ShoppingCart shoppingCart) {
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        shoppingCart.getShoppingContext().setSecurityContext(securityContext);
        shoppingCart.getShoppingContext().setCustomerName(null);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(null);

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
    public LogoutCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super();
    }

}
