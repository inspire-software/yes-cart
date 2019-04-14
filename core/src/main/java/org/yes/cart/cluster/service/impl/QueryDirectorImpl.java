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
package org.yes.cart.cluster.service.impl;

import org.yes.cart.cluster.service.QueryDirector;
import org.yes.cart.cluster.service.QueryDirectorPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 16:49
 */
public class QueryDirectorImpl implements QueryDirector {

    private final List<QueryDirectorPlugin> plugins;

    public QueryDirectorImpl(final List<QueryDirectorPlugin> plugins) {
        this.plugins = plugins;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> supportedQueries() {
        final List<String> types = new ArrayList<>();
        for (final QueryDirectorPlugin plugin : this.plugins) {
            types.addAll(plugin.supports());
        }
        return types;
    }

    /** {@inheritDoc} */
    @Override
    public List<Object[]> runQuery(final String type, final String query) {
        final String lcType = type.toLowerCase();
        for (final QueryDirectorPlugin plugin : this.plugins) {
            if (plugin.supports().contains(lcType)) {
                return plugin.runQuery(query);
            }
        }
        throw new UnsupportedOperationException("Query of type " + type + " are not supported");
    }
}
