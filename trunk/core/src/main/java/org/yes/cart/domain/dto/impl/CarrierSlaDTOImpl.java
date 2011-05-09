package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.CarrierSlaDTO;

import java.math.BigDecimal;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class CarrierSlaDTOImpl implements CarrierSlaDTO {

    @DtoField(value = "carrierslaId", readOnly = true)
    private long carrierslaId;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "currency")
    private String currency;

    @DtoField(value = "maxDays")
    private Integer maxDays;

    @DtoField(value = "slaType")
    private String slaType;

    @DtoField(value = "price")
    private BigDecimal price;

    @DtoField(value = "percent")
    private BigDecimal percent;

    @DtoField(value = "script")
    private String script;

    @DtoField(value = "priceNotLess")
    private BigDecimal priceNotLess;

    @DtoField(value = "percentNotLess")
    private BigDecimal percentNotLess;

    @DtoField(value = "costNotLess")
    private BigDecimal costNotLess;


    @DtoField(
            value = "carrier",
            converter = "carrierId2Carrier",
            entityBeanKeys = "org.yes.cart.domain.entity.Carrier"
    )
    private long carrierId;


    /** {@inheritDoc} */
    public long getCarrierslaId() {
        return carrierslaId;
    }

    /**
     * {@inheritDoc}
     */
    public void setCarrierslaId(final long carrierslaId) {
        this.carrierslaId = carrierslaId;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getMaxDays() {
        return maxDays;
    }

    /**
     * {@inheritDoc}
     */
    public void setMaxDays(final Integer maxDays) {
        this.maxDays = maxDays;
    }

    /**
     * {@inheritDoc}
     */
    public String getSlaType() {
        return slaType;
    }

    /**
     * {@inheritDoc}
     */
    public void setSlaType(final String slaType) {
        this.slaType = slaType;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * {@inheritDoc}
     */
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getPercent() {
        return percent;
    }

    /**
     * {@inheritDoc}
     */
    public void setPercent(final BigDecimal percent) {
        this.percent = percent;
    }

    /**
     * {@inheritDoc}
     */
    public String getScript() {
        return script;
    }

    /**
     * {@inheritDoc}
     */
    public void setScript(final String script) {
        this.script = script;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getPriceNotLess() {
        return priceNotLess;
    }

    /**
     * {@inheritDoc}
     */
    public void setPriceNotLess(final BigDecimal priceNotLess) {
        this.priceNotLess = priceNotLess;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getPercentNotLess() {
        return percentNotLess;
    }

    /**
     * {@inheritDoc}
     */
    public void setPercentNotLess(final BigDecimal percentNotLess) {
        this.percentNotLess = percentNotLess;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getCostNotLess() {
        return costNotLess;
    }

    /**
     * {@inheritDoc}
     */
    public void setCostNotLess(final BigDecimal costNotLess) {
        this.costNotLess = costNotLess;
    }

    /**
     * {@inheritDoc}
     */
    public long getCarrierId() {
        return carrierId;
    }

    /**
     * {@inheritDoc}
     */
    public void setCarrierId(final long carrierId) {
        this.carrierId = carrierId;
    }
}
