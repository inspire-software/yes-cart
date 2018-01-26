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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.HeaderMetaInclude;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.js.ServerSideJs;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CheckoutServiceFacade;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.support.util.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Just show the result of payment operation for
 * payment gateways with call back
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/12/11
 * Time: 11:50 AM
 */
public class ResultPage extends AbstractWebPage {

    private static final Logger LOG = LoggerFactory.getLogger(ResultPage.class);

    @SpringBean(name = StorefrontServiceSpringKeys.CHECKOUT_SERVICE_FACADE)
    private CheckoutServiceFacade checkoutServiceFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    protected ShoppingCartCommandFactory shoppingCartCommandFactory;

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    protected ContentServiceFacade contentServiceFacade;

    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public ResultPage(final PageParameters params) {

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

        final PageParameters params = getPageParameters();

        if (LOG.isDebugEnabled()) {
            LOG.debug(HttpUtil.dumpRequest((HttpServletRequest) getRequest().getContainerRequest()));
        }

        // Status gives preliminary result from return URL, which can be sent as "status" or "hint"
        final String status = params.get("status").toString(params.get("hint").toString());
        // Order number can be sent as "orderNum" or "orderGuid" or we use current cart to recover
        final String orderNum = params.get("orderNum").toString(params.get("orderGuid").toString(getCurrentCart().getGuid()));

        final boolean doCleanCart;

        final CustomerOrder customerOrder = checkoutServiceFacade.findByReference(orderNum);
        final Map<String, Object> contentParams = new HashMap<>();
        contentParams.put("order", customerOrder);
        contentParams.putAll(getWicketUtil().pageParametesAsMap(params));

        if (StringUtils.isNotBlank(status)) {
            // Trust the return page as chances are the actual payment callback has not yet happened
            if ("ok".equals(status)) {
                doCleanCart = true;
                //info(getLocalizer().getString("orderSuccess", this));
            } else if ("cancel".equals(status)) {
                doCleanCart = true;
                //error(getLocalizer().getString("orderErrorCancelled", this));
            } else {
                doCleanCart = false; // no payment so leave the cart to re-try
                //error(getLocalizer().getString("paymentWasFailed", this));
            }
        } else {
            // Try to get info from the order
            if (customerOrder != null) {
                if (CustomerOrder.ORDER_STATUS_CANCELLED.equals(customerOrder.getOrderStatus())
                        || CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT.equals(customerOrder.getOrderStatus())) {
                    doCleanCart = true;
                    //error(getLocalizer().getString("orderErrorCancelled", this));
                } else {
                    // Could be paid or pending callback, so just display success
                    doCleanCart = true;
                    //info(getLocalizer().getString("orderSuccess", this));
                }
            } else {
                doCleanCart = false; // no order for cart so don't clean
                //error(getLocalizer().getString("orderErrorNotFound", this));
            }
        }

        if (doCleanCart) {
            cleanCart();
        }
        add(new Label("resultMessage", contentServiceFacade.getDynamicContentBody("resultpage_message",
                getCurrentShopId(), getLocale().getLanguage(), contentParams)).setEscapeModelStrings(false));

        super.onBeforeRender();

        persistCartIfNecessary();
    }

    /**
     * Clean shopping cart end prepare it to reusing.
     */
    private void cleanCart() {
        shoppingCartCommandFactory.execute(
                ShoppingCartCommand.CMD_CLEAN, getCurrentCart(),
                Collections.singletonMap(
                        ShoppingCartCommand.CMD_CLEAN,
                        null)
        );
    }

}
