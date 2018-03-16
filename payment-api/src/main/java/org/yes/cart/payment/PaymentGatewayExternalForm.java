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

package org.yes.cart.payment;

/**
 * Payment gateway interface with external form processing. I.e. form action of post operation located on the
 * URL, that belong to particular payment gateway.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface PaymentGatewayExternalForm extends PaymentGateway {

    /**
     * Get action url for <form action="postActionUrl">
     *
     * @return url for post action.
     */
    String getPostActionUrl();

    /**
     * Get submit button, that can be different for different external gateways,
     * because of regulation policy about button view.
     *
     * @param locale         to get localized html
     *
     * @return html code for submit button if it supported by gateway, otherwise null, so the default submit button has to be used.
     */
    String getSubmitButton(String locale);

}
