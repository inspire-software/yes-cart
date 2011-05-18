package org.yes.cart.web.support.request;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * USed in multistore support.
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
