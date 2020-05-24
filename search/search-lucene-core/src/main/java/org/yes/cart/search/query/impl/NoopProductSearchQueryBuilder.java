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

package org.yes.cart.search.query.impl;

import org.apache.lucene.search.Query;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.query.ProductSearchQueryBuilder;

import java.util.List;

/**
 * No operation builder used to silence some parameters in param-to-builder maps.
 *
 * User: denispavlov
 * Date: 03/12/2014
 * Time: 21:06
 */
public class NoopProductSearchQueryBuilder implements ProductSearchQueryBuilder<Query> {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Query> createQueryChain(final NavigationContext<Query> navigationContext, final String parameter, final Object value) {
        return null;
    }
}
