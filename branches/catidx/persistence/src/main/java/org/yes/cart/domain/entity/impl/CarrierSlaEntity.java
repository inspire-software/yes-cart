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

package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.Carrier;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TCARRIERSLA"
)
public class CarrierSlaEntity implements org.yes.cart.domain.entity.CarrierSla, java.io.Serializable {


    private String name;
    private String description;
    private String currency;
    private Integer maxDays;
    private String slaType;
    private BigDecimal price;
    private BigDecimal percent;
    private String script;
    private BigDecimal priceNotLess;
    private BigDecimal percentNotLess;
    private BigDecimal costNotLess;
    private Carrier carrier;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CarrierSlaEntity() {
    }




    @Column(name = "NAME", nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION", length = 4000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "CURRENCY", nullable = false, length = 3)
    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "MAX_DAYS")
    public Integer getMaxDays() {
        return this.maxDays;
    }

    public void setMaxDays(Integer maxDays) {
        this.maxDays = maxDays;
    }

    @Column(name = "SLA_TYPE", nullable = false, length = 1)
    public String getSlaType() {
        return this.slaType;
    }

    public void setSlaType(String slaType) {
        this.slaType = slaType;
    }

    @Column(name = "PRICE")
    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "PER_CENT")
    public BigDecimal getPercent() {
        return this.percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    @Column(name = "SCRIPT", length = 4000)
    public String getScript() {
        return this.script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Column(name = "PRICE_NOTLESS")
    public BigDecimal getPriceNotLess() {
        return this.priceNotLess;
    }

    public void setPriceNotLess(BigDecimal priceNotLess) {
        this.priceNotLess = priceNotLess;
    }

    @Column(name = "PERCENT_NOTLESS")
    public BigDecimal getPercentNotLess() {
        return this.percentNotLess;
    }

    public void setPercentNotLess(BigDecimal percentNotLess) {
        this.percentNotLess = percentNotLess;
    }

    @Column(name = "COST_NOTLESS")
    public BigDecimal getCostNotLess() {
        return this.costNotLess;
    }

    public void setCostNotLess(BigDecimal costNotLess) {
        this.costNotLess = costNotLess;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARRIER_ID", nullable = false)
    public Carrier getCarrier() {
        return this.carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP")
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIMESTAMP")
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Column(name = "CREATED_BY", length = 64)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED_BY", length = 64)
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "GUID", unique = true, nullable = false, length = 36)
    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    // The following is extra code specified in the hbm.xml files


    private long carrierslaId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})

    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "CARRIERSLA_ID", nullable = false)
    public long getCarrierslaId() {
        return this.carrierslaId;
    }


    @Transient
    public long getId() {
        return this.carrierslaId;
    }

    public void setCarrierslaId(long carrierslaId) {
        this.carrierslaId = carrierslaId;
    }


    // end of extra code specified in the hbm.xml files

}


