/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.i18n.I18NModel;
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

            final Map<String, String> all = new HashMap<>();

            final String[] map = StringUtils.split(mapping, ',');
            for (final String mapItem : map) {
                final String[] item = StringUtils.split(mapItem.trim(), '=');
                all.put(item[0].trim(), item[1].trim());
            }

            if (all.containsKey(locale)) {
                return all.get(locale);
            }

            if (all.containsKey(I18NModel.DEFAULT)) {
                return all.get(I18NModel.DEFAULT);
            }

        }

        return locale;
    }
}
