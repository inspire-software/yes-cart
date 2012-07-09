package org.yes.cart.web.support.util.cookie;

import org.yes.cart.shoppingcart.ShoppingCart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 18-Sep-2011
 * Time: 12:53:26
 */
public interface ShoppingCartPersister {

    /**
     * Persist shopping cart to cookie.
     * @param httpServletRequest request.
     * @param httpServletResponse responce
     * @param shoppingCart shopping cart
     */
    void persistShoppingCart(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             ShoppingCart shoppingCart);

}
