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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CheckoutServiceFacade;

import java.util.Collections;

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

    @SpringBean(name = StorefrontServiceSpringKeys.CHECKOUT_SERVICE_FACADE)
    private CheckoutServiceFacade checkoutServiceFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    protected ShoppingCartCommandFactory shoppingCartCommandFactory;

    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public ResultPage(final PageParameters params) {

        super(params);

        final String orderNum = params.get("orderNum").toString();

        final CustomerOrder customerOrder = checkoutServiceFacade.findByGuid(orderNum);

        final boolean isPaymentSuccessful = checkoutServiceFacade.isOrderPaymentSuccessful(customerOrder);

        if (isPaymentSuccessful) {
            cleanCart();
        }

        add(
                new Label(
                        "paymentResult",
                        getLocalizer().getString(isPaymentSuccessful ? "paymentWasOk" : "paymentWasFailed", this)
                )
        );


    }

    @Override
    protected void onRender() {

        processCommands();

        super.onRender();
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
