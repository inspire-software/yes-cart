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

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.shoppingcart.ShoppingCart;
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
public class PayPalExpressCheckoutFilter extends AbstractFilter implements Filter {

    private final PaymentProcessorFactory paymentProcessorFactory;

    private final CustomerOrderService customerOrderService;

    private final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade;

    /**
     * Construct filter.
     *
     * @param applicationDirector   app director.
     * @param paymentProcessorFactory payment processor.
     * @param customerOrderService  {@link CustomerOrderService}     to use
     * @param paymentCallBackHandlerFacade handler.
     */
    public PayPalExpressCheckoutFilter(final ApplicationDirector applicationDirector,
                                       final PaymentProcessorFactory paymentProcessorFactory,
                                       final CustomerOrderService customerOrderService,
                                       final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade) {
        super(applicationDirector);
        this.paymentProcessorFactory = paymentProcessorFactory;
        this.customerOrderService = customerOrderService;
        this.paymentCallBackHandlerFacade = paymentCallBackHandlerFacade;
    }

    @Override
    public ServletRequest doBefore(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        final String orderGuid = cart.getGuid();

        final CustomerOrder customerOrder =  customerOrderService.findByGuid(orderGuid);


        final String paymentGatewayLabel = getFilterConfig().getInitParameter("paymentGatewayLabel");

        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(paymentGatewayLabel, customerOrder.getShop().getCode());

        final PaymentGatewayExternalForm paymentGatewayExternalForm = (PaymentGatewayExternalForm) paymentProcessor.getPaymentGateway();

        final Payment payment = paymentProcessor.createPaymentsToAuthorize(
                customerOrder,
                true,
                servletRequest.getParameterMap(),
                "tmp")
                .get(0);

        final Map<String, String> nvpCallResult = paymentGatewayExternalForm.setExpressCheckoutMethod(
                payment.getPaymentAmount(), payment.getOrderCurrency());

        final String redirectUrl;
        
        if (paymentGatewayExternalForm.isSuccess(nvpCallResult)) {
            /*not encoded answer will be like this
            TOKEN=EC%2d8DX631540T256421Y&TIMESTAMP=2011%2d12%2d21T20%3a12%3a37Z&CORRELATIONID=2d2aa98bcd550&ACK=Success&VERSION=2%2e3&BUILD=2271164
             Redirect url  to paypal for perform login and payment */
            redirectUrl = paymentGatewayExternalForm.getParameterValue("PP_EC_PAYPAL_URL")
                    + "?orderGuid="  + orderGuid
                    + "&token="      + nvpCallResult.get("TOKEN")
                    + "&cmd=_express-checkout";

        } else {
            redirectUrl = "paymentresult" //mounted page
                    + "?orderNum="   + orderGuid
                    + "&errMsg="     + nvpCallResult.get("L_ERRORCODE0")
                    + '|'   + nvpCallResult.get("L_SHORTMESSAGE0")
                    + '|'   + nvpCallResult.get("L_LONGMESSAGE0")
                    + '|'   + nvpCallResult.get("L_SEVERITYCODE0") ;

        }

        try {

            paymentCallBackHandlerFacade.handlePaymentCallback(nvpCallResult, paymentGatewayLabel);

        } catch (OrderException e) {

            ShopCodeContext.getLog(this).error("Transition failed during payment call back for " + paymentGatewayLabel
                    + " payment gateway. Dump request " + HttpUtil.dumpRequest((HttpServletRequest) servletRequest), e);

        }

        ShopCodeContext.getLog(this).info("Pay pal filter user will be redirected to {}", redirectUrl);

        ((HttpServletResponse) servletResponse).sendRedirect(
                ((HttpServletResponse) servletResponse).encodeRedirectURL(redirectUrl)
        );

        return null;
    }

    @Override
    public void doAfter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        // NOTHING
    }
}
