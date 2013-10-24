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

package org.yes.cart.web.service.wicketsupport;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Support for pagination
 *
 * User: denispavlov
 * Date: 13-06-28
 * Time: 8:30 AM
 */
public interface PaginationSupport {

    /**
     * Check if current sort option is selected.
     *
     * @param pageParameters current page parameters
     * @param sortField sorting field
     * @param sortOrder order of sorting (asc / desc)
     *
     * @return true if this is the current sorting option
     */
    boolean isSortSelected(PageParameters pageParameters, String sortField, String sortOrder);


    /**
     * Marks link as selected by adding css class.
     *
     * @param link sorting link to check
     * @param cssClass class to use for "class" attribute in markup (if this is selected link)
     * @param pageParameters current page parameters
     * @param sortField sorting field
     * @param sortOrder order of sorting (asc / desc)
     */
    void markSelectedSortLink(Link link,
                              String cssClass,
                              PageParameters pageParameters, String sortField, String sortOrder);

    /**
     * Checks if current page is selected.
     *
     * @param pageParameters current page parameters
     * @param pageIndex index to check
     *
     * @return true if page index is currently active
     */
    boolean isPageSelected(PageParameters pageParameters, int pageIndex);

    /**
     * Marks link as selected by adding css class.
     *
     * @param link sorting link to check
     * @param cssClass class to use for "class" attribute in markup (if this is selected link)
     * @param pageParameters current page parameters
     * @param pageIndex index to check
     */
    void markSelectedPageLink(Link link,
                              String cssClass,
                              PageParameters pageParameters, int pageIndex);

    /**
     *
     * Get current page index
     *
     * @param pageParameters current page parameters
     * @return current page index
     */
    int getCurrentPage(PageParameters pageParameters);
}
