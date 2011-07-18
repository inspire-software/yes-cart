package org.yes.cart.web.page.component.navigation;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/14/11
 * Time: 12:27 PM
 */
public class URLPagingNavigator   extends PagingNavigator {


    private final IPagingLabelProvider labelProvider;
    private PagingNavigation pagingNavigation;
    private final PageParameters pageParameters;


    /**
     * {@inheritDoc}
     */
    public URLPagingNavigator(final String s, final IPageable iPageable, final PageParameters pageParameters) {
        this(s, iPageable, null, pageParameters);
    }

    /**
     * {@inheritDoc}
     */
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
    protected PagingNavigation newNavigation(final IPageable pageable,
                                             final IPagingLabelProvider labelProvider) {
        return new URLPagingNavigation("navigation", pageable, labelProvider);
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractLink newPagingNavigationIncrementLink(final String id, final IPageable pageable, final int increment) {

        final PageParameters map = WicketUtil.getFilteredRequestParameters(pageParameters);

        map.add(WebParametersKeys.PAGE, pageable.getCurrentPage() + increment);

        return new BookmarkablePageLink<Link>(id, HomePage.class, map);


        //return new PagingNavigationIncrementLink<Void>(id, pageable, increment);
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractLink newPagingNavigationLink(final String id, final IPageable pageable, int pageNumber) {
        final PageParameters params = WicketUtil.getFilteredRequestParameters(pageParameters);

        params.set(WebParametersKeys.PAGE, pageNumber);

        return new BookmarkablePageLink<Link>(id, HomePage.class, new PageParameters(params));

    }

    @Override
    protected void onBeforeRender() {

        if (get("first") == null) {
            // Get the navigation bar and add it to the hierarchy

            pagingNavigation = newNavigation(getPageable(), labelProvider);
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
