package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Lightweight price object.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 11:11:44 PM
 */
public interface SkuPriceDTO extends Identifiable {


    /**
     * @return tier level for this quantity
     * @depricated
     */
    BigDecimal getQuantityTier();


    /**
     * Get the product sku id .
     *
     * @return product sku id
     */
    long getProductSkuId();

    /**
     * Set product sku.
     *
     * @param productSkuId product sku id .
     */
    void setProductSkuId(long productSkuId);

    /**
     * Get shop id.
     *
     * @return shop id
     */
    long getShopId();

    /**
     * Set shop id.
     *
     * @param shopId id
     */
    void setShopId(long shopId);

    /**
     * Get currency code.
     *
     * @return currency code
     */
    String getCurrency();

    /**
     * set currecny code.
     *
     * @param currency curr code
     */
    void setCurrency(String currency);

    /**
     * Get price quantity.
     *
     * @return quantity
     */
    BigDecimal getQuantity();

    /**
     * Set quantity.
     *
     * @param quantity quantity to set
     */
    void setQuantity(BigDecimal quantity);

    /**
     * Get regular/list price.
     *
     * @return regular price.
     */
    BigDecimal getRegularPrice();

    /**
     * Set regular price.
     *
     * @param regularPrice regula price.
     */
    void setRegularPrice(BigDecimal regularPrice);

    /**
     * Get sale price.
     *
     * @return sale price.
     */
    BigDecimal getSalePrice();

    /**
     * Set sale price.
     *
     * @param salePrice sale price.
     */
    void setSalePrice(BigDecimal salePrice);

    /**
     * Get minimal price to use in discount per day or name your price strategy.
     *
     * @return minimal price
     */
    BigDecimal getMinimalPrice();

    /**
     * Set minimal price
     *
     * @param minimalPrice
     */
    void setMinimalPrice(BigDecimal minimalPrice);

    /**
     * Set sale from date.
     *
     * @return sale from date.
     */
    Date getSalefrom();

    /**
     * Get sale from date.
     *
     * @param salefrom sale from date.
     */
    void setSalefrom(Date salefrom);

    /**
     * Get sale till date.
     *
     * @return sale till date.
     */
    Date getSaletill();

    /**
     * Set sale till date
     *
     * @param saletill sale till date
     */
    void setSaletill(Date saletill);

    /**
     * Primary key.
     *
     * @return pk value
     */
    long getSkuPriceId();

    /**
     * Set pk value
     *
     * @param skuPriceId pk value.
     */
    void setSkuPriceId(long skuPriceId);

    /**
     * Get sku name.
     *
     * @return sku name.
     */
    String getSkuName();

    /**
     * Set sku name.
     *
     * @param skuName sku name.
     */
    void setSkuName(String skuName);

    /**
     * Get sku code.
     *
     * @return sku code.
     */
    String getCode();


    /**
     * Setsku code.
     *
     * @param code sku code.
     */
    void setCode(String code);


}
