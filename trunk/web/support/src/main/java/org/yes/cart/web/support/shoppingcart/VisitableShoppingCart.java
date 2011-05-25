package org.yes.cart.web.support.shoppingcart;

import org.yes.cart.shoppingcart.ShoppingCart;
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


}
