package org.yes.cart.service.order;

import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.shoppingcart.ShoppingCart;

/**
 * Assemble {@link CustomerOrder} from {@link org.yes.cart.shoppingcart.ShoppingCart}.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface OrderAssembler {


    /**
     * Create and fill {@link CustomerOrder} from given {@link ShoppingCart}.
     *
     * @param shoppingCart given shopping cart
     * @return order
     */
    CustomerOrder assembleCustomerOrder(ShoppingCart shoppingCart);

    /**
     * Create and fill {@link CustomerOrder} from given {@link ShoppingCart}.
     *
     * @param shoppingCart given shopping cart
     * @param temp         true if not all data need to be filled
     * @return order
     */
    CustomerOrder assembleCustomerOrder(ShoppingCart shoppingCart, boolean temp);

    /**
     *
     * Format given address to string.
     *
     * @param defaultAddress given address
     * @return formated address
     */
    String formatAddress(Address defaultAddress);



}
