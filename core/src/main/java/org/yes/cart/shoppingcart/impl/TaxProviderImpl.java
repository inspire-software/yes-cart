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

package org.yes.cart.shoppingcart.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.shoppingcart.TaxProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 07/06/2016
 * Time: 18:39
 */
public class TaxProviderImpl implements TaxProvider, ConfigurationRegistry<String, TaxProvider> {

    private static final Logger LOG = LoggerFactory.getLogger(TaxProviderImpl.class);

    private final TaxProvider defaultTaxProvider;
    private final Map<String, TaxProvider> customTaxProviders = new HashMap<String, TaxProvider>();

    public TaxProviderImpl(final TaxProvider defaultTaxProvider) {
        this.defaultTaxProvider = defaultTaxProvider;
    }

    /** {@inheritDoc} */
    @Override
    public Tax determineTax(final String shopCode, final String currency, final String countryCode, final String stateCode, final String itemCode) {
        return getTaxProvider(shopCode).determineTax(shopCode, currency, countryCode, stateCode, itemCode);
    }

    protected TaxProvider getTaxProvider(final String shopCode) {
        TaxProvider provider = customTaxProviders.get(shopCode);
        if (provider == null) {
            provider = defaultTaxProvider;
        }
        return provider;
    }

    /** {@inheritDoc} */
    public boolean supports(final Object configuration) {
        return configuration instanceof TaxProvider ||
                (configuration instanceof Class && TaxProvider.class.isAssignableFrom((Class<?>) configuration));
    }

    /** {@inheritDoc} */
    public void register(final String shopCode, final TaxProvider provider) {

        if (provider != null) {
            LOG.info("Custom shop settings for {} registering tax provider {}", shopCode, provider.getClass());
            customTaxProviders.put(shopCode, provider);
        } else {
            LOG.info("Custom shop settings for {} registering tax provider DEFAULT", shopCode);
            customTaxProviders.remove(shopCode);
        }

    }
}
