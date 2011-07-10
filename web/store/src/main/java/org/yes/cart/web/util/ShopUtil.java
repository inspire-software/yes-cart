package org.yes.cart.web.util;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.util.HttpUtil;

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
     * Get the single value of given parameter.
     * @param parameterName  given parameter name.
     * @return string value if it presetn in request map , otherwise null
     */
    public static String getRequetsParameterValue(final String parameterName) {
        return HttpUtil.getSingleValue(
            FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(parameterName)
        );

    }

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
