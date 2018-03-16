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


import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;

import java.time.Instant;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:33:53
 */
public class PaymentGatewayParameterEntity extends DescriptorImpl implements PaymentGatewayParameter {

    private static final long serialVersionUID = 20100714L;

    private long paymentGatewayParameterId;
    private String name;
    private String value;
    private String businesstype;
    private boolean secure;
    protected String pgLabel;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;


    /**
     * @return pk value
     */
    @Override
    public long getPaymentGatewayParameterId() {
        return paymentGatewayParameterId;
    }

    /**
     * @param paymentGatewayParameterId pk value
     */
    @Override
    public void setPaymentGatewayParameterId(final long paymentGatewayParameterId) {
        this.paymentGatewayParameterId = paymentGatewayParameterId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBusinesstype() {
        return businesstype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBusinesstype(final String businesstype) {
        this.businesstype = businesstype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSecure() {
        return secure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    /**
     * Name.
     *
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Name
     *
     * @param name name
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Description.
     *
     * @return Description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Description
     *
     * @param description Description
     */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Label.
     *
     * @return label.
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * Label.
     *
     * @param label label
     */
    @Override
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPgLabel() {
        return pgLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPgLabel(final String pgLabel) {
        this.pgLabel = pgLabel;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

}
