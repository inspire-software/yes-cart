package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.SkuPriceDTO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Default implementation.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 11:17:34 PM
 */
@Dto
public class SkuPriceDTOImpl implements SkuPriceDTO {

    private static final long serialVersionUID = 20100620L;

    @DtoField(value = "skuPriceId")
    private long skuPriceId;

    @DtoField(value = "regularPrice")
    private BigDecimal regularPrice;

    @DtoField(value = "minimalPrice")
    private BigDecimal minimalPrice;

    @DtoField(value = "salePrice")
    private BigDecimal salePrice;

    @DtoField(value = "salefrom")
    private Date salefrom;

    @DtoField(value = "saletill")
    private Date saletill;

    @DtoField(
            value = "sku",
            converter = "skuId2Sku",
            entityBeanKeys = "org.yes.cart.domain.entity.ProductSku"
    )
    private long productSkuId;

    @DtoField(
            value = "shop",
            converter = "shopId2Shop",
            entityBeanKeys = "org.yes.cart.domain.entity.Shop"
    )
    private long shopId;

    @DtoField(value = "quantity")
    private BigDecimal quantity;

    @DtoField(value = "currency")
    private String currency;

    @DtoField(value = "sku.code", readOnly = true)
    private String code;

    @DtoField(value = "sku.name", readOnly = true)
    private String skuName;

    /** {@inheritDoc}*/
    public String getSkuName() {
        return skuName;
    }

    /** {@inheritDoc}*/
    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }

    /** {@inheritDoc}*/
    public String getCode() {
        return code;
    }

    /** {@inheritDoc}*/
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc}*/
    public String getCurrency() {
        return currency;
    }

    /** {@inheritDoc}*/
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /**
     * @deprecated
     * @return
     */
    public BigDecimal getQuantityTier() {
        return quantity;
    }

    /**
     * @deprecated
     * @param quantityTier
     */
    public void setQuantityTier(final BigDecimal quantityTier) {
        this.quantity = quantityTier;
    }


    /** {@inheritDoc}*/
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    /** {@inheritDoc}*/
    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    /** {@inheritDoc}*/
    public long getSkuPriceId() {
        return skuPriceId;
    }

    /** {@inheritDoc}*/
    public void setSkuPriceId(final long skuPriceId) {
        this.skuPriceId = skuPriceId;
    }

    /** {@inheritDoc}*/
    public BigDecimal getRegularPrice() {
        return regularPrice;
    }

    /** {@inheritDoc}*/
    public void setRegularPrice(final BigDecimal regularPrice) {
        this.regularPrice = regularPrice;
    }

    /** {@inheritDoc}*/
    public BigDecimal getMinimalPrice() {
        return minimalPrice;
    }

    /** {@inheritDoc}*/
    public void setMinimalPrice(final BigDecimal minimalPrice) {
        this.minimalPrice = minimalPrice;
    }

    /** {@inheritDoc}*/
    public Date getSalefrom() {
        return salefrom;
    }

    /** {@inheritDoc}*/
    public void setSalefrom(final Date salefrom) {
        this.salefrom = salefrom;
    }

    /** {@inheritDoc}*/
    public Date getSaletill() {
        return saletill;
    }

    /** {@inheritDoc}*/
    public void setSaletill(final Date saletill) {
        this.saletill = saletill;
    }

    /** {@inheritDoc}*/
    public long getProductSkuId() {
        return productSkuId;
    }

    /** {@inheritDoc}*/
    public void setProductSkuId(final long productSkuId) {
        this.productSkuId = productSkuId;
    }

    /** {@inheritDoc}*/
    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc}*/
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /** {@inheritDoc}*/
    public BigDecimal getQuantity() {
        return quantity;
    }

    /** {@inheritDoc}*/
    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SkuPriceDTOImpl that = (SkuPriceDTOImpl) o;

        if (productSkuId != that.productSkuId) return false;
        if (skuPriceId != that.skuPriceId) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (regularPrice != null ? !regularPrice.equals(that.regularPrice) : that.regularPrice != null) return false;
        if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (skuPriceId ^ (skuPriceId >>> 32));
        result = 31 * result + (int) (productSkuId ^ (productSkuId >>> 32));
        result = 31 * result + (int) (shopId ^ (shopId >>> 32));
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (regularPrice != null ? regularPrice.hashCode() : 0);
        result = 31 * result + (salePrice != null ? salePrice.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SkuPriceDTOImpl{" +
                "skuPriceId=" + skuPriceId +
                ", regularPrice=" + regularPrice +
                ", minimalPrice=" + minimalPrice +
                ", salefrom=" + salefrom +
                ", saletill=" + saletill +
                ", productSkuId=" + productSkuId +
                ", shopId=" + shopId +
                ", quantity=" + quantity +
                ", currency='" + currency + '\'' +
                ", quantityTier=" + quantity +
                ", listPrice=" + regularPrice +
                ", salePrice=" + salePrice +
                '}';
    }
}
