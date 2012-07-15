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

package org.yes.cart.web.page.component.breadcrumbs;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 8:38:04 AM
 */
public interface CrumbNamePrefixProvider {

    /**
     * Get the localized name of breadcrumb for given key.
     *
     * @param key given key
     * @return localized prefix if localization found, otherwise key.
     */
    String getLinkNamePrefix(String key);

    /**
     * Get adapted value for link label
     *
     * @param key   given key
     * @param value to adapt
     * @return localized adapted value if localization found, otherwise key.
     */
    String getLinkName(String key, String value);

}
