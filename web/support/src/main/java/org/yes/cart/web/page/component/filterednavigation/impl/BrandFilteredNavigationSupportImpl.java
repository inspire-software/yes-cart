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

import org.apache.lucene.search.BooleanQuery;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.query.impl.BrandSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.page.component.filterednavigation.BrandFilteredNavigationSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13-09-28
 * Time: 12:49 AM
 */
public class BrandFilteredNavigationSupportImpl extends AbstractFilteredNavigationSupportImpl implements BrandFilteredNavigationSupport {

    private final BrandSearchQueryBuilder queryBuilder = new BrandSearchQueryBuilder();

    public BrandFilteredNavigationSupportImpl(final LuceneQueryFactory luceneQueryFactory,
                                              final ProductService productService) {
        super(luceneQueryFactory, productService);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "filteredNavigationSupport-brandFilteredNavigationRecords")
    public List<FilteredNavigationRecord> getFilteredNavigationRecords(final BooleanQuery query,
                                                                       final List<Long> categories,
                                                                       final long shopId,
                                                                       final String locale,
                                                                       final String recordName) {

        final List<FilteredNavigationRecord> navigationList = new ArrayList<FilteredNavigationRecord>();

        if (categories != null && !isAttributeAlreadyFiltered(query, ProductSearchQueryBuilder.BRAND_FIELD)) {

            final List<FilteredNavigationRecord> allNavigationRecordsTemplates = getProductService().getDistinctBrands(locale, categories);

            for (final FilteredNavigationRecord recordTemplate : allNavigationRecordsTemplates) {

                final FilteredNavigationRecord record = recordTemplate.clone();
                final BooleanQuery candidateQuery = getLuceneQueryFactory().getSnowBallQuery(
                        query,
                        queryBuilder.createQuery(categories, shopId, record.getValue())
                );
                int candidateResultCount = getProductService().getProductQty(candidateQuery);
                if (candidateResultCount > 0) {
                    record.setName(recordName);
                    record.setCode(ProductSearchQueryBuilder.BRAND_FIELD);
                    record.setCount(candidateResultCount);
                    navigationList.add(record);
                }
            }

        }
        return navigationList;
    }
}
