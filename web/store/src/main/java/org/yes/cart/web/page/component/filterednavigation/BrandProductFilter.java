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
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.query.impl.BrandSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Simple product filtering by brand component.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/18/11
 * Time: 10:00 PM
 */
public class BrandProductFilter extends AbstractProductFilter {

    private boolean filteredNavigationByBrand = false;

    /**
     * Construct filtered navigation by brand.
     *
     * @param id         panel id
     * @param query      current query.
     * @param categoryId current category id
     */
    public BrandProductFilter(final String id, final BooleanQuery query, final long categoryId) {

        super(id, query, categoryId);

        if (categoryId > 0) {

            final String selectedLocale = getLocale().getLanguage();

            filteredNavigationByBrand = getCategory().getNavigationByBrand() == null ? false : getCategory().getNavigationByBrand();

            if (filteredNavigationByBrand) {

                setNavigationRecords(
                        getFilteredNavigationRecords(
                                getProductService().getDistinctBrands(selectedLocale, getCategories())
                        )
                );

            }
        }

    }

    /**
     * {@inheritDoc}
     */
    List<FilteredNavigationRecord> getFilteredNavigationRecords(
            final List<FilteredNavigationRecord> allNavigationRecords) {

        final List<FilteredNavigationRecord> navigationList = new ArrayList<FilteredNavigationRecord>();

        if (!isAttributeAlreadyFiltered(ProductSearchQueryBuilder.BRAND_FIELD)) {

            final BrandSearchQueryBuilder queryBuilder = new BrandSearchQueryBuilder();

            for (FilteredNavigationRecord record : allNavigationRecords) {
                BooleanQuery candidateQuery = getLuceneQueryFactory().getSnowBallQuery(
                        getQuery(),
                        queryBuilder.createQuery(getCategories(), record.getValue())
                );
                int candidateResultCount = getProductService().getProductQty(candidateQuery);
                if (candidateResultCount > 0) {
                    record.setName(getLocalizer().getString("brand", this));
                    record.setCode(ProductSearchQueryBuilder.BRAND_FIELD);
                    record.setCount(candidateResultCount);
                    navigationList.add(record);
                }
            }

        }
        return navigationList;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isVisible() {
        return  super.isVisible()
                && filteredNavigationByBrand
                && getNavigationRecords() != null
                && !getNavigationRecords().isEmpty();
    }

}
