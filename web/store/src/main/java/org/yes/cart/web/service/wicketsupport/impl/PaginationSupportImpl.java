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

package org.yes.cart.web.service.wicketsupport.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.service.wicketsupport.PaginationSupport;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

/**
 * User: denispavlov
 * Date: 13-06-28
 * Time: 9:27 AM
 */
public class PaginationSupportImpl implements PaginationSupport {


    /** {@inheritDoc} */
    @Override
    public boolean isSortSelected(final PageParameters pageParameters, final String sortOrder, final String sortField) {
        return WicketUtil.isSelectedProductSortOnPage(pageParameters, sortOrder, sortField);
    }

    /** {@inheritDoc} */
    @Override
    public void markSelectedSortLink(final Link link,
                                     final PageParameters pageParameters, final String sortOrder, final String sortField) {
        if (isSortSelected(pageParameters, sortOrder, sortField)) {
            link.add(new AttributeModifier("class", "sort-order-active"));
        } else {
            link.add(new AttributeModifier("class", "sort-order"));

        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPageSelected(final PageParameters pageParameters, final int pageIndex) {
        return WicketUtil.isSelectedPageActive(pageParameters, WebParametersKeys.PAGE, pageIndex);
    }

    /** {@inheritDoc} */
    @Override
    public void markSelectedPageLink(final Link link,
                                     final PageParameters pageParameters, final int pageIndex) {
        if (isPageSelected(pageParameters, pageIndex)) {
            link.add(new AttributeModifier("class", "nav-page-active"));
        } else {
            link.add(new AttributeModifier("class", "nav-page"));

        }
    }

    /** {@inheritDoc} */
    @Override
    public int getCurrentPage(PageParameters pageParameters) {

        final String currentPage = pageParameters.get(WebParametersKeys.PAGE).toString();
        int currentPageIdx = 0;
        if (currentPage != null) {
            currentPageIdx = NumberUtils.toInt(currentPage);
        }

        if (currentPageIdx < 0) {
            return 0;
        }
        return currentPageIdx;
    }

    /** {@inheritDoc} */
    @Override
    public void removePageParam(final PageParameters pageParameters) {
        pageParameters.remove(WebParametersKeys.PAGE);
    }
}
