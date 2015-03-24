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

import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13-09-27
 * Time: 11:34 PM
 */
public interface BrandFilteredNavigationSupport {

    /**
     * Get navigation records for specific filter.
     *
     * @param navigationContext navigation context.
     * @param locale selected locale
     * @param recordName record localisable name
     *
     * @return distinct filter navigation records
     */
    List<FilteredNavigationRecord> getFilteredNavigationRecords(final NavigationContext navigationContext,
                                                                final String locale,
                                                                final String recordName);

}