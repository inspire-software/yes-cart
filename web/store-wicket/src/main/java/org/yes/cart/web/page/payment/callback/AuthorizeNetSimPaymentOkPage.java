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

package org.yes.cart.web.page.payment.callback;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.util.HttpParamsUtils;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.HeaderMetaInclude;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.js.ServerSideJs;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WicketServiceSpringKeys;
import org.yes.cart.web.support.service.CheckoutServiceFacade;
import org.yes.cart.web.support.util.HttpUtil;
import org.yes.cart.web.util.WicketUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * Authorize.Net SIM method integration  payment call back page.
 * The redirect to this page in successful payment case only.
 * Authorize do not perform call back in case of failed payment
 * <p/>
 * Page responsible to change order status and redirect to home page.
 * Redirection performed javascript and meta tag.  Output of this page
 * aggregated/included into other page on
 * payment gateway side.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/12/11
 * Time: 10:47 AM
 */
public class AuthorizeNetSimPaymentOkPage extends AbstractWebPage {

    private static final long serialVersionUID = 20110323L;

    private static final String ORDER_GUID = "orderGuid";     // correspond to  AuthorizeNetSimPaymentGatewayImpl

    @SpringBean(name = WicketServiceSpringKeys.WICKET_UTIL)
    private WicketUtil wicketUtil;

    @SpringBean(name = StorefrontServiceSpringKeys.CHECKOUT_SERVICE_FACADE)
    private CheckoutServiceFacade checkoutServiceFacade;

    @SpringBean(name = ServiceSpringKeys.SHOP_SERVICE)
    private ShopService shopService;

    @SpringBean(name = "paymentCallBackHandlerFacade")
    private PaymentCallBackHandlerFacade paymentCallBackHandlerFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    protected ShoppingCartCommandFactory shoppingCartCommandFactory;


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public AuthorizeNetSimPaymentOkPage(final PageParameters params) {

        super(params);

        add(new StandardFooter(FOOTER));
        add(new StandardHeader(HEADER));
        add(new FeedbackPanel(FEEDBACK));
        add(new ServerSideJs("serverSideJs"));
        add(new HeaderMetaInclude("headerInclude"));

    }

    @Override
    protected void onBeforeRender() {

        executeHttpPostedCommands();

        final Logger log = ShopCodeContext.getLog(this);

        final HttpServletRequest httpServletRequest = wicketUtil.getHttpServletRequest();

        log.error(HttpUtil.dumpRequest(httpServletRequest));
        if (log.isDebugEnabled()) {
            log.debug(HttpUtil.dumpRequest(httpServletRequest));
        }

        try {
            // Authorize.Net does not have callback, so relay page should be used instead
            paymentCallBackHandlerFacade.handlePaymentCallback(
                    HttpParamsUtils.createSingleValueMap(httpServletRequest.getParameterMap()),
                    "authorizeNetSimPaymentGatewayLabel");

        } catch (OrderException e) {

            log.error("Transition failed during payment call back for authorizeNetSimPaymentGatewayLabel payment gateway" , e);
            log.error(HttpUtil.dumpRequest(httpServletRequest));

        }

        // Order number can be sent as "orderNum" or "orderGuid" or we use current cart to recover
        final String orderNum = HttpUtil.getSingleValue(httpServletRequest.getParameter(ORDER_GUID));

        final boolean doCleanCart;

        // Try to get info from the order
        final CustomerOrder customerOrder = checkoutServiceFacade.findByReference(orderNum);
        if (customerOrder != null) {
            if (CustomerOrder.ORDER_STATUS_CANCELLED.equals(customerOrder.getOrderStatus())
                    || CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT.equals(customerOrder.getOrderStatus())) {
                doCleanCart = true;
                error(getLocalizer().getString("orderErrorCancelled", this));
            } else {
                // Could be paid or pending callback, so just display success
                doCleanCart = true;
                info(getLocalizer().getString("orderSuccess", this));
            }
        } else {
            doCleanCart = false; // no order for cart so don't clean
            error(getLocalizer().getString("orderErrorNotFound", this));
        }

        if (doCleanCart) {
            cleanCart();
        }

        super.onBeforeRender();

        persistCartIfNecessary();
    }

    /**
     * Clean shopping cart end prepare it to reusing.
     */
    private void cleanCart() {
        shoppingCartCommandFactory.execute(
                ShoppingCartCommand.CMD_CLEAN, ApplicationDirector.getShoppingCart(),
                Collections.singletonMap(
                        ShoppingCartCommand.CMD_CLEAN,
                        null)
        );
    }

}
