package org.yes.cart.shoppingcart;

import java.io.Serializable;
import java.util.Map;

/**
 * Factory for shopping cart visitor events.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:25:25 PM
 */
public interface ShoppingCartCommandFactory extends Serializable {

    /**
     * Get the {@link ShoppingCartCommand} by given map of parameters.
     * @param pageParameters map of web request parameters.
     * @return {@link ShoppingCartCommand} on null if command can not be found in map parameters.
     */
    ShoppingCartCommand create(final Map pageParameters);

}
