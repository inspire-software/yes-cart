package org.yes.cart.shoppingcart.impl;

import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.Map;

/**
 *
 * Set payment gateway label for shopping cart.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/6/11
 * Time: 5:49 PM
 */
public class SetPaymentGatewayLabelCommandImpl extends AbstractCartCommandImpl  implements ShoppingCartCommand {

    private static final long serialVersionUID = 20111106L;

       public static final String CMD_KEY = "setPgLAbel";

       private final String value;

       /**
        * Execute command on shopping cart to perform changes.
        *
        * @param shoppingCart the shopping cart
        */
       public void execute(final ShoppingCart shoppingCart) {
           shoppingCart.getOrderInfo().setPaymentGatewayLabel(value);
           setModifiedDate(shoppingCart);
       }

       /**
        * @return command key
        */
       public String getCmdKey() {
           return CMD_KEY;
       }

       /**
        * COnstruct command implementation.
        * @param applicationContext application context
        * @param parameters page parameters
        */
       public SetPaymentGatewayLabelCommandImpl(
               final ApplicationContext applicationContext, final Map parameters) {
           super();
           value = (String) parameters.get(CMD_KEY);
       }

}
