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

import org.slf4j.Logger;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.payment.PaymentGatewayPayPalExpressCheckout;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayCallback;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.HttpParamsUtils;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.request.IPResolver;
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
 * This filter will server interaction with pay pall express checkout gateway.
 * And must me mapped to PayPalExpressCheckoutPaymentGatewayImpl#getPostActionUrl value,
 * by default it has value "paymentpaypalexpress".
 * <p/>
 * Action flow is following:
 * get the TOKEN from paypal express checkout gateway
 * redirect to paypal EC login page
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/18/11
 * Time: 5:50 PM
 */
public class PayPalExpressCheckoutFilter extends BasePaymentGatewayCallBackFilter implements Filter {

    private final PaymentProcessorFactory paymentProcessorFactory;

    private final CustomerOrderService customerOrderService;

    private final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade;

    /**
     * Construct filter.
     *
     * @param paymentProcessorFactory payment processor.
     * @param customerOrderService  {@link CustomerOrderService}     to use
     * @param paymentCallBackHandlerFacade handler.
     */
    public PayPalExpressCheckoutFilter(final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade,
                                       final ShopService shopService,
                                       final IPResolver ipResolver,
                                       final PaymentProcessorFactory paymentProcessorFactory,
                                       final CustomerOrderService customerOrderService) {
        super(paymentCallBackHandlerFacade, shopService, ipResolver);
        this.paymentProcessorFactory = paymentProcessorFactory;
        this.customerOrderService = customerOrderService;
        this.paymentCallBackHandlerFacade = paymentCallBackHandlerFacade;
    }

    @Override
    public ServletRequest doBefore(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {

        final Logger log = ShopCodeContext.getLog(this);

        final String callbackDump = HttpUtil.dumpRequest((HttpServletRequest) servletRequest);

        if (isCallerIpAllowed(servletRequest)) {

            if (log.isDebugEnabled()) {
                log.debug(callbackDump);
            }

            final Map parameters = servletRequest.getParameterMap();
            final Map<String, String> singleMap = HttpParamsUtils.createSingleValueMap(parameters);

            final boolean start = "1".equals(getParameterIgnoreCase(singleMap, "start"));

            if (start) {

                // This filter is locally mapped and we still have cart in cookies
                final ShoppingCart cart = (ShoppingCart) servletRequest.getAttribute("ShoppingCart");

                final String orderGuid = cart.getGuid();

                final CustomerOrder customerOrder = customerOrderService.findByReference(orderGuid);

                final PaymentProcessor paymentProcessor = getPaymentProcessor(customerOrder);

                final PaymentGatewayPayPalExpressCheckout paymentGatewayExternalForm = (PaymentGatewayPayPalExpressCheckout) paymentProcessor.getPaymentGateway();

                final Payment payment = paymentProcessor.createPaymentsToAuthorize(
                        customerOrder,
                        true,
                        servletRequest.getParameterMap(),
                        "tmp")
                        .get(0);

                // setExpressCheckoutMethod will redirect customer to login to paypal and authorise the payment
                final String redirectUrl = paymentGatewayExternalForm.setExpressCheckoutMethod(payment, String.valueOf(customerOrder.getId()));

                ShopCodeContext.getLog(this).info("Pay pal filter user will be redirected to {}", redirectUrl);

                // Send redirect to paypal for customer to login and authorise payment
                ((HttpServletResponse) servletResponse).sendRedirect(
                        ((HttpServletResponse) servletResponse).encodeRedirectURL(redirectUrl)
                );

            } else {

                // We are attempting to process express checkout callback
                final String orderGuid = getParameterIgnoreCase(singleMap, "orderGuid");
                final String verify = getParameterIgnoreCase(singleMap, "verify");
                final String token = getParameterIgnoreCase(singleMap, "token");

                final CustomerOrder customerOrder = customerOrderService.findByReference(orderGuid);
                if (customerOrder != null && String.valueOf(customerOrder.getId()).equals(verify)) {

                    final String paymentGatewayLabel = getFilterConfig().getInitParameter("paymentGatewayLabel");

                    try {

                        final PaymentProcessor paymentProcessor = getPaymentProcessor(customerOrder);

                        final PaymentGatewayPayPalExpressCheckout paymentGatewayExternalForm = (PaymentGatewayPayPalExpressCheckout) paymentProcessor.getPaymentGateway();

                        final Payment payment = paymentProcessor.createPaymentsToAuthorize(
                                customerOrder,
                                true,
                                servletRequest.getParameterMap(),
                                "tmp")
                                .get(0);

                        final Map<String, String> result = paymentGatewayExternalForm.doExpressCheckoutPayment(payment, token);
                        result.put("orderGuid", orderGuid); // Must be passed in to parameters

                        final PaymentGatewayCallback callback = paymentCallBackHandlerFacade.registerCallback(
                                result, paymentGatewayLabel, ShopCodeContext.getShopCode(), callbackDump);

                        paymentCallBackHandlerFacade.handlePaymentCallback(callback);

                        ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_OK);

                    } catch (OrderException e) {

                        log.error("Transition failed during payment call back for " + getPaymentGatewayLabel() + " payment gateway" , e);
                        log.error(callbackDump);

                        // Send 500, so that PG know that there was an issue and may resend the update
                        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                    }

                } else {

                    log.error("Transition failed during payment call back for " + getPaymentGatewayLabel() + " payment gateway: orderGuid verification failed");
                    log.error(callbackDump);

                    // Send 500, so that PG know that there was an issue and may resend the update
                    ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                }

            }


        } else {

            if (log.isWarnEnabled()) {
                log.warn("Received payment gateway callback from unauthorised IP {}", ipResolver.resolve((HttpServletRequest) servletRequest));
                log.warn(callbackDump);
            }
            // Send forbidden to notify PG that this is a security issue and not error of any kind
            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN);

        }

        return null;  //no forwarding, just return

    }

    private String getParameterIgnoreCase(final Map<String, String> singleMap, final String key) {
        for (final String paramKey : singleMap.keySet()) {
            if (paramKey.equalsIgnoreCase(key)) {
                return singleMap.get(paramKey);
            }
        }
        return null;
    }

    private PaymentProcessor getPaymentProcessor(final CustomerOrder customerOrder) {
        final String paymentGatewayLabel = getPaymentGatewayLabel();

        final Shop pgShop = customerOrder.getShop().getMaster() != null ? customerOrder.getShop().getMaster() : customerOrder.getShop();
        return paymentProcessorFactory.create(paymentGatewayLabel, pgShop.getCode());
    }

    @Override
    public void doAfter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        // NOTHING
    }
}
