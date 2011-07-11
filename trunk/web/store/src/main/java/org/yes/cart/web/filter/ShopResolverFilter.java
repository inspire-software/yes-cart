package org.yes.cart.web.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.request.HttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.Date;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 4:46:09 PM
 * <p/>
 * Shop resolver filter.
 * If shop can not be resolved by server/domain name
 * filter redirect to default url.
 */
public class ShopResolverFilter extends AbstractFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ShopResolverFilter.class);

    private final SystemService systemService;


    /**
     * @param systemService service
     */
    public ShopResolverFilter(
            final ApplicationDirector applicationDirector,
            final SystemService systemService) {
        super(applicationDirector);
        this.systemService = systemService;
    }

    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {
        if (LOG.isDebugEnabled()) {
            LOG.debug(MessageFormat.format("Request id {0} start at {1}",
                    servletRequest.toString(),
                    (new Date()).getTime()));
        }

        final String serverDomainName = servletRequest.getServerName().toLowerCase();

        final Shop shop = getApplicationDirector().getShopByDomainName(serverDomainName);

        if (shop == null) {
            final String url = systemService.getDefaultShopURL();
            if (LOG.isInfoEnabled()) {
                LOG.info("Shop can not be resolved. Redirect to : " + url);
            }
            ((HttpServletResponse) servletResponse).sendRedirect(url);
            return null;
        }

        ApplicationDirector.setCurrentShop(shop);

        final ShoppingCart shoppingCart = (ShoppingCart) servletRequest.getAttribute(WebParametersKeys.REQUEST_SHOPPING_CART);

        shoppingCart.getShoppingContext().setShopId(shop.getShopId());

        return getModifiedRequest(servletRequest, shop);

    }


    /**
     * Create http serlet wrapper to handle multi store requests.
     *
     * @param servletRequest current request
     * @param shop           resolved shop
     * @return servlet wrapper
     */
    private ServletRequest getModifiedRequest(final ServletRequest servletRequest, final Shop shop) {

        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final String servletPath = httpServletRequest.getServletPath();

        if (StringUtils.isNotEmpty(servletPath)) {
            final String newServletPath = shop.getMarkupFolder() + servletPath;
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("New servlet path is :" + newServletPath);
                }
                return new HttpServletRequestWrapper(httpServletRequest, newServletPath);
            } catch (/*MalformedURL*/Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Wrong URL for path : " + newServletPath, e);
                }
            }
        }

        return servletRequest;
    }


    /**
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest,
                        final ServletResponse servletResponse) throws IOException, ServletException {
        if (LOG.isDebugEnabled()) {
            LOG.debug(MessageFormat.format("Request id {0}   end at {1}",
                    servletRequest.toString(),
                    (new Date()).getTime()));
        }

    }

}
