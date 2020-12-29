/*
 * Copyright 2009 Inspire-Software.com
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

import org.yes.cart.payment.persistence.entity.PaymentGatewayCallback;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.web.support.request.IPResolver;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: inspiresoftware
 * Date: 29/12/2020
 * Time: 12:09
 */
public class PaySeraPaymentGatewayCallBackFilter extends BasePaymentGatewayCallBackFilter {

    /**
     * Construct filter.
     *
     * @param paymentCallBackHandlerFacade handler.
     * @param shopService                  shop service
     * @param ipResolver                   IP resolver
     */
    public PaySeraPaymentGatewayCallBackFilter(final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade,
                                               final ShopService shopService,
                                               final IPResolver ipResolver) {
        super(paymentCallBackHandlerFacade, shopService, ipResolver);
    }

    @Override
    protected void postProcessSuccess(final HttpServletResponse servletResponse, final PaymentGatewayCallback callback) throws IOException {

        super.postProcessSuccess(servletResponse, callback);

        /*
            https://developers.paysera.com/en/checkout/integrations/integration-specification

            Script must return text "OK". Only then our system will register, that information about the payment has been received.
         */
        servletResponse.setHeader("Content-Type", "text/plain");
        PrintWriter writer = servletResponse.getWriter();
        writer.write("OK");
        writer.close();

    }
}
