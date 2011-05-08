package org.yes.cart.domain.entity;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 * <p/>
 * Product sku. Sku is product or product variation.
 * By default one product has at leat one sku
 * or several in case of multisku product.
 * Sku is abbreviation from stock keeping unit.
 * Attribute values is distinguish between sku for multisku product.
 */
public interface ProductSku extends Auditable, Attributable, Rankable {

    /**
     * @return sku primary key
     */
    long getSkuId();

    /**
     * Set primary key value.
     *
     * @param skuId primary key value.
     */
    void setSkuId(long skuId);

    /**
     * Get the sku code.
     *
     * @return sku code
     */
    String getCode();

    /**
     * Stock keeping unit code.
     * Limitation code must not contains underscore
     *
     * @param code code
     */
    void setCode(String code);

    /**
     * Get the product.
     *
     * @return {@link Product}
     */
    Product getProduct();

    /**
     * Set {@link Product}.
     *
     * @param product {@link Product}
     */
    void setProduct(Product product);

    /**
     * Get all products attributes.
     *
     * @return collection of product attributes.
     */
    Collection<AttrValueProductSku> getAttribute();

    /**
     * Get all products attributes filtered by given name.
     *
     * @param attributeCode code of attribute
     * @return collection of product attributes filtered by attribute name or empty collection if no attribute were found.
     */
    ///////////////////////// Collection<AttrValueProductSku> getAttributesByCode(String attributeCode);

    /**
     * Get single attribute.
     *
     * @param attributeCode code of attribute
     * @return single {@link AttrValueProductSku} or null if not found.
     */
    ///////////////////////// AttrValueProductSku getAttributeByCode(String attributeCode);


    /**
     * Set collection of products attributes.
     *
     * @param attribute collection of products attributes
     */
    void setAttribute(Collection<AttrValueProductSku> attribute);

    /**
     * Get {@link Seo}.
     *
     * @return seo object.
     */
    Seo getSeo();

    /**
     * Set {@link Seo}.
     *
     * @param seo {@link Seo}
     */
    void setSeo(Seo seo);

    /**
     * Get the sku name.
     *
     * @return sku name.
     */
    String getName();

    /**
     * Set sku name.
     *
     * @param name sku name.
     */
    void setName(String name);

    /**
     * Get sku decription.
     *
     * @return sku description.
     */
    String getDescription();

    /**
     * Set sku decription.
     *
     * @param description sku decription.
     */
    void setDescription(String description);

    /**
     * {@inheritDoc}
     */
    int getRank();

    /**
     * {@inheritDoc}
     */
    void setRank(int rank);

    /**
     * Get collection of sku prive tiers.
     *
     * @return collection of sku prive tiers.
     */
    Collection<SkuPrice> getSkuPrice();

    /**
     * Set collection of sku prive tiers.
     *
     * @param skuPrice collection of sku prive tiers.
     */
    void setSkuPrice(Collection<SkuPrice> skuPrice);

    /**
     * Get the sku bar code.
     *
     * @return Sku bar code
     */
    String getBarCode();

    /**
     * Set sku bar code.
     *
     * @param barCode bar code.
     */
    void setBarCode(String barCode);


}


