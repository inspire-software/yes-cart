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
import org.yes.cart.shoppingcart.PricingPolicyProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 07/06/2016
 * Time: 18:39
 */
public class PricingPolicyProviderImpl implements PricingPolicyProvider, ConfigurationRegistry<String, PricingPolicyProvider> {

    private static final Logger LOG = LoggerFactory.getLogger(PricingPolicyProviderImpl.class);

    private final PricingPolicyProvider defaultPricingPolicyProvider;
    private final Map<String, PricingPolicyProvider> customPricingPolicyProviders = new HashMap<>();

    public PricingPolicyProviderImpl(final PricingPolicyProvider defaultPricingPolicyProvider) {
        this.defaultPricingPolicyProvider = defaultPricingPolicyProvider;
    }


    /** {@inheritDoc} */
    @Override
    public PricingPolicy determinePricingPolicy(final String shopCode, final String currency, final String customerEmail, String countryCode, String stateCode) {

        PricingPolicyProvider provider = getPricingPolicyProvider(shopCode);

        return provider.determinePricingPolicy(shopCode, currency, customerEmail, countryCode, stateCode);
    }

    protected PricingPolicyProvider getPricingPolicyProvider(final String shopCode) {
        PricingPolicyProvider provider = customPricingPolicyProviders.get(shopCode);
        if (provider == null) {
            provider = defaultPricingPolicyProvider;
        }
        return provider;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final Object configuration) {
        return configuration instanceof PricingPolicyProvider ||
                (configuration instanceof Class && PricingPolicyProvider.class.isAssignableFrom((Class<?>) configuration));
    }

    /** {@inheritDoc} */
    @Override
    public void register(final String shopCode, final PricingPolicyProvider provider) {

        if (provider != null) {
            LOG.info("Custom shop settings for {} registering pricing provider {}", shopCode, provider.getClass());
            customPricingPolicyProviders.put(shopCode, provider);
        } else {
            LOG.info("Custom shop settings for {} registering pricing provider DEFAULT", shopCode);
            customPricingPolicyProviders.remove(shopCode);
        }

    }
}
