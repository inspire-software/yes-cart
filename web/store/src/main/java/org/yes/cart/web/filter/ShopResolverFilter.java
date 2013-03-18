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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.ChangeCurrencyEventCommandImpl;
import org.yes.cart.shoppingcart.impl.SetShopCartCommandImpl;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.request.HttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
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
public class ShopResolverFilter extends AbstractFilter implements Filter, ApplicationContextAware, ServletContextAware {

    private final SystemService systemService;

    private ApplicationContext applicationContext;

    private ServletContext servletContext;

    private final LanguageService languageService;


    /**
     * @param systemService service
     */
    public ShopResolverFilter(
            final ApplicationDirector applicationDirector,
            final SystemService systemService,
            final LanguageService languageService) {
        super(applicationDirector);
        this.systemService = systemService;
        this.languageService = languageService;
    }

    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {

        final Logger log = ShopCodeContext.getLog();
        if (log.isDebugEnabled()) {
            log.debug(MessageFormat.format("Request id {0} start at {1}",
                    servletRequest.toString(),
                    (new Date()).getTime()));
        }

        final String serverDomainName = servletRequest.getServerName().toLowerCase();

        final Shop shop = getApplicationDirector().getShopByDomainName(serverDomainName);

        if (shop == null) {
            final String url = systemService.getDefaultShopURL();
            if (log.isInfoEnabled()) {
                log.info("Shop can not be resolved. Redirect to : " + url);
            }
            ((HttpServletResponse) servletResponse).sendRedirect(url);
            return null;
        }

        setDefaultValues(shop);

        ApplicationDirector.setCurrentShop(shop);
        ShopCodeContext.setShopCode(shop.getCode());
        //ApplicationDirector.setCurrentServletContext(servletContext);
        ApplicationDirector.setCurrentMailTemplateFolder(servletContext.getRealPath(shop.getMailFolder()) + File.separator);


        return getModifiedRequest(servletRequest, shop);

    }

    /**
     * Set default values. Mostly for new cart.
     * @param shop shop
     */
    private void setDefaultValues(final Shop shop) {

        final ShoppingCart shoppingCart = ApplicationDirector.getShoppingCart();

        if (shoppingCart.getCurrencyCode() == null) { // new cart only may satisfy this condition

            new SetShopCartCommandImpl(applicationContext, Collections.singletonMap(SetShopCartCommandImpl.CMD_KEY, shop.getShopId()))
                    .execute(shoppingCart);

            new ChangeCurrencyEventCommandImpl(applicationContext, Collections.singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, shop.getDefaultCurrency()))
                    .execute(shoppingCart);

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
            final Logger log = ShopCodeContext.getLog();
            final String newServletPath = shop.getMarkupFolder() + servletPath;
            try {

                if (log.isDebugEnabled()) {
                    log.debug("New servlet path is :" + newServletPath);
                }
                return new HttpServletRequestWrapper(httpServletRequest, newServletPath);
            } catch (/*MalformedURL*/Exception e) {
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
        final Logger log = ShopCodeContext.getLog();
        if (log.isDebugEnabled()) {
            log.debug(MessageFormat.format("Request id {0}   end at {1}",
                    servletRequest.toString(),
                    (new Date()).getTime()));
        }

    }

    /** {@inheritDoc} */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /** {@inheritDoc} */
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
