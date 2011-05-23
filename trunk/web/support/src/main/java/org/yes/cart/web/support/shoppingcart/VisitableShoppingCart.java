package org.yes.cart.web.support.shoppingcart;

import org.yes.cart.domain.dto.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.support.util.cookie.annotations.PersistentCookie;

/**
 * Web aware extension to shopping cart object.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 5:08:46 PM
 */
@PersistentCookie(value = "npa01", expirySeconds = 864000, path = "/")
public interface VisitableShoppingCart extends ShoppingCart {

    /**
     * Accept shopping cart visitor that potentially will make modifications
     * to the cart.
     *
     * @param command the modification visitor
     */
    void accept(final ShoppingCartCommand command);



}
