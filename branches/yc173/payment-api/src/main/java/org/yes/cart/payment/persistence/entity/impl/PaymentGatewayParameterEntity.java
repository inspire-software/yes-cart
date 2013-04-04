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


import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;

import javax.persistence.*;
import java.util.Date;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:33:53
 */
@Entity
@Table(name = "TPAYMENTGATEWAYPARAMETER")
public class PaymentGatewayParameterEntity extends DescriptorImpl implements PaymentGatewayParameter {

    private static final long serialVersionUID = 20100714L;

    private long paymentGatewayParameterId;
    private String value;
    protected String pgLabel;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;


    /**
     * @return pk value
     */
    @Id
    @GeneratedValue
    @Column(name = "PAYMENTGATEWAYPARAMETER_ID", nullable = false)
    public long getPaymentGatewayParameterId() {
        return paymentGatewayParameterId;
    }

    /**
     * @param paymentGatewayParameterId pk value
     */
    public void setPaymentGatewayParameterId(final long paymentGatewayParameterId) {
        this.paymentGatewayParameterId = paymentGatewayParameterId;
    }

    /**
     * {@inheritDoc}
     */
    @Column(name = "P_VALUE", length = 4000)
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Name.
     *
     * @return name
     */
    @Column(name = "P_NAME", length = 64)
    public String getName() {
        return name;
    }

    /**
     * Name
     *
     * @param name name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Description.
     *
     * @return Description
     */
    @Column(name = "P_DESCRIPTION", length = 265)
    public String getDescription() {
        return description;
    }

    /**
     * Description
     *
     * @param description Description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Label.
     *
     * @return label.
     */
    @Column(name = "P_LABEL", length = 64)
    public String getLabel() {
        return label;
    }

    /**
     * Label.
     *
     * @param label label
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * {@inheritDoc}
     */
    @Column(name = "PG_LABEL", length = 64)
    public String getPgLabel() {
        return pgLabel;
    }

    /**
     * {@inheritDoc}
     */
    public void setPgLabel(final String pgLabel) {
        this.pgLabel = pgLabel;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP")
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIMESTAMP")
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(final Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Column(name = "CREATED_BY", length = 64)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED_BY", length = 64)
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "GUID", length = 36)
    public String getGuid() {
        return this.guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

}
