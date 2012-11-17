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

package org.yes.cart.web.filter.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.filter.AbstractFilter;
import org.yes.cart.web.support.util.HttpUtil;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * This filter pefrom initial handling of silent / hidden call back from payment gateway,
 * that perform external form processing. See "External payment form processing. Back/silent filter. Handler"
 * sequence diagram.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 11:44:41
 */
public class BasePaymentGatewayCallBackFilter extends AbstractFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade;


    /**
     * Construct filter.
     *
     * @param paymentCallBackHandlerFacade handler.
     * @param applicationDirector app director
     */
    public BasePaymentGatewayCallBackFilter(
            final ApplicationDirector applicationDirector,
            final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade) {
        super(applicationDirector);
        this.paymentCallBackHandlerFacade = paymentCallBackHandlerFacade;
    }

    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {

        if (isCallerIpAllowed()) {

            if (LOG.isDebugEnabled()) {
                LOG.debug(HttpUtil.dumpRequest((HttpServletRequest) servletRequest));
            }

            final Map parameters = servletRequest.getParameterMap();

            final String paymentGatewayLabel = getFilterConfig().getInitParameter("paymentGatewayLabel");

            try {

                paymentCallBackHandlerFacade.handlePaymentCallback(parameters, paymentGatewayLabel);

            } catch (OrderException e) {

                LOG.error("Transition failed during payment call back for " + paymentGatewayLabel + " payment gateway" , e);

            }

            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_OK);

        } else {

            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_NOT_FOUND);

        }

        return null;  //no forwarding, just return
    }


    /**
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        //nothing
    }

    protected boolean isCallerIpAllowed() {
        return true; //TODOV2
    }

    /**
     * Get payment call back facade.
     * @return {@link PaymentCallBackHandlerFacade}
     */
    protected PaymentCallBackHandlerFacade getPaymentCallBackHandlerFacade() {
        return paymentCallBackHandlerFacade;
    }
}
