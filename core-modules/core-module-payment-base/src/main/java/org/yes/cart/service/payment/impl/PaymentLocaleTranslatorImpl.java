package org.yes.cart.service.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.service.payment.PaymentLocaleTranslator;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 29/12/2015
 * Time: 14:35
 */
public class PaymentLocaleTranslatorImpl implements PaymentLocaleTranslator {

    /**
     * {@inheritDoc}
     */
    @Override
    public String translateLocale(final PaymentGateway paymentGateway, final String locale) {

        final String mapping = paymentGateway.getParameterValue("LANGUAGE_MAP");
        if (StringUtils.isNotBlank(mapping)) {

            final Map<String, String> all = new HashMap<String, String>();

            final String[] map = StringUtils.split(mapping, ',');
            for (final String mapItem : map) {
                final String[] item = StringUtils.split(mapItem.trim(), '=');
                all.put(item[0].trim(), item[1].trim());
            }

            if (all.containsKey(locale)) {
                return all.get(locale);
            }

        }

        return locale;
    }
}
