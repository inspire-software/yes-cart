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

package org.yes.cart.web.support.service.impl;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.service.CentralViewResolver;

import java.util.List;
import java.util.Map;

/**
 * Service responsible to resolve label of central view in storefront.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/26/11
 * Time: 8:50 PM
 */
public class CentralViewResolverFactoryImpl implements CentralViewResolver {

    private List<CentralViewResolver> resolvers;

    private static final Pair<String, String> DEFAULT = new Pair<String, String>(CentralViewLabel.DEFAULT, CentralViewLabel.DEFAULT);

    public CentralViewResolverFactoryImpl(final List<CentralViewResolver> resolvers) {
        this.resolvers = resolvers;
    }

    /**
     * Resolve central renderer label.
     *
     * @param parameters request parameters map
     *
     * @return resolved main panel renderer label
     */
    public Pair<String, String> resolveMainPanelRendererLabel(final Map parameters) {

        for (final CentralViewResolver resolver : resolvers) {
            final Pair<String, String> label = resolver.resolveMainPanelRendererLabel(parameters);
            if (label != null) {
                return label;
            }
        }
        return DEFAULT; // global failover
    }

}
