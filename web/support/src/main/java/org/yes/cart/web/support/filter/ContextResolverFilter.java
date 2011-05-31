package org.yes.cart.web.support.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.web.support.request.HttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 3:29:31 PM
 * <p/>
 * This filter cover failover functionality for static resources via request manipulation.
 * It try to resolve content in default context, if content not present in shop context.
 * See also <b>docBase</b> for tomcat configuration parameter.
 */
public class ContextResolverFilter extends AbstractFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ContextResolverFilter.class);




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

    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {

        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        final String servletPath = httpServletRequest.getServletPath();

        if (StringUtils.isNotEmpty(servletPath)) {
            return getWrappedServletRequest(
                    httpServletRequest,
                    getFilterConfig().getServletContext(),
                    new String[]{
                            /*getRequestRuntimeContainer().getShop().getFspointer()*/"", //TODO not sure is it applicable ?
                            "getRequestRuntimeContainer().getDefaultContextPath()"     //TODO WTF
                    }
            );

        }
        return servletRequest;
    }

    /**
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest,
                        final ServletResponse servletResponse) throws IOException, ServletException {
        // NOTHING
    }
}