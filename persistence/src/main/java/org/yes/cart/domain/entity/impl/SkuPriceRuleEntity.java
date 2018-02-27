package org.yes.cart.domain.entity.impl;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * User: denispavlov
 * Date: 27/02/2018
 * Time: 20:40
 */
public class SkuPriceRuleEntity implements org.yes.cart.domain.entity.SkuPriceRule, java.io.Serializable {

    private long skuPriceRuleId;
    private long version;

    private String shopCode;
    private String currency;
    private String code;
    private String ruleAction;

    private String eligibilityCondition;

    private BigDecimal marginPercent;
    private BigDecimal marginAmount;
    private BigDecimal roundingUnit;
    private String priceTag;
    private String priceRef;

    private String tag;

    private int rank;

    private String name;
    private String description;

    private boolean enabled;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    @Override
    public long getId() {
        return skuPriceRuleId;
    }

    @Override
    public long getSkuPriceRuleId() {
        return skuPriceRuleId;
    }

    @Override
    public void setSkuPriceRuleId(final long skuPriceRuleId) {
        this.skuPriceRuleId = skuPriceRuleId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public String getShopCode() {
        return shopCode;
    }

    @Override
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getRuleAction() {
        return ruleAction;
    }

    @Override
    public void setRuleAction(final String ruleAction) {
        this.ruleAction = ruleAction;
    }

    @Override
    public String getEligibilityCondition() {
        return eligibilityCondition;
    }

    @Override
    public void setEligibilityCondition(final String eligibilityCondition) {
        this.eligibilityCondition = eligibilityCondition;
    }

    @Override
    public BigDecimal getMarginPercent() {
        return marginPercent;
    }

    @Override
    public void setMarginPercent(final BigDecimal marginPercent) {
        this.marginPercent = marginPercent;
    }

    @Override
    public BigDecimal getMarginAmount() {
        return marginAmount;
    }

    @Override
    public void setMarginAmount(final BigDecimal marginAmount) {
        this.marginAmount = marginAmount;
    }

    @Override
    public BigDecimal getRoundingUnit() {
        return roundingUnit;
    }

    @Override
    public void setRoundingUnit(final BigDecimal roundingUnit) {
        this.roundingUnit = roundingUnit;
    }

    @Override
    public String getPriceTag() {
        return priceTag;
    }

    @Override
    public void setPriceTag(final String priceTag) {
        this.priceTag = priceTag;
    }

    @Override
    public String getPriceRef() {
        return priceRef;
    }

    @Override
    public void setPriceRef(final String priceRef) {
        this.priceRef = priceRef;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(final String tag) {
        this.tag = tag;
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }
}
