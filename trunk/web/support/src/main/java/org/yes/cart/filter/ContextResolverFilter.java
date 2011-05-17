

package org.yes.cart.filter;

import org.yes.cart.shoppingcart.RequestRuntimeContainer;
import org.yes.cart.request.HttpServletRequestWrapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 3:29:31 PM
 *
 * This filter cover failover functionality for static resources via request manipulation.
 * It try to resolve content in default context, if content not present in shop context.
 * See also <b>docBase</b> for tomcat configuration parameter.
 *
 */
public class ContextResolverFilter extends AbstractFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ContextResolverFilter.class);


    /**
     * @param container current request container.
     */
    public ContextResolverFilter(final RequestRuntimeContainer container) {
        super(container);
    }

    private ServletRequest getWrappedServletRequest(final HttpServletRequest httpServletRequest,
                                                    final ServletContext servletContext, 
                                                    final String[] rootPathChain) {

        final String servletPath = httpServletRequest.getServletPath();
        for (String rootPath : rootPathChain) {
            final String resourceName = rootPath + servletPath;
            try {
                if (servletContext.getResource(resourceName) != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Retrieving resource: " + resourceName);
                    }
                    return new HttpServletRequestWrapper(httpServletRequest, resourceName);
                }
            } catch (MalformedURLException mue) {
                LOG.error("Uable to locate resouce from URL", mue);
            }
        }
        return httpServletRequest;

    }

    /** {@inheritDoc} */
    public ServletRequest doBefore(final ServletRequest servletRequest,
                         final ServletResponse servletResponse) throws IOException, ServletException {

        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        final String servletPath = httpServletRequest.getServletPath();

        if (StringUtils.isNotEmpty(servletPath)) {
            return getWrappedServletRequest(
                    httpServletRequest,
                    getFilterConfig().getServletContext(),
                    new String[] {
                            /*getRequestRuntimeContainer().getShop().getFspointer()*/"", //TODO not sure is it applicable ?
                            getRequestRuntimeContainer().getDefaultContextPath()
                    }
            );

        }
        return servletRequest;
    }

    /** {@inheritDoc} */
    public void doAfter(final ServletRequest servletRequest,
                        final ServletResponse servletResponse) throws IOException, ServletException {
        // NOTHING
    }
}