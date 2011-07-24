package org.yes.cart.shoppingcart.impl;

import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.Map;

/**
 *
 * Clean cart and prepare it to reuse.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CleanCartCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    public static final String CMD_KEY = "cleanCartCmd";

    /** {@inheritDoc} */
    public void execute(ShoppingCart shoppingCart) {
        shoppingCart.clean();
        setModifiedDate(shoppingCart);
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
    public CleanCartCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super();
    }

}
