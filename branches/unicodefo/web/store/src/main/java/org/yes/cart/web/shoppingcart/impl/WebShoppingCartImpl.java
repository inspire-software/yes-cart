package org.yes.cart.web.shoppingcart.impl;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/13/11
 * Time: 7:18 PM
 */

import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.util.cookie.annotations.PersistentCookie;


@PersistentCookie(value = "yes", expirySeconds = 864000, path = "/")
public class WebShoppingCartImpl extends ShoppingCartImpl {
}
