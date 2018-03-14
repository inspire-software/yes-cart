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

package org.yes.cart.web.page;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.PaymentGatewayCallback;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;
import org.yes.cart.util.ShopCodeContext;
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

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizeNetSimPaymentOkPage.class);

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

    @SpringBean(name = "cartRepository")
    protected CartRepository cartRepository;


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public AuthorizeNetSimPaymentOkPage(final PageParameters params) {

        super(params);

        add(new FeedbackPanel(FEEDBACK));

    }

    @Override
    protected void onBeforeRender() {

        executeHttpPostedCommands();

        final HttpServletRequest httpServletRequest = wicketUtil.getHttpServletRequest();

        final String callbackDump = HttpUtil.dumpRequest(httpServletRequest);

        LOG.debug("Callback:\n{}", callbackDump);

        try {

            final PaymentGatewayCallback callback = paymentCallBackHandlerFacade.registerCallback(
                    httpServletRequest.getParameterMap(), "authorizeNetSimPaymentGatewayLabel",
                    ShopCodeContext.getShopCode(), callbackDump);

            // Authorize.Net does not have callback, so relay page should be used instead
            paymentCallBackHandlerFacade.handlePaymentCallback(callback);

        } catch (OrderException e) {

            LOG.error("Transition failed during payment call back for authorizeNetSimPaymentGatewayLabel payment gateway" , e);
            LOG.error("Callback:\n{}", callbackDump);

        }

        // Order number can be sent as "orderNum" or "orderGuid" or we use current cart to recover
        final String orderNum = HttpUtil.getSingleValue(httpServletRequest.getParameter(ORDER_GUID));

        final boolean doCleanCart;
        final String cartGuid;
        final String continueButton;

        // Try to get info from the order
        final CustomerOrder customerOrder = checkoutServiceFacade.findByReference(orderNum);
        if (customerOrder != null) {
            cartGuid = customerOrder.getCartGuid();
            if (CustomerOrder.ORDER_STATUS_CANCELLED.equals(customerOrder.getOrderStatus())
                    || CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT.equals(customerOrder.getOrderStatus())) {
                doCleanCart = true;
                error(getLocalizer().getString("orderErrorCancelled", this));
                continueButton = "";
            } else {
                // Could be paid or pending callback, so just display success
                doCleanCart = true;
                info(getLocalizer().getString("orderSuccess", this));
                final PaymentGateway pg = checkoutServiceFacade.getOrderPaymentGateway(customerOrder);
                continueButton = pg.getParameterValue("ORDER_RECEIPT_URL");
            }
        } else {
            doCleanCart = false; // no order for cart so don't clean
            cartGuid = null;
            error(getLocalizer().getString("orderErrorNotFound", this));
            continueButton = "";
        }

        if (doCleanCart) {
            cleanCart(cartGuid);
        }

        addOrReplace(new ExternalLink("continueButton", continueButton).setVisible(StringUtils.isNotBlank(continueButton)));

        final StringBuilder linksConverter = new StringBuilder();
        if (StringUtils.isNotBlank(continueButton)) {
            linksConverter
                    .append("<script type=\"text/javascript\">")
                    .append("window.location.href = '").append(continueButton).append("';")
                    .append("</script>");
        }

        addOrReplace(new Label("linksConverter", linksConverter.toString()).setEscapeModelStrings(false));

        super.onBeforeRender();

        persistCartIfNecessary();
    }

    /**
     * Clean shopping cart.
     * Note that this is a replay call so there will be no cookies for cart and we need
     * Cart guid from the order to clean the cart
     */
    private void cleanCart(final String cartGuid) {
        final ShoppingCart cart = cartRepository.getShoppingCart(cartGuid);
        if (cart != null) {
            shoppingCartCommandFactory.execute(
                    ShoppingCartCommand.CMD_CLEAN, cart,
                    Collections.singletonMap(
                            ShoppingCartCommand.CMD_CLEAN,
                            null)
            );
        }
    }

}
