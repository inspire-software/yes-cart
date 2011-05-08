package org.yes.cart.domain.dto;

import java.io.Serializable;
import java.util.Collection;

/**
 * Product Sku light weight product object.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 10:37:01 PM
 */
public interface ProductSkuDTO extends Serializable {


    /**
     * Get pk value.
     *
     * @return pk value.
     */
    long getSkuId();

    /**
     * Set pk value
     *
     * @param skuId pk value
     */
    void setSkuId(final long skuId);

    /**
     * @return sku code
     */
    String getCode();

    /**
     * Set sku code.
     *
     * @param code sku code
     */
    void setCode(final String code);

    /**
     * Get name.
     *
     * @return sku name.
     */
    String getName();

    /**
     * Set sku name.
     *
     * @param name sku name.
     */
    void setName(final String name);

    /**
     * Get description.
     *
     * @return sku description.
     */
    String getDescription();

    /**
     * Set description.
     *
     * @param description sku description.
     */
    void setDescription(final String description);

    /**
     * Get pk value of product
     *
     * @return pk value of product
     */
    long getProductId();

    /**
     * set pk value of product.
     *
     * @param productId product pk value.
     */
    void setProductId(final long productId);

    /**
     * Get rank
     *
     * @return rank of sku
     */
    int getRank();

    /**
     * Set rank.
     *
     * @param rank rank
     */
    void setRank(final int rank);

    /**
     * Get bar code.
     *
     * @return bar code.
     */
    String getBarCode();

    /**
     * Set bar code.
     *
     * @param barCode bar code.
     */
    void setBarCode(final String barCode);

    /**
     * Set seo
     *
     * @return seo.
     */
    SeoDTO getSeoDTO();

    /**
     * Set seo
     *
     * @param seoDTO seo
     */
    void setSeoDTO(final SeoDTO seoDTO);

    /**
     * @return price for this sku
     */
    Collection<SkuPriceDTO> getPrice();

    /**
     * Set sku prices
     *
     * @param price pice collection
     */
    void setPrice(Collection<SkuPriceDTO> price);

    /**
     * Get attribute collection.
     *
     * @return attribute collection.
     */
    Collection<AttrValueProductSkuDTO> getAttribute();

    /**
     * Set attribute collection.
     *
     * @param attribute attr collection to set.
     */
    void setAttribute(Collection<AttrValueProductSkuDTO> attribute);


}
