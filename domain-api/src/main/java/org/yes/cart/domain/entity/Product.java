package org.yes.cart.domain.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Set;


/**
 * Product.
 * TODO need to think about delivery estimation time when product has back order capabiliy and
 * it in order and no items on stock
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Product extends Auditable, Attributable {

    /**
     * Get product pk.
     *
     * @return product pk.
     */
    long getProductId();

    /**
     * Set product pk.
     *
     * @param productId product pk.
     */
    void setProductId(long productId);

    /**
     * Get the product code.
     *
     * @return product code.
     */
    String getCode();

    /**
     * Product code.
     * Limitation code must not contains underscore
     *
     * @param code
     */
    void setCode(String code);

    /**
     * Get start of product availability.
     * Null - product has not start date, means no limitation.
     *
     * @return start of product availability.
     */
    Date getAvailablefrom();

    /**
     * Set start of product availability.
     *
     * @param availablefrom start of product availability.
     */
    void setAvailablefrom(Date availablefrom);

    /**
     * Get end of product availability.
     * Null - product has not end date, means no limitation.
     *
     * @return end of product availability.
     */
    Date getAvailabletill();

    /**
     * Set end of product availability.
     *
     * @param availabletill end of product availability.
     */
    void setAvailabletill(Date availabletill);

    /**
     * Get product {@link Availability}
     *
     * @return {@link Availability}
     */
    Availability getAvailability();

    /**
     * Set product {@link Availability}
     *
     * @param availability product {@link Availability}
     */
    void setAvailability(Availability availability);


    /**
     * Get the {@link Brand} of product.
     *
     * @return {@link Brand} of product.
     */
    Brand getBrand();

    /**
     * Set {@link Brand} of product.
     *
     * @param brand {@link Brand} of product.
     */
    void setBrand(Brand brand);

    /**
     * Get {@link ProductType}
     *
     * @return product type
     */
    ProductType getProducttype();

    /**
     * Set the {@link ProductType}
     *
     * @param producttype Get {@link ProductType}
     */
    void setProducttype(ProductType producttype);


    /**
     * Product's SKUs. SKU - Stock keeping unit or product variation. Each product has at least one sku.
     *
     * @return collection fo product skus.
     */
    Collection<ProductSku> getSku();

    /**
     * Set collection of skus.
     *
     * @param sku sku collection.
     */
    void setSku(Collection<ProductSku> sku);


    /**
     * Get all products attributes.
     *
     * @return collection of product attributes.
     */
    Set<AttrValueProduct> getAttribute();

    /**
     * Get all products attributes filtered by given attribute code.
     *
     * @param attributeCode code of attribute
     * @return collection of product attributes filtered by attribute name or empty collection if no attribute were found.
     */
    ///////////////////////////////////////////////////////////////Collection<AttrValueProduct> getAttributesByCode(String attributeCode);

    /**
     * Get single attribute.
     *
     * @param attributeCode code of attribute
     * @return single {@link AttrValue} or null if not found.
     */
    ///////////////////////////////////////////////////////////////AttrValueProduct getAttributeByCode(String attributeCode);


    /**
     * Set collection of products attributes.
     *
     * @param attribute collection of products attributes
     */
    void setAttribute(Set<AttrValueProduct> attribute);

    /**
     * Get the assigned categories to product.
     *
     * @return assigned categories
     */
    Set<ProductCategory> getProductCategory();

    /**
     * Set assigned categories.
     *
     * @param productCategory assigned categories.
     */
    void setProductCategory(Set<ProductCategory> productCategory);

    /**
     * Get {@link ProductEnsebleOption} for product if it has enseble flag
     *
     * @return Set of {@link ProductEnsebleOption} for product.
     */
    Set<ProductEnsebleOption> getEnsebleOption();

    /**
     * Set {@link ProductEnsebleOption} for product.
     *
     * @param ensebleOption {@link ProductEnsebleOption} for product
     */
    void setEnsebleOption(Set<ProductEnsebleOption> ensebleOption);

    /**
     * Set the product {@link ProductAssociation}, like up-sell, cross-sell, etc..
     *
     * @return product {@link ProductAssociation}.
     */
    Set<ProductAssociation> getProductAssociations();

    /**
     * Set product {@link ProductAssociation}.
     *
     * @param productAssociations product {@link ProductAssociation}.
     */
    void setProductAssociations(Set<ProductAssociation> productAssociations);

    /**
     * Product seo.
     *
     * @return product {@link Seo} information or null if seo not set.
     */
    Seo getSeo();

    /**
     * Set product seo information.
     *
     * @param seo product seo.
     */
    void setSeo(Seo seo);

    /**
     * Get product name.
     *
     * @return product name.
     */
    String getName();

    /**
     * Set product name.
     *
     * @param name product name.
     */
    void setName(String name);

    /**
     * Get product description.
     *
     * @return product description.
     */
    String getDescription();

    /**
     * Set product description.
     *
     * @param description product description.
     */
    void setDescription(String description);

    /**
     * Get the default sku. For single sku product it will be only one sku.
     * In case of multi sku product default sku has the same sku code as product, otherwise
     * the first will be returned.,
     *
     * @return default sku or null if not found
     */
    ProductSku getDefaultSku();

    /**
     * Get the featured flag for product.
     *
     * @return set featured flag.
     */
    Boolean getFeatured();

    /**
     * Set product featured flag.
     *
     * @param featured featured flag.
     */
    void setFeatured(Boolean featured);


    /**
     * Get sku by given code.
     *
     * @param skuCode given sku code
     * @return product sku if found, otherwise null
     */
    ProductSku getSku(String skuCode);

    /**
     * Is product multisku .
     *
     * @return true if product multisku
     */
    boolean isMultiSkuProduct();


    /**
     * Get the space separated product tags. For example
     * sale specialoffer newarrival etc.
     * This tags should not show to shopper, just for query navigation.
     * @return space separated product tags
     */
    String getTag();

    /**
     * Set space separated product tags.
     * @param tag space separated product tags.
     */
    void setTag(String tag);


    /**
     * Get total quantity of skus on all warehouses.
     * @return total quantity
     */
    BigDecimal getQtyOnWarehouse();

}


