/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.web.filter.payment;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.filter.AbstractFilter;
import org.yes.cart.web.support.request.IPResolver;
import org.yes.cart.web.support.util.HttpUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

/**
 * This filter perform initial handling of silent / hidden call back from payment gateway,
 * that perform external form processing. See "External payment form processing. Back/silent filter. Handler"
 * sequence diagram.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 11:44:41
 */
public class BasePaymentGatewayCallBackFilter extends AbstractFilter implements Filter {

    private final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade;
    private final ShopService shopService;
    private final IPResolver ipResolver;

    // Use Weak map to prevent memory leaks, Shop is kept in cache, so once it expires the pattern will expire too.
    private final Map<Shop, Pattern> patternCache = new WeakHashMap<Shop, Pattern>();
    private String paymentGatewayLabel;

    /**
     * Construct filter.
     *  @param paymentCallBackHandlerFacade handler.
     * @param shopService shop service
     * @param ipResolver  IP resolver
     */
    public BasePaymentGatewayCallBackFilter(final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade,
                                            final ShopService shopService,
                                            final IPResolver ipResolver) {
        this.paymentCallBackHandlerFacade = paymentCallBackHandlerFacade;
        this.shopService = shopService;
        this.ipResolver = ipResolver;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.paymentGatewayLabel = getFilterConfig().getInitParameter("paymentGatewayLabel");
    }

    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {

        final Logger log = ShopCodeContext.getLog(this);

        if (isCallerIpAllowed(servletRequest)) {

            if (log.isDebugEnabled()) {
                log.debug(HttpUtil.dumpRequest((HttpServletRequest) servletRequest));
            }

            final Map parameters = servletRequest.getParameterMap();

            try {

                paymentCallBackHandlerFacade.handlePaymentCallback(parameters, paymentGatewayLabel);

                ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_OK);

            } catch (OrderException e) {

                log.error("Transition failed during payment call back for " + paymentGatewayLabel + " payment gateway" , e);
                log.error(HttpUtil.dumpRequest((HttpServletRequest) servletRequest));

                // Send 500, so that PG know that there was an issue and may resend the update
                ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            }

        } else {

            if (log.isWarnEnabled()) {
                log.warn("Received payment gateway callback from unauthorised IP",
                        HttpUtil.dumpRequest((HttpServletRequest) servletRequest));
            }
            // Send forbidden to notify PG that this is a security issue and not error of any kind
            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN);

        }

        return null;  //no forwarding, just return
    }


    /**
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        //nothing
    }

    protected boolean isCallerIpAllowed(final ServletRequest servletRequest) {

        final Shop shop = shopService.getById(ShopCodeContext.getShopId());

        final Pattern allowedIps;
        if (patternCache.containsKey(shop)) {
            allowedIps = patternCache.get(shop);
        } else {
            final String cfg = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PAYMENT_GATEWAYS_ALLOWED_IPS_REGEX);
            if (StringUtils.isBlank(cfg)) {
                allowedIps = null;
            } else {
                allowedIps = Pattern.compile(cfg);
            }
            patternCache.put(shop, allowedIps);
        }

        if (allowedIps == null) {
            return true;
        }

        final String ip = ipResolver.resolve((HttpServletRequest) servletRequest);

        return allowedIps.matcher(ip).matches();
    }

}
