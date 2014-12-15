package org.yes.cart.shoppingcart.impl;

import org.yes.cart.domain.entity.Customer;

/**
 * Format customer full name.
 * May depend from country or locale
 */
public class NameFormatImpl {

    public String formatFullName(final Customer customer) {
        return customer.getFirstname() + " " + customer.getLastname();
    }
}
