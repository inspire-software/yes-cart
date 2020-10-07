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

package org.yes.cart.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.ConfigurablePaymentGateway;
import org.yes.cart.payment.service.PaymentGatewayConfigurationVisitor;
import org.yes.cart.payment.service.PaymentGatewayParameterService;

import java.util.Collection;

/**
 * User: inspiresoftware
 * Date: 07/10/2020
 * Time: 11:22
 */
public abstract class AbstractPaySeraPaymentGatewayImpl implements ConfigurablePaymentGateway, PaymentGateway {

    private PaymentGatewayParameterService paymentGatewayParameterService;

    private Collection<PaymentGatewayParameter> allParameters = null;

    private String shopCode;
    private String label;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShopCode() {
        return shopCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(final String locale) {
        String pgName = getParameterValue("name_" + locale);
        if (pgName == null) {
            pgName = getParameterValue("name");
        }
        if (pgName == null) {
            pgName = getLabel();
        }
        return pgName;
    }


    /**
     * {@inheritDoc}
     */

    @Override
    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters() {
        if (allParameters == null) {
            allParameters = paymentGatewayParameterService.findAll(getLabel(), shopCode);
        }
        return allParameters;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void deleteParameter(final String parameterLabel) {
        paymentGatewayParameterService.deleteByLabel(getLabel(), parameterLabel);
        allParameters = null;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void addParameter(final PaymentGatewayParameter paymentGatewayParameter) {
        paymentGatewayParameterService.create(paymentGatewayParameter);
        allParameters = null;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void updateParameter(final PaymentGatewayParameter paymentGatewayParameter) {
        paymentGatewayParameterService.update(paymentGatewayParameter);
        allParameters = null;
    }


    /**
     * Parameter service for given gateway.
     *
     * @param paymentGatewayParameterService service
     */
    public void setPaymentGatewayParameterService(
            final PaymentGatewayParameterService paymentGatewayParameterService) {
        this.paymentGatewayParameterService = paymentGatewayParameterService;
    }


    /**
     * Get the parameter value from given collection.
     *
     * @param valueLabel key to search
     * @return value or null if not found
     */
    @Override
    public String getParameterValue(final String valueLabel) {
        if (valueLabel == null || valueLabel.startsWith("#")) {
            return null; // Need to prevent direct access to Shop specific attributes
        }
        if (shopCode != null && !"DEFAULT".equals(shopCode)) {
            final String shopSpecific = getParameterValueInternal("#" + shopCode + "_" + valueLabel);
            if (shopSpecific != null) {
                return shopSpecific;
            }
        }
        return getParameterValueInternal(valueLabel);
    }

    private String getParameterValueInternal(final String valueLabel) {
        final Collection<PaymentGatewayParameter> values = getPaymentGatewayParameters();
        for (PaymentGatewayParameter keyValue : values) {
            if (keyValue.getLabel().equals(valueLabel)) {
                return keyValue.getValue();
            }
        }
        return null;
    }


    String getHiddenFieldValue(final String fieldName, final String value) {
        if (value == null) {
            return "";
        }
        final String str = value;
        if (StringUtils.isBlank(str)) {
            return "";
        }
        return "<input type='hidden' name='" + fieldName + "' value='" + StringUtils.remove(value, '\'') + "'>\n";
    }

    protected String getHiddenFieldParam(final String fieldName, final String param) {
        final String value = getParameterValue(param);
        return getHiddenFieldValue(fieldName, value);
    }

    protected String getHiddenFieldParam(final String fieldName, final String param, final String fallbackParam) {
        final String value = getParameterValue(param);
        if (StringUtils.isBlank(value)) {
            return getHiddenFieldParam(fieldName, fallbackParam);
        }
        return getHiddenFieldValue(fieldName, value);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(final PaymentGatewayConfigurationVisitor visitor) {
        this.shopCode = visitor.getConfiguration("shopCode");
        this.label = visitor.getConfiguration("label");
    }

}
