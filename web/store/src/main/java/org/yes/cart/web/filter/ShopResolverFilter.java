package org.yes.cart.web.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.support.request.HttpServletRequestWrapper;
import org.yes.cart.web.support.service.ShopResolverService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    private final ShopResolverService shopResolverService;

    private final SystemService systemService;

    private final CategoryService categoryService;

    private final ShopService shopService;

    /**
     * @param shopResolverService service
     * @param systemService       service
     * @param categoryService     service
     * @param shopService         service
     */
    public ShopResolverFilter(final ShopResolverService shopResolverService,
                              final SystemService systemService,
                              final CategoryService categoryService,
                              final ShopService shopService) {
        super();
        this.shopResolverService = shopResolverService;
        this.systemService = systemService;
        this.shopService = shopService;
        this.categoryService = categoryService;
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

        final Shop shop = shopResolverService.getShop(servletRequest);

        if (shop == null) {
            final String url = systemService.getDefaultShopURL();
            if (LOG.isInfoEnabled()) {
                LOG.info("Shop can not be resolved. Redirect to : " + url);
            }
            ((HttpServletResponse) servletResponse).sendRedirect(url);
            return null;
        }

        final ShoppingCart shoppingCart = getShoppingCart(servletRequest, servletResponse);

        shoppingCart.getShoppingContext().setShopId(shop.getShopId());

        return getModifiedRequest(servletRequest, shop);

    }



    private ServletRequest getModifiedRequest(final ServletRequest servletRequest, final Shop shop) {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final String servletPath = httpServletRequest.getServletPath();
        if (StringUtils.isNotEmpty(servletPath)) {
            final String newServletPath = shop.getMarkupFolder() + servletPath;
            if (LOG.isDebugEnabled()) {
                LOG.debug("New servlet path is :" + newServletPath);
            }
            return new HttpServletRequestWrapper(httpServletRequest, newServletPath);
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
