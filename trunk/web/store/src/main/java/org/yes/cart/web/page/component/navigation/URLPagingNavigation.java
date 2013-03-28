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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

import java.util.Collections;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/14/11
 * Time: 11:04 AM
 */
public class URLPagingNavigation extends PagingNavigation {

    private final Class homePage;
    private final String activePageLinkHtmlClass;

    /**
     * {@inheritDoc}
     */
    public URLPagingNavigation(final String s, final IPageable iPageable, final IPagingLabelProvider iPagingLabelProvider, final String activePageLinkHtmlClass) {
        super(s, iPageable, iPagingLabelProvider);
        this.activePageLinkHtmlClass = activePageLinkHtmlClass;
        setViewSize(5);
        homePage = Application.get().getHomePage();
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractLink newPagingNavigationLink(final String id, final IPageable pageable, final int pageIndex) {

        final PageParameters pageParameters =
                WicketUtil.getFilteredRequestParameters(
                        getPage().getPageParameters(),
                        Collections.EMPTY_LIST);

        pageParameters.set(WebParametersKeys.PAGE, pageIndex);

        final AbstractLink rez =  new BookmarkablePageLink<Link>(id, homePage, pageParameters);

        if (WicketUtil.isSelectedPageActive(getPage().getPageParameters(), WebParametersKeys.PAGE, pageIndex)) {
            rez.add(new AttributeModifier("class", activePageLinkHtmlClass));
        }

        return  rez;
    }

}