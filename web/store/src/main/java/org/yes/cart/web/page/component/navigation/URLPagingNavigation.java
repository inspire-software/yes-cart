package org.yes.cart.web.page.component.navigation;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/14/11
 * Time: 11:04 AM
 */
public class URLPagingNavigation extends PagingNavigation {

    /**
     * {@inheritDoc}
     */
    public URLPagingNavigation(final String s, final IPageable iPageable) {
        super(s, iPageable);
    }

    /**
     * {@inheritDoc}
     */
    public URLPagingNavigation(final String s, final IPageable iPageable, final IPagingLabelProvider iPagingLabelProvider) {
        super(s, iPageable, iPagingLabelProvider);
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractLink newPagingNavigationLink(final String id, final IPageable pageable, final int pageIndex) {

        final PageParameters pageParameters =
                WicketUtil.getFilteredRequestParameters(
                        WicketUtil.getPageParametes(),
                        Collections.EMPTY_LIST);

        pageParameters.set(WebParametersKeys.PAGE, pageIndex);

        return new BookmarkablePageLink<Link>(id, HomePage.class, pageParameters);
    }

}