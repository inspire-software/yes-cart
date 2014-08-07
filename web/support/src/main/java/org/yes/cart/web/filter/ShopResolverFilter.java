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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.request.HttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 4:46:09 PM
 * <p/>
 * Shop resolver filter.
 * If shop can not be resolved by server/domain name
 * filter redirect to default url.
 */
public class ShopResolverFilter extends AbstractFilter implements Filter, ServletContextAware {

    private final SystemService systemService;

    private ServletContext servletContext;

    private final ShoppingCartCommandFactory cartCommandFactory;


    public ShopResolverFilter(
            final ApplicationDirector applicationDirector,
            final SystemService systemService,
            final ShoppingCartCommandFactory cartCommandFactory) {
        super(applicationDirector);
        this.systemService = systemService;
        this.cartCommandFactory = cartCommandFactory;
    }

    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {

        final String serverDomainName = servletRequest.getServerName().toLowerCase();

        final Shop shop = getApplicationDirector().getShopByDomainName(serverDomainName);

        if (shop == null) {
            final String url = systemService.getDefaultShopURL();
            final Logger log = ShopCodeContext.getLog(this);
            if (log.isInfoEnabled()) {
                log.info("Shop can not be resolved. For server name [" + serverDomainName + "] Redirect to : [" + url + "]");
            }
            ((HttpServletResponse) servletResponse).sendRedirect(url);
            return null;
        }

        setDefaultValues(shop);

        ApplicationDirector.setCurrentShop(shop);
        ApplicationDirector.setShopperIPAddress(getRemoteIpAddr(servletRequest));
        ShopCodeContext.setShopCode(shop.getCode());
        ShopCodeContext.setShopId(shop.getShopId()) ;
        //ApplicationDirector.setCurrentServletContext(servletContext);
        ApplicationDirector.setCurrentMailTemplateFolder(servletContext.getRealPath(shop.getMailFolder()) + File.separator);


        return getModifiedRequest(servletRequest, shop);

    }

    private String getRemoteIpAddr(final ServletRequest servletRequest) {
        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String userIpAddress = httpRequest.getHeader("X-Forwarded-For");
        if (userIpAddress == null) {
            return httpRequest.getRemoteAddr();
        }
        return userIpAddress;
    }

    /**
     * Set default values. Mostly for new cart.
     * @param shop shop
     */
    private void setDefaultValues(final Shop shop) {

        final ShoppingCart shoppingCart = ApplicationDirector.getShoppingCart();
        if (shoppingCart != null) { // this may happen if shoppingCart filter is not assigned to this url pattern
            if (shoppingCart.getCurrencyCode() == null) { // new cart only may satisfy this condition

                cartCommandFactory.execute(shoppingCart, new HashMap<String, Object>() {{
                    put(ShoppingCartCommand.CMD_SETSHOP, shop.getShopId());
                    put(ShoppingCartCommand.CMD_CHANGECURRENCY, shop.getDefaultCurrency());
                }});

            }
        }
    }



    /**
     * Create http servlet wrapper to handle multi store requests.
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
                return new HttpServletRequestWrapper(httpServletRequest, newServletPath);
            } catch (/*MalformedURL*/Exception e) {
                final Logger log = ShopCodeContext.getLog(this);
                if (log.isErrorEnabled()) {
                    log.error("Wrong URL for path : " + newServletPath, e);
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

        ApplicationDirector.clear();
        ShopCodeContext.clear();

    }

    /** {@inheritDoc} */
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
