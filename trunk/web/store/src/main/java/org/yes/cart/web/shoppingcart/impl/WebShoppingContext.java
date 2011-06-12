package org.yes.cart.web.shoppingcart.impl;


import org.yes.cart.shoppingcart.impl.ShoppingContextImpl;
import org.yes.cart.web.support.constants.WebParametersKeys;

import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;

/**
 * Just managed bean.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/9/11
 * Time: 9:20 PM
 */
@ManagedBean(name = WebParametersKeys.SESSION_SHOPPING_CONTEXT)
@SessionScoped
public class WebShoppingContext extends ShoppingContextImpl {
}
