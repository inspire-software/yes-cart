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

package org.yes.cart.web.page.component.breadcrumbs;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;
import java.util.Set;

/**
 * Bread crumbs builder produce category and
 * attributive filtered navigation breadcrumbs based on
 * web query string and context.
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 9:50:51 AM
 */
public interface BreadCrumbsBuilder {



    /**
     * We have 2 kinds of breadcrumbs:
     * 1. category path, for example electronics -> phones -> ip phones
     * 2. attributive filters, for example ip phones [price range, brands, weight, ect]
     *
     * @param locale                locale
     * @param shopId                current shop id
     * @param customerShopId        current shop id
     * @param categoryId            current category id
     * @param pageParameters        current query string
     *
     * @return list of crumbs
     */
    List<Crumb> getBreadCrumbs(String locale,
                               long shopId,
                               long customerShopId,
                               long categoryId,
                               PageParameters pageParameters);




}
