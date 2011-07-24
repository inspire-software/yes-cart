package org.yes.cart.shoppingcart.impl;

import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 13:16
 */
public abstract class AbstractCartCommandImpl implements ShoppingCartCommand {


    /**
     * {@inheritDoc}
     */
    public void setModifiedDate(final ShoppingCart shoppingCart) {
        ((ShoppingCartImpl) shoppingCart).setModifiedDate(new Date());
    }
}
