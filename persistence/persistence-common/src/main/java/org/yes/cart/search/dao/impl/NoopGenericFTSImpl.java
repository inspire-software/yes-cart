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

package org.yes.cart.search.dao.impl;

import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.GenericFTS;
import org.yes.cart.search.dto.FilteredNavigationRecordRequest;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 20/03/2018
 * Time: 09:24
 */
public class NoopGenericFTSImpl<PK extends Serializable, FTQ> implements GenericFTS<PK, FTQ> {

    @Override
    public List<PK> fullTextSearchRaw(final String query) {
        return Collections.emptyList();
    }

    @Override
    public List<PK> fullTextSearch(final FTQ query) {
        return Collections.emptyList();
    }

    @Override
    public List<PK> fullTextSearch(final FTQ query, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse) {
        return Collections.emptyList();
    }

    @Override
    public Pair<List<Object[]>, Integer> fullTextSearch(final FTQ query, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse, final String... fields) {
        return new Pair<>(Collections.emptyList(), 0);
    }

    @Override
    public Map<String, List<Pair<Pair<String, I18NModel>, Integer>>> fullTextSearchNavigation(final FTQ query, final List<FilteredNavigationRecordRequest> facetingRequest) {
        return Collections.emptyMap();
    }

    @Override
    public int fullTextSearchCount(final FTQ query) {
        return 0;
    }
}
