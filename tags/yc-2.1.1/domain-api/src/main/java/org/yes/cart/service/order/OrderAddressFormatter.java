package org.yes.cart.service.order;

import org.yes.cart.domain.entity.Address;

/**
 * User: denispavlov
 * Date: 11/06/2014
 * Time: 10:02
 */
public interface OrderAddressFormatter {

    /**
     * Format address.
     *
     * @param address address to format
     *
     * @return string representation of this address
     */
    String formatAddress(final Address address);

}
