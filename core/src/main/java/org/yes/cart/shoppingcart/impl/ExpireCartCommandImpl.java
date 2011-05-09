package org.yes.cart.shoppingcart.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
        //shoppingCart.setCustomerEmail(null);
        shoppingCart.setLatestViewedSkus(null);
        //TODO keep actual what need set to null. Visited categories , tag clound etc
        //shoppingCart.setAuthentication(null);
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
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
