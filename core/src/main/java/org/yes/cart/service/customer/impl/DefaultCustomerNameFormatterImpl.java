/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.service.customer.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.customer.CustomerNameFormatter;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 06/03/2015
 * Time: 20:45
 */
public class DefaultCustomerNameFormatterImpl implements CustomerNameFormatter {


    private final String nameTemplate;

    public DefaultCustomerNameFormatterImpl(final String nameTemplate) {
        this.nameTemplate = nameTemplate;
    }

    @Override
    public String formatName(final Address address) {
        return formatNameInternal(address, nameTemplate);
    }

    @Override
    public String formatName(final Address address, final String format) {
        if (StringUtils.isBlank(format)) {
            return formatNameInternal(address, nameTemplate);
        }
        return formatNameInternal(address, format);
    }

    private String formatNameInternal(final Address address, final String format) {
        if (address != null) {

            final Map<String, String> values = new HashMap<String, String>();
            values.put("salutation", StringUtils.defaultString(address.getSalutation()));
            values.put("firstname", StringUtils.defaultString(address.getFirstname()));
            values.put("middlename", StringUtils.defaultString(address.getMiddlename()));
            values.put("lastname", StringUtils.defaultString(address.getLastname()));

            return new StrSubstitutor(values, "{{", "}}").replace(format);

        }
        return StringUtils.EMPTY;
    }

    @Override
    public String formatName(final Customer customer) {
        return formatNameInternal(customer, nameTemplate);
    }

    @Override
    public String formatName(final Customer customer, final String format) {
        if (StringUtils.isBlank(format)) {
            return formatNameInternal(customer, nameTemplate);
        }
        return formatNameInternal(customer, format);
    }


    private String formatNameInternal(final Customer customer, final String format) {
        if (customer != null) {

            final Map<String, String> values = new HashMap<String, String>();
            values.put("salutation", StringUtils.defaultString(customer.getSalutation()));
            values.put("firstname", StringUtils.defaultString(customer.getFirstname()));
            values.put("middlename", StringUtils.defaultString(customer.getMiddlename()));
            values.put("lastname", StringUtils.defaultString(customer.getLastname()));

            return new StrSubstitutor(values, "{{", "}}").replace(format);

        }
        return StringUtils.EMPTY;
    }


}
