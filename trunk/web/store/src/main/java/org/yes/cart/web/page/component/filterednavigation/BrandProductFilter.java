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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;

/**
 *
 * Simple product filtering by brand component.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/18/11
 * Time: 10:00 PM
 */
public class BrandProductFilter extends AbstractProductFilter {

    private boolean filteringNavigationAllowedIncategory = false;

    private Boolean visibilityRezult;

    @SpringBean(name = StorefrontServiceSpringKeys.FILTERNAV_SUPPORT_BRANDS)
    private BrandFilteredNavigationSupport brandsFilteredNavigationSupport;


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
            filteringNavigationAllowedIncategory = getCategory().getNavigationByBrand() == null ? false : getCategory().getNavigationByBrand();
        }

    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible() {

        if (filteringNavigationAllowedIncategory) {

            if (visibilityRezult == null) {

                final String selectedLocale = getLocale().getLanguage();

                setNavigationRecords(brandsFilteredNavigationSupport.getFilteredNavigationRecords(
                        getQuery(), getCategories(), ShopCodeContext.getShopId(), selectedLocale, getLocalizer().getString("brand", this))
                );

                visibilityRezult = super.isVisible()
                        && filteringNavigationAllowedIncategory
                        && getNavigationRecords() != null
                        && !getNavigationRecords().isEmpty();


            }

            return visibilityRezult;

        }

        return false;

    }

}
