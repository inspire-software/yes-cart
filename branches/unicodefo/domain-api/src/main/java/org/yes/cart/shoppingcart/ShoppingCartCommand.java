package org.yes.cart.shoppingcart;


import java.io.Serializable;

/**
 * .
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:07:03 PM
 */
public interface ShoppingCartCommand extends Serializable {

    /**
     * Execute command on shopping cart to perform changes.
     *
     * @param shoppingCart the shopping cart
     */
    void execute(ShoppingCart shoppingCart);

    /**
     * Execute command on shopping cart to perform changes.
     *
     * @param shoppingCart the shopping cart
     */
    void setModifiedDate(ShoppingCart shoppingCart);

    /**
     * @return command key
     */
    String getCmdKey();

}
