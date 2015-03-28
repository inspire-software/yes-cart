package org.yes.cart.service.order;

/**
 * User: denispavlov
 * Date: 05/06/2014
 * Time: 18:36
 */
public class OrderAssemblyException extends OrderException {

    public OrderAssemblyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public OrderAssemblyException(final String message) {
        super(message);
    }
}
