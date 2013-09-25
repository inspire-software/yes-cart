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

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.WebParametersKeys;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/14/11
 * Time: 12:27 PM
 */
public class URLPagingNavigator   extends PagingNavigator {


    private final IPagingLabelProvider labelProvider;
    private PagingNavigation pagingNavigation;
    private final PageParameters pageParameters;
    private final String activePageLinkHtmlClass;
    private final Class homePage;


    /**
     * {@inheritDoc}
     */
    public URLPagingNavigator(final String s, final IPageable iPageable, final PageParameters pageParameters, final String activePageLinkHtmlClass) {
        this(s, iPageable, null, pageParameters, activePageLinkHtmlClass);
    }

    /**
     * {@inheritDoc}
     */
    public URLPagingNavigator(final String s,
                              final IPageable iPageable,
                              final IPagingLabelProvider iPagingLabelProvider,
                              final PageParameters pageParameters,
                              final String activePageLinkHtmlClass) {
        super(s, iPageable, iPagingLabelProvider);
        this.labelProvider = iPagingLabelProvider;
        this.pageParameters = pageParameters;
        this.activePageLinkHtmlClass = activePageLinkHtmlClass;
        homePage = Application.get().getHomePage();
    }

    /**
     * {@inheritDoc}
     */
    protected PagingNavigation newNavigation(final String id,
                                             final IPageable pageable,
                                             final IPagingLabelProvider labelProvider) {
        return new URLPagingNavigation(id/*"navigation"*/, pageable, labelProvider, activePageLinkHtmlClass);
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractLink newPagingNavigationIncrementLink(final String id, final IPageable pageable, final int increment) {

        final LinksSupport links = ((AbstractWebPage) getPage()).getWicketSupportFacade().links();
        final PageParameters map = links.getFilteredCurrentParameters(pageParameters);
        map.set(WebParametersKeys.PAGE, pageable.getCurrentPage() + increment);

        return links.newLink(id, map);

        //return new PagingNavigationIncrementLink<Void>(id, pageable, increment);
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractLink newPagingNavigationLink(final String id, final IPageable pageable, int pageNumber) {

        final LinksSupport links = ((AbstractWebPage) getPage()).getWicketSupportFacade().links();
        final PageParameters params = links.getFilteredCurrentParameters(pageParameters);

        final int pNum;

        if ("last".equals(id)) {
            pNum = getPageable().getPageCount() - 1;
        } else {
            pNum = pageNumber;
        }

        params.set(WebParametersKeys.PAGE, pNum);

        return links.newLink(id, params);

    }

    @Override
    protected void onBeforeRender() {

        final Component component = get("first");

        if (component == null) {
            // Get the navigation bar and add it to the hierarchy

            pagingNavigation = newNavigation("navigation", getPageable(), labelProvider);
            add(pagingNavigation);

            int previousPage = getPageable().getCurrentPage() - 1;
            if (previousPage < 0) {
                previousPage = 0;
            }

            int nextPage = getPageable().getCurrentPage() + 1;
            if (nextPage > getPageable().getPageCount() - 1) {
                nextPage = getPageable().getPageCount() - 1;
            }

            int lastPage = getPageable().getPageCount() - 1;


            // Add additional page links
            add(newPagingNavigationLink("first", getPageable(), 0));//.add(new TitleAppender("PagingNavigator.first")));
            add(newPagingNavigationLink("prev", getPageable(), previousPage));//.add(new TitleAppender("PagingNavigator.first")));
            add(newPagingNavigationLink("next", getPageable(), nextPage));//.add(new TitleAppender("PagingNavigator.first")));
            add(newPagingNavigationLink("last", getPageable(), lastPage));//.add(new TitleAppender("PagingNavigator.last")));
        }
        super.onBeforeRender();
    }



}
