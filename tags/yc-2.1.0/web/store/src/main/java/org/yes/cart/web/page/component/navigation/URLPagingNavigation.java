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

package org.yes.cart.web.page.component.navigation;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.service.wicketsupport.PaginationSupport;
import org.yes.cart.web.support.constants.WebParametersKeys;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/14/11
 * Time: 11:04 AM
 */
public class URLPagingNavigation extends PagingNavigation {


    /**
     * {@inheritDoc}
     */
    public URLPagingNavigation(final String s, final IPageable iPageable, final IPagingLabelProvider iPagingLabelProvider) {
        super(s, iPageable, iPagingLabelProvider);
        setViewSize(5);
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractLink newPagingNavigationLink(final String id, final IPageable pageable, final int pageIndex) {

        final LinksSupport links = ((AbstractWebPage) getPage()).getWicketSupportFacade().links();
        final PaginationSupport pagination = ((AbstractWebPage) getPage()).getWicketSupportFacade().pagination();

        final PageParameters pageParameters = links.getFilteredCurrentParameters(getPage().getPageParameters());
        pageParameters.set(WebParametersKeys.PAGE, pageIndex);

        final Link rez = links.newLink(id, pageParameters);
        pagination.markSelectedPageLink(rez, getPage().getPageParameters(), pageIndex);

        return  rez;
    }

}