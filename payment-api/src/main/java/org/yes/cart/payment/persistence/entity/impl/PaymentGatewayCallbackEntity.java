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

package org.yes.cart.payment.persistence.entity.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.payment.persistence.entity.PaymentGatewayCallback;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
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

    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;


    /**
     * {@inheritDoc}
     */
    public String getShopCode() {
        return shopCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isProcessed() {
        return processed;
    }


    /**
     * {@inheritDoc}
     */
    public void setProcessed(final boolean processed) {
        this.processed = processed;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return label;
    }

    /**
     * {@inheritDoc}
     */
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
    public Map getParameterMap() {
        if (parameterMap == null) {
            parameterMap = parseParameters(parameterMapInternal);
        }
        return Collections.unmodifiableMap(parameterMap);
    }

    /**
     * {@inheritDoc}
     */
    public void setParameterMap(final Map parameterMap) {
        this.parameterMap = new LinkedHashMap<String, String[]>();
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
        final Map<String, String[]> map = new LinkedHashMap<String, String[]>();
        final String[] params = StringUtils.split(parameters, '\n');
        for (final String param : params) {
            final String[] nameAndValues = StringUtils.split(param, '=');
            map.put(nameAndValues[0], StringUtils.split(nameAndValues[1], '\t'));
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
    public String getRequestDump() {
        return requestDump;
    }

    /**
     * {@inheritDoc}
     */
    public void setRequestDump(final String requestDump) {
        this.requestDump = requestDump;
    }

    /**
     * {@inheritDoc}
     */
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    public void setCreatedTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    public void setUpdatedTimestamp(final Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    public String getCreatedBy() {
        return this.createdBy;
    }

    /**
     * {@inheritDoc}
     */
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * {@inheritDoc}
     */
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    /**
     * {@inheritDoc}
     */
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * {@inheritDoc}
     */
    public String getGuid() {
        return this.guid;
    }

    /**
     * {@inheritDoc}
     */
    public void setGuid(final String guid) {
        this.guid = guid;
    }


    /**
     * {@inheritDoc}
     */
    public long getPaymentGatewayCallbackId() {
        return paymentGatewayCallbackId;
    }

    /**
     * {@inheritDoc}
     */
    public void setPaymentGatewayCallbackId(final long paymentGatewayCallbackId) {
        this.paymentGatewayCallbackId = paymentGatewayCallbackId;
    }

}


