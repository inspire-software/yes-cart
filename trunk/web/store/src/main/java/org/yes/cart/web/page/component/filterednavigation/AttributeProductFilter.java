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

package org.yes.cart.web.page.component.filterednavigation;

import org.apache.lucene.search.BooleanQuery;
import org.yes.cart.cache.Cacheable;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.query.impl.AttributiveSearchQueryBuilderImpl;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Product attribute value filtering component.
 * Supported simple value and value ranges, for example
 * product color can be one of Black, Red, White and
 * weight can be filtered within some range from 1 to 3 Kg, 3 - 5 Kg, 5 - 10 Kg
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/30/11
 * Time: 2:57 PM
 */
public class AttributeProductFilter extends AbstractProductFilter {

    private boolean filteredNavigationByAttribute = false;

    private Boolean visibilityRezult;

    /**
     * Construct attributive filtering component.
     *
     * @param id         panel id
     * @param query      current query.
     * @param categoryId current category id
     */
    public AttributeProductFilter(final String id, final BooleanQuery query, final long categoryId) {

        super(id, query, categoryId);

        if (categoryId > 0) {

            final String selectedLocale = getLocale().getLanguage();

            filteredNavigationByAttribute = getCategory().getNavigationByAttributes() == null ? false : getCategory().getNavigationByAttributes();

            final ProductType productType = getCategory().getProductType();

            if (filteredNavigationByAttribute && productType != null) {

                setNavigationRecords(
                        getFilteredNavigationRecords(
                                getProductService().getDistinctAttributeValues(selectedLocale, productType.getProducttypeId())
                        )
                );

            }
        }

    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeProductFlterImplMethodCache")
    List<FilteredNavigationRecord> getFilteredNavigationRecords(List<FilteredNavigationRecord> allNavigationRecords) {

        final AttributiveSearchQueryBuilderImpl queryBuilder = new AttributiveSearchQueryBuilderImpl();

        final List<FilteredNavigationRecord> navigationList = new ArrayList<FilteredNavigationRecord>();

        for (FilteredNavigationRecord record : allNavigationRecords) {

            if (!isAttributeAlreadyFiltered(ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD + ":" + record.getCode())) {

                final BooleanQuery candidateQuery = getQueryCandidate(queryBuilder, record);

                final int candidateResultCount = getProductService().getProductQty(candidateQuery);

                if (candidateResultCount > 0) {
                    record.setCount(candidateResultCount);
                    navigationList.add(record);
                }

            }

        }

        return navigationList;

    }


    private BooleanQuery getQueryCandidate(final AttributiveSearchQueryBuilderImpl queryBuilder, final FilteredNavigationRecord record) {

        final BooleanQuery booleanQuery;

        if ("S".equals(record.getType())) {
            booleanQuery = queryBuilder.createQuery(getCategories(), record.getCode(), record.getValue());
        } else { // range navigarion
            String[] range = record.getValue().split("-");
            booleanQuery = queryBuilder.createQueryWithRangeValues(getCategories(), record.getCode(),
                    new Pair<String, String>(range[0], range[1]));
        }

        return getLuceneQueryFactory().getSnowBallQuery(
                getQuery(),
                booleanQuery
        );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible() {

        if (filteredNavigationByAttribute) {

            if (visibilityRezult == null) {

                visibilityRezult = super.isVisible()
                        && getNavigationRecords() != null
                        && !getNavigationRecords().isEmpty();

            }

            return visibilityRezult;

        }

        return false;

    }
}
