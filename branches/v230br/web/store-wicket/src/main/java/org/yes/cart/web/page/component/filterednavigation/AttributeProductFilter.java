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

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;

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

    @SpringBean(name = StorefrontServiceSpringKeys.FILTERNAV_SUPPORT_ATTRIBUTES)
    private AttributeFilteredNavigationSupport attributeFilteredNavigationSupport;

    /**
     * Construct attributive filtering component.
     *
     * @param id         panel id
     * @param categoryId current category id
     * @param navigationContext navigation context.
     */
    public AttributeProductFilter(final String id, final long categoryId, final NavigationContext navigationContext) {

        super(id, categoryId, navigationContext);

        if (categoryId > 0L) {

            final String selectedLocale = getLocale().getLanguage();

            filteredNavigationByAttribute = getCategory().getNavigationByAttributes() == null ? false : getCategory().getNavigationByAttributes();

            final ProductType productType = getCategory().getProductType();

            if (filteredNavigationByAttribute && productType != null) {

                setNavigationRecords(
                        attributeFilteredNavigationSupport.getFilteredNavigationRecords(
                                getNavigationContext(), selectedLocale, productType.getProducttypeId())
                );

            }
        }

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
