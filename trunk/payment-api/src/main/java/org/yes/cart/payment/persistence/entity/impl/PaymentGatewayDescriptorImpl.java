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

package org.yes.cart.payment.persistence.entity.impl;

import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;

/**
 * Payment gateway descriptor.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:33:53
 */
public class PaymentGatewayDescriptorImpl extends DescriptorImpl implements PaymentGatewayDescriptor {

    private static final long serialVersionUID = 20100714L;

    private String url;
    private String login;
    private String password;


    /**
     * Construct payment gateway descriptor.
     *
     * @param description description
     * @param label       label
     * @param url         url
     */
    public PaymentGatewayDescriptorImpl(final String description,
                                        final String label,
                                        final String url) {
        super(description, label);
        this.url = url;
    }

    /**
     * Default constructor.
     */
    public PaymentGatewayDescriptorImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set url.
     *
     * @param url url
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * {@inheritDoc}
     */
    public String getLogin() {
        return login;
    }

    /**
     * {@inheritDoc}
     */
    public void setLogin(final String login) {
        this.login = login;
    }

    /**
     * {@inheritDoc}
     */
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     */
    public void setPassword(final String password) {
        this.password = password;
    }
}
