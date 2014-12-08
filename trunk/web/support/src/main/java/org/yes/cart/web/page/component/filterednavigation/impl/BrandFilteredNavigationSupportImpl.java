/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.web.page.component.filterednavigation.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.domain.queryobject.FilteredNavigationRecordRequest;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.domain.queryobject.impl.FilteredNavigationRecordRequestImpl;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.page.component.filterednavigation.BrandFilteredNavigationSupport;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13-09-28
 * Time: 12:49 AM
 */
public class BrandFilteredNavigationSupportImpl extends AbstractFilteredNavigationSupportImpl implements BrandFilteredNavigationSupport {

    private static final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    public BrandFilteredNavigationSupportImpl(final LuceneQueryFactory luceneQueryFactory,
                                              final ProductService productService) {
        super(luceneQueryFactory, productService);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "filteredNavigationSupport-brandFilteredNavigationRecords")
    public List<FilteredNavigationRecord> getFilteredNavigationRecords(final NavigationContext navigationContext,
                                                                       final String locale,
                                                                       final String recordName) {

        final List<FilteredNavigationRecord> navigationList = new ArrayList<FilteredNavigationRecord>();

        if (!navigationContext.isGlobal() && !navigationContext.isFilteredBy(ProductSearchQueryBuilder.BRAND_FIELD)) {

            final List<FilteredNavigationRecord> allNavigationRecordsTemplates = getProductService().getDistinctBrands(locale, navigationContext.getCategories());

            final FilteredNavigationRecordRequest request = new FilteredNavigationRecordRequestImpl("brandFacet", ProductSearchQueryBuilder.BRAND_FIELD);

            final Map<String, List<Pair<String, Integer>>> counts =
                    getProductService().findFilteredNavigationRecords(navigationContext.getProductQuery(), Collections.singletonList(request));


            final List<Pair<String, Integer>> rangeCounts = new ArrayList<Pair<String, Integer>>(counts.get("brandFacet"));

            if (rangeCounts.isEmpty()) {
                LOGFTQ.warn("Unable to get price filtered navigation for query: {}, request: {}", navigationContext.getProductQuery(), request);
                return Collections.emptyList();
            }

            for (final FilteredNavigationRecord recordTemplate : allNavigationRecordsTemplates) {

                final Iterator<Pair<String, Integer>> rangeCountsIt = rangeCounts.iterator();
                while (rangeCountsIt.hasNext()) {

                    final Pair<String, Integer> rangeCount = rangeCountsIt.next();
                    if (rangeCount.getFirst().equals(recordTemplate.getValue().toLowerCase())) {

                        final Integer candidateResultCount = rangeCount.getSecond();
                        if (candidateResultCount != null && candidateResultCount > 0) {
                            final FilteredNavigationRecord record = recordTemplate.clone();
                            record.setName(recordName);
                            record.setCode(ProductSearchQueryBuilder.BRAND_FIELD);
                            record.setCount(candidateResultCount);
                            navigationList.add(record);
                        }

                        rangeCountsIt.remove(); // we no longer need this count
                        break;

                    }
                }
            }

        }
        return navigationList;
    }
}
