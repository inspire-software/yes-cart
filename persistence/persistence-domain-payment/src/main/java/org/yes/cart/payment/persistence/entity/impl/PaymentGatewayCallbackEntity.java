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

package org.yes.cart.payment.persistence.entity.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.payment.persistence.entity.PaymentGatewayCallback;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12/02/2017
 * Time: 14:55
 */
public class PaymentGatewayCallbackEntity implements PaymentGatewayCallback, Serializable {

    private static final long serialVersionUID = 20170212L;

    private long paymentGatewayCallbackId;

    private String shopCode;
    private boolean processed;

    private String label;
    private String parameterMapInternal;
    private Map<String, String[]> parameterMap;
    private String requestDump;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;


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
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProcessed() {
        return processed;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setProcessed(final boolean processed) {
        this.processed = processed;
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
    public void setLabel(final String label) {
        this.label = label;
    }

    public String getParameterMapInternal() {
        return parameterMapInternal;
    }

    public void setParameterMapInternal(final String parameterMapInternal) {
        this.parameterMapInternal = parameterMapInternal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map getParameterMap() {
        if (parameterMap == null) {
            parameterMap = parseParameters(parameterMapInternal);
        }
        return Collections.unmodifiableMap(parameterMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParameterMap(final Map parameterMap) {
        this.parameterMap = new LinkedHashMap<>();
        if (parameterMap != null) {
            for (final Object setEntry : parameterMap.entrySet()) {
                final Map.Entry entry = (Map.Entry) setEntry;
                if (entry.getValue() instanceof String[]) {
                    this.parameterMap.put((String) entry.getKey(), (String[]) entry.getValue());
                } else {
                    this.parameterMap.put((String) entry.getKey(), new String[] { (String) entry.getValue() } );
                }
            }
        }
        this.parameterMapInternal = stringifyParameters(this.parameterMap);
    }

    Map<String, String[]> parseParameters(final String parameters) {
        final Map<String, String[]> map = new LinkedHashMap<>();
        final String[] params = StringUtils.split(parameters, '\n');
        for (final String param : params) {
            final String[] nameAndValues = StringUtils.splitPreserveAllTokens(param, '=');
            map.put(nameAndValues[0],
                    nameAndValues.length > 1 && StringUtils.isNotBlank(nameAndValues[1]) ?
                            StringUtils.split(nameAndValues[1], '\t') : new String[] { "" });
        }
        return map;
    }

    String stringifyParameters(final Map<String, String[]> parameters) {
        final StringBuilder out = new StringBuilder();
        for (final Map.Entry<String, String[]> param : parameters.entrySet()) {
            if (out.length() > 0) {
                out.append('\n');
            }
            out.append(param.getKey()).append('=');
            boolean next = false;
            for (final String value : param.getValue()) {
                if (next) {
                    out.append('\t');
                }
                out.append(value);
                next = true;
            }
        }
        return out.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestDump() {
        return requestDump;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRequestDump(final String requestDump) {
        this.requestDump = requestDump;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGuid() {
        return this.guid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public long getPaymentGatewayCallbackId() {
        return paymentGatewayCallbackId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPaymentGatewayCallbackId(final long paymentGatewayCallbackId) {
        this.paymentGatewayCallbackId = paymentGatewayCallbackId;
    }

}


