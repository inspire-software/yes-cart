package org.yes.cart.service.order;

/**
 *
 * Generate unique order number.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface OrderNumberGenerator {

    /**
     * Generate Order number.
     * @return Generated order number.
     */
    String getNextOrderNumber();

}
