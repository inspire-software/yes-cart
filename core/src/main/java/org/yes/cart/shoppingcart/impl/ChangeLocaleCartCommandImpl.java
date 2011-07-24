package org.yes.cart.shoppingcart.impl;

import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/25/11
 * Time: 11:37 AM
 */
public class ChangeLocaleCartCommandImpl  extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20110625L;

    public final static String CMD_KEY = "changeLocaleCmd";

    private final String locale;

    /** {@inheritDoc} */
    public String getCmdKey() {
        return CMD_KEY;
    }

    /**
     * Construct command to change shopping cart locale.
     * @param applicationContext  application context.
     * @param parameters  command parameters.
     */
    public ChangeLocaleCartCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super(applicationContext, parameters);
        locale = (String) parameters.get(getCmdKey());
    }


    /** {@inheritDoc} */
    public void execute(final ShoppingCart shoppingCart) {
        if (locale != null) {
            ((ShoppingCartImpl)shoppingCart).setCurrentLocale(locale);
            recalculatePrice(shoppingCart);
            setModifiedDate(shoppingCart);
        }
    }
}