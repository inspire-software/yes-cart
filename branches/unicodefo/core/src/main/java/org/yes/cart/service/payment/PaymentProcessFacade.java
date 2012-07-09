package org.yes.cart.service.payment;

import org.yes.cart.service.order.OrderException;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.Map;

/**
 *
 * This facade hide interaction with order and payment
 * services during payment.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 09/11/11
 * Time: 10:08
 */
public interface PaymentProcessFacade {


    /**
     * Perform order payment.
     * @param paymentParameter the payment parameters.
     * @param shoppingCart  the shopping cart.
     * @throws OrderException in case if payment was failed
     * @return true in case of succesfull payment
     */
    boolean pay(final ShoppingCart shoppingCart, final Map paymentParameter) throws OrderException;


}
