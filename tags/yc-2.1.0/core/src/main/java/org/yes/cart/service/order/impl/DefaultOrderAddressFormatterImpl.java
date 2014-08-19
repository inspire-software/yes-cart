package org.yes.cart.service.order.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.service.order.OrderAddressFormatter;

import java.text.MessageFormat;

/**
 * User: denispavlov
 * Date: 11/06/2014
 * Time: 10:03
 */
public class DefaultOrderAddressFormatterImpl implements OrderAddressFormatter {

    private final String addressTemplate;

    public DefaultOrderAddressFormatterImpl(final String addressTemplate) {
        this.addressTemplate = addressTemplate;
    }

    @Override
    public String formatAddress(final Address address) {
        if (address != null) {

            return MessageFormat.format(
                    addressTemplate,
                    StringUtils.defaultString(address.getAddrline1()),
                    StringUtils.defaultString(address.getAddrline2()),
                    StringUtils.defaultString(address.getPostcode()),
                    StringUtils.defaultString(address.getCity()),
                    StringUtils.defaultString(address.getCountryCode()),
                    StringUtils.defaultString(address.getStateCode()),
                    StringUtils.defaultString(address.getFirstname()),
                    StringUtils.defaultString(address.getLastname()),
                    StringUtils.defaultString(address.getPhoneList())
            );
        }
        return StringUtils.EMPTY;
    }
}
