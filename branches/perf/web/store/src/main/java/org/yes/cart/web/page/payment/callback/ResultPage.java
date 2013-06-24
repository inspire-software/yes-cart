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
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.CleanCartCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;

import java.util.Collections;
import java.util.List;

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

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerOrderService customerOrderService;

     @SpringBean(name = ServiceSpringKeys.ORDER_PAYMENT_SERICE)
    private CustomerOrderPaymentService customerOrderPaymentService;

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

        final CustomerOrder customerOrder = customerOrderService.findByGuid(orderNum);

        final boolean isPaymentWasOk = isOk(customerOrder);

        if (isPaymentWasOk) {
            cleanCart();
        }

        add(
                new Label(
                        "paymentResult",
                        getLocalizer().getString(isPaymentWasOk ? "paymentWasOk" : "paymentWasFailed", this)
                )
        );


    }

    @Override
    protected void onRender() {

        processCommands();

        super.onRender();
    }

    /**
     * Clean shopiing cart end prepare it to reusing.
     */
    private void cleanCart() {
        shoppingCartCommandFactory.create(
                Collections.singletonMap(
                        CleanCartCommandImpl.CMD_KEY,
                        null)
        ).execute(ApplicationDirector.getShoppingCart());
    }

    /**
     * Get payment status from order.
     * We expected one payment only.
     * @param customerOrder  given customer order.
     * @return  true in case of successful page
     */
    private boolean isOk(final CustomerOrder customerOrder) {
        if (customerOrder == null) {
            return false;
        }
        final List<CustomerOrderPayment> payments = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);
        return (payments != null && !payments.isEmpty() && Payment.PAYMENT_STATUS_OK.equals(payments.get(0).getPaymentProcessorResult()));
    }


}
