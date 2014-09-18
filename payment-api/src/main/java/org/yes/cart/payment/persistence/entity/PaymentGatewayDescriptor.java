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

package org.yes.cart.payment.persistence.entity;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface PaymentGatewayDescriptor extends Descriptor {


    /**
     * Get payment gateway priority.
     *
     * @return priority.
     */
    int getPriority();


    /**
     * Get the payment gateway url. Url can be a web service url,
     * spring name bean, jndi name. For more information see service locator from core.
     *
     * @return url of payment gateway.
     */
    String getUrl();


    /**
     * Get service login name.
     *
     * @return login name.
     */
    String getLogin();

    /**
     * Set service login name.
     *
     * @param login login name.
     */
    void setLogin(String login);

    /**
     * Get service password.
     *
     * @return password.
     */
    String getPassword();

    /**
     * Set service password.
     *
     * @param password password.
     */
    void setPassword(String password);


}
