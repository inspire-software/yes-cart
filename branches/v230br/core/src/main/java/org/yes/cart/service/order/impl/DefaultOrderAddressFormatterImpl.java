package org.yes.cart.service.order.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.service.order.OrderAddressFormatter;

import java.util.HashMap;
import java.util.Map;

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
        return formatAddressInternal(address, addressTemplate);
    }

    @Override
    public String formatAddress(final Address address, final String format) {
        if (StringUtils.isBlank(format)) {
            return formatAddressInternal(address, addressTemplate);
        }
        return formatAddressInternal(address, format);
    }

    private String formatAddressInternal(final Address address, final String format) {
        if (address != null) {


            final Map<String, String> values = new HashMap<String, String>();
            values.put("firstname", StringUtils.defaultString(address.getFirstname()));
            values.put("middlename", StringUtils.defaultString(address.getMiddlename()));
            values.put("lastname", StringUtils.defaultString(address.getLastname()));

            values.put("addrline1", StringUtils.defaultString(address.getAddrline1()));
            values.put("addrline2", StringUtils.defaultString(address.getAddrline2()));

            values.put("postcode", StringUtils.defaultString(address.getPostcode()));
            values.put("city", StringUtils.defaultString(address.getCity()));
            values.put("countrycode", StringUtils.defaultString(address.getCountryCode()));
            values.put("statecode", StringUtils.defaultString(address.getStateCode()));

            values.put("phone1", StringUtils.defaultString(address.getPhone1()));
            values.put("phone2", StringUtils.defaultString(address.getPhone2()));

            values.put("mobile1", StringUtils.defaultString(address.getMobile1()));
            values.put("mobile2", StringUtils.defaultString(address.getMobile2()));

            values.put("email1", StringUtils.defaultString(address.getEmail1()));
            values.put("email2", StringUtils.defaultString(address.getEmail2()));

            values.put("custom1", StringUtils.defaultString(address.getCustom1()));
            values.put("custom2", StringUtils.defaultString(address.getCustom2()));
            values.put("custom3", StringUtils.defaultString(address.getCustom3()));
            values.put("custom4", StringUtils.defaultString(address.getCustom4()));

            return new StrSubstitutor(values, "{{", "}}").replace(format);

        }
        return StringUtils.EMPTY;
    }
}
