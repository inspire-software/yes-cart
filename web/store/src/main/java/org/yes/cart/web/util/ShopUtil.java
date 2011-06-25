package org.yes.cart.web.util;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.WebParametersKeys;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

/**
 *
 *
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/20/11
 * Time: 9:42 PM
 */
public class ShopUtil {

    /**
     * Get the shop.
     *
     * @return {@link org.yes.cart.domain.entity.Shop}
     */
    public static Shop getShop() {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        final Application application = facesContext.getApplication();
        final ShoppingCart shoppingCart = (ShoppingCart) application.getVariableResolver().resolveVariable(
                facesContext, WebParametersKeys.SESSION_SHOPPING_CART);
        final ApplicationDirector applicationDirector = (ApplicationDirector) application.getVariableResolver().resolveVariable(
                facesContext, WebParametersKeys.APPLICATION_DYNAMYC_CACHE);
        return applicationDirector.getShopById(shoppingCart.getShoppingContext().getShopId());
    }

}
