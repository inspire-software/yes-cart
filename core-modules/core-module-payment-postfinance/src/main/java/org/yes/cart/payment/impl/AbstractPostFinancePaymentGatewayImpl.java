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

package org.yes.cart.payment.impl;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.ConfigurablePaymentGateway;
import org.yes.cart.payment.service.PaymentGatewayConfigurationVisitor;
import org.yes.cart.payment.service.PaymentGatewayParameterService;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 07/10/2015
 * Time: 14:43
 */
public abstract class AbstractPostFinancePaymentGatewayImpl implements ConfigurablePaymentGateway, PaymentGateway {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPostFinancePaymentGatewayImpl.class);

    private PaymentGatewayParameterService paymentGatewayParameterService;

    private Collection<PaymentGatewayParameter> allParameters = null;

    private String shopCode;

    /**
     * {@inheritDoc}
     */
    public String getShopCode() {
        return shopCode;
    }

    /**
     * {@inheritDoc}
     */
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

    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters() {
        if (allParameters == null) {
            allParameters = paymentGatewayParameterService.findAll(getLabel(), shopCode);
        }
        return allParameters;
    }

    /**
     * {@inheritDoc}
     */

    public void deleteParameter(final String parameterLabel) {
        paymentGatewayParameterService.deleteByLabel(getLabel(), parameterLabel);
        allParameters = null;
    }

    /**
     * {@inheritDoc}
     */

    public void addParameter(final PaymentGatewayParameter paymentGatewayParameter) {
        paymentGatewayParameterService.create(paymentGatewayParameter);
        allParameters = null;
    }

    /**
     * {@inheritDoc}
     */

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


    protected String getHiddenField(final String fieldName, final Object value) {
        return "<input type='hidden' name='" + fieldName + "' value='" + value + "'>\n";
    }

    protected String sha1sign(final Map<String, String> params, final String secret) {

        final StringBuilder all = new StringBuilder();
        for (final Map.Entry<String, String> entry : params.entrySet()) {

            if (StringUtils.isNotBlank(entry.getValue())) {
                all.append(entry.getKey().toUpperCase()).append("=").append(entry.getValue()).append(secret);
            }

        }
        try {
            final Charset charset = Charset.forName("UTF-8");
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return new String(Hex.encodeHex(digest.digest(all.toString().getBytes(charset)))).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            LOG.error("SHA-1 not available", e);
            return "SHA-1 not available";
        }

    }

    /**
     * Dump map value into String.
     *
     * @param map given map
     * @return dump map as string
     */
    public static String dump(Map<?, ?> map) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append(" : ");
            stringBuilder.append(entry.getValue());
        }

        return stringBuilder.toString();
    }


    /**
     * {@inheritDoc}
     */
    public void accept(final PaymentGatewayConfigurationVisitor visitor) {
        this.shopCode = visitor.getConfiguration("shopCode");
    }

}
