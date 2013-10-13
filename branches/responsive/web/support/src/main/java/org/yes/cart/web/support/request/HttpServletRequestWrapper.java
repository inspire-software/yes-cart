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

package org.yes.cart.web.support.request;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * Used in multistore support.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 3:24:38 PM
 * <p/>
 * Wrapped servlet request.
 */
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

    private String servletPath;

    /**
     * Constructor.
     *
     * @param request     the <code>ServletRequest</code>
     * @param servletPath resloved servlet path. See <code>ServletRequest#getServletPath</code>
     */
    public HttpServletRequestWrapper(final ServletRequest request, final String servletPath) {
        super((HttpServletRequest) request);
        this.servletPath = servletPath;
    }

    /**
     * Constructor.
     *
     * @param request     the <code>HttpServletRequest</code>
     * @param servletPath resloved servlet path. See <code>ServletRequest#getServletPath</code>
     */
    public HttpServletRequestWrapper(final HttpServletRequest request, final String servletPath) {
        super(request);
        this.servletPath = servletPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServletPath() {
        return servletPath;
    }
}
