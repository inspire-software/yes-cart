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
