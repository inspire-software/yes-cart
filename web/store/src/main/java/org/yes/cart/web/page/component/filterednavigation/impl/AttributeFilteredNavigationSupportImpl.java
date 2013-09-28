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
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.query.impl.AttributiveSearchQueryBuilderImpl;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.page.component.filterednavigation.AttributeFilteredNavigationSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13-09-28
 * Time: 12:17 AM
 */
public class AttributeFilteredNavigationSupportImpl extends AbstractFilteredNavigationSupportImpl implements AttributeFilteredNavigationSupport {

    public AttributeFilteredNavigationSupportImpl(final LuceneQueryFactory luceneQueryFactory,
                                                  final ProductService productService) {
        super(luceneQueryFactory, productService);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "filteredNavigationSupport-attributeFilteredNavigationRecords")
    public List<FilteredNavigationRecord> getFilteredNavigationRecords(final BooleanQuery query,
                                                                       final List<Long> categories,
                                                                       final String locale,
                                                                       final long productTypeId) {

        final List<FilteredNavigationRecord> allNavigationRecords = getProductService().getDistinctAttributeValues(locale, productTypeId);

        final AttributiveSearchQueryBuilderImpl queryBuilder = new AttributiveSearchQueryBuilderImpl();

        final List<FilteredNavigationRecord> navigationList = new ArrayList<FilteredNavigationRecord>();

        for (FilteredNavigationRecord record : allNavigationRecords) {

            if (!isAttributeAlreadyFiltered(query, ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD + ":" + record.getCode())) {

                final BooleanQuery candidateQuery = getQueryCandidate(query, categories, queryBuilder, record);

                final int candidateResultCount = getProductService().getProductQty(candidateQuery);

                if (candidateResultCount > 0) {
                    record.setCount(candidateResultCount);
                    navigationList.add(record);
                }

            }

        }

        return navigationList;

    }



    private BooleanQuery getQueryCandidate(final BooleanQuery query,
                                           final List<Long> categories,
                                           final AttributiveSearchQueryBuilderImpl queryBuilder,
                                           final FilteredNavigationRecord record) {

        final BooleanQuery booleanQuery;

        if ("S".equals(record.getType())) {
            booleanQuery = queryBuilder.createQuery(categories, record.getCode(), record.getValue());
        } else { // range navigation
            String[] range = record.getValue().split("-");
            booleanQuery = queryBuilder.createQueryWithRangeValues(categories, record.getCode(),
                    new Pair<String, String>(range[0], range[1]));
        }

        return getLuceneQueryFactory().getSnowBallQuery(query, booleanQuery);
    }




}
