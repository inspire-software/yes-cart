package org.yes.cart.web.filter;


import org.yes.cart.web.application.ApplicationDirector;

import javax.servlet.*;
import java.io.IOException;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 11:56:24 AM
 */
public abstract class AbstractFilter {

    private FilterConfig filterConfig = null;

    private final ApplicationDirector applicationDirector;

    /**
     * Construct filter.
     * @param applicationDirector app director.
     */
    public AbstractFilter(final ApplicationDirector applicationDirector) {
        this.applicationDirector = applicationDirector;
    }

    /**
     * Get app director.
     * @return {@link ApplicationDirector}
     */
    public ApplicationDirector getApplicationDirector() {
        return applicationDirector;
    }

    /**
     * {@inheritDoc}
     */
    public final void doFilter(final ServletRequest servletRequest,
                               final ServletResponse servletResponse,
                               final FilterChain filterChain) throws IOException, ServletException {
        final ServletRequest passRequest = doBefore(servletRequest, servletResponse);
        if (passRequest == null) {
            return;
        }
        filterChain.doFilter(passRequest, servletResponse);
        doAfter(passRequest, servletResponse);
    }

    /**
     * Do all before next item in chain.
     *
     * @param servletRequest  the request
     * @param servletResponse the response
     * @return request to pass onto chain (return null to terminate chain).
     * @throws IOException      as with filter
     * @throws ServletException as with filter
     */
    public abstract ServletRequest doBefore(final ServletRequest servletRequest,
                                            final ServletResponse servletResponse) throws IOException, ServletException;

    /**
     * Do all before next item in chain.
     *
     * @param servletRequest  the request
     * @param servletResponse the response
     * @throws IOException      as with filter
     * @throws ServletException as with filter
     */
    public abstract void doAfter(final ServletRequest servletRequest,
                                 final ServletResponse servletResponse) throws IOException, ServletException;

    /**
     * {@inheritDoc}
     */
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        filterConfig = null;
    }

    /**
     * @return filter config
     */
    protected FilterConfig getFilterConfig() {
        return filterConfig;
    }


}
