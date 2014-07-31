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

package org.yes.cart.web.page.payment.callback;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.js.ServerSideJs;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CheckoutServiceFacade;

import java.util.Collections;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 5:17 PM
 */
public class LiqPayReturnUrlPage  extends AbstractWebPage {


    @SpringBean(name = StorefrontServiceSpringKeys.CHECKOUT_SERVICE_FACADE)
    private CheckoutServiceFacade checkoutServiceFacade;

    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public LiqPayReturnUrlPage(final PageParameters params) {
        super(params);

        add(new FeedbackPanel("feedback"));
        add(new ServerSideJs("serverSideJs"));
    }

    @Override
    protected void onBeforeRender() {
        executeHttpPostedCommands();

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        if (cart != null && StringUtils.isNotBlank(cart.getGuid())) {
            final CustomerOrder order = checkoutServiceFacade.findByGuid(cart.getGuid());
            if (order != null) {
                if (CustomerOrder.ORDER_STATUS_CANCELLED.equals(order.getOrderStatus())) {
                    error(getLocalizer().getString("order.error.cancelled", this));
                } else {
                    if (checkoutServiceFacade.isOrderPaymentSuccessful(order)) {
                        info(getLocalizer().getString("order.success", this));
                    }
                }
                getShoppingCartCommandFactory().execute(cart,
                        (Map) Collections.singletonMap(
                                ShoppingCartCommand.CMD_CLEAN,
                                ShoppingCartCommand.CMD_CLEAN
                        ));

            } else {
                error(getLocalizer().getString("order.error.not.found", this));
            }
        } else {
            error(getLocalizer().getString("order.error.not.found", this));
        }

        super.onBeforeRender();

        persistCartIfNecessary();
    }
}
