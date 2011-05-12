package org.yes.cart.shoppingcart.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.yes.cart.domain.dto.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ExpireCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20101026L;

    public static String CMD_KEY = "expireCartCmd";

    /** {@inheritDoc} */
    public void execute(final ShoppingCart shoppingCart) {
        shoppingCart.getShoppingContext().clearContext();        
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        shoppingCart.getShoppingContext().setSecurityContext(securityContext);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(null);

    }

    /** {@inheritDoc} */
    public String getCmdKey() {
        return CMD_KEY;
    }

    /**
     *
     * @param applicationContext application context
     * @param parameters page parameters
     */
    public ExpireCartCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super();
    }

}
