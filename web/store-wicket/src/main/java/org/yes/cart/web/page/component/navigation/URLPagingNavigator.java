/*
 * Copyright 2009 Inspire-Software.com
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

import org.apache.wicket.AttributeModifier;
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


    public URLPagingNavigator(final String s, final IPageable iPageable, final PageParameters pageParameters) {
        this(s, iPageable, null, pageParameters);
    }

    public URLPagingNavigator(final String s,
                              final IPageable iPageable,
                              final IPagingLabelProvider iPagingLabelProvider,
                              final PageParameters pageParameters) {
        super(s, iPageable, iPagingLabelProvider);
        this.labelProvider = iPagingLabelProvider;
        this.pageParameters = pageParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PagingNavigation newNavigation(final String id,
                                             final IPageable pageable,
                                             final IPagingLabelProvider labelProvider) {
        return new URLPagingNavigation(id, pageable, labelProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractLink newPagingNavigationIncrementLink(final String id, final IPageable pageable, final int increment) {

        final LinksSupport links = ((AbstractWebPage) getPage()).getWicketSupportFacade().links();
        final PageParameters map = links.getFilteredCurrentParameters(pageParameters);

        final long total = pageable.getPageCount();
        final long current = pageable.getCurrentPage();
        final long tryPage = current + increment;

        map.set(WebParametersKeys.PAGE, tryPage < 0 ? 0 : tryPage >= total ? total - 1 : tryPage);

        return (AbstractLink) links.newLink(id, map).add(new AttributeModifier("class", "nav-page-control"));

        //return new PagingNavigationIncrementLink<Void>(id, pageable, increment);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractLink newPagingNavigationLink(final String id, final IPageable pageable, int pageNumber) {

        final LinksSupport links = ((AbstractWebPage) getPage()).getWicketSupportFacade().links();
        final PageParameters params = links.getFilteredCurrentParameters(pageParameters);

        final long pNum;

        if ("last".equals(id)) {
            pNum = getPageable().getPageCount() - 1;
        } else {
            pNum = pageNumber;
        }

        params.set(WebParametersKeys.PAGE, pNum);

        return (AbstractLink) links.newLink(id, params).add(new AttributeModifier("class", "nav-page-control " + id));

    }

    @Override
    protected void onBeforeRender() {

        final Component component = get("first");

        if (component == null) {
            // Get the navigation bar and add it to the hierarchy

            pagingNavigation = newNavigation("navigation", getPageable(), labelProvider);
            add(pagingNavigation);

            long previousPage = getPageable().getCurrentPage() - 1;
            if (previousPage < 0) {
                previousPage = 0;
            }

            long nextPage = getPageable().getCurrentPage() + 1;
            if (nextPage > getPageable().getPageCount() - 1) {
                nextPage = getPageable().getPageCount() - 1;
            }

            long lastPage = getPageable().getPageCount() - 1;


            // Add additional page links
            add(newPagingNavigationLink("first", getPageable(), 0));//.add(new TitleAppender("PagingNavigator.first")));
            add(newPagingNavigationLink("prev", getPageable(), (int) previousPage));//.add(new TitleAppender("PagingNavigator.first")));
            add(newPagingNavigationLink("next", getPageable(), (int) nextPage));//.add(new TitleAppender("PagingNavigator.first")));
            add(newPagingNavigationLink("last", getPageable(), (int) lastPage));//.add(new TitleAppender("PagingNavigator.last")));
        }
        super.onBeforeRender();
    }



}
