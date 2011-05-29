package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;

/**
 * Attribute type DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductTypeAttrDTO extends Unique {

    /**
     * Get the primary key.
     *
     * @return primary key.
     */
    long getProductTypeAttrId();

    /**
     * Set pk value.
     *
     * @param productTypeAttrId product type pk value.
     */
    void setProductTypeAttrId(long productTypeAttrId);

    /**
     * Get the attribute.
     *
     * @return {@link org.yes.cart.domain.entity.Attribute}
     */
    public AttributeDTO getAttributeDTO();

    /**
     * Set attribute.
     *
     * @param attribute attribute.
     */
    public void setAttributeDTO(AttributeDTO attribute);


    /**
     * Get default product type if any.
     *
     * @return default product type.
     */
    long getProducttypeId();

    /**
     * Set product type id
     *
     * @param producttypeId Product Type id
     */
    void setProducttypeId(long producttypeId);

    /**
     * Candidate to deprecation.
     *
     * @return rank
     * @deprecated do not use it
     */
    int getRank();


    /**
     * Candidate to deprecation.
     *
     * @param rank to set.
     * @deprecated do not use it
     */
    void setRank(int rank);

    /**
     * Is this attibute visible on storefront ?
     *
     * @return true if attribute is vesible.
     */
    boolean isVisible();

    /**
     * Set attribute visible to storefront.
     *
     * @param visible is attribute visible to storefront.
     */
    void setVisible(boolean visible);

    /**
     * Is this attribute will be taken to count product simularity.
     *
     * @return true if this attribute will be taken to count product simularity.
     */
    boolean isSimulariry();

    /**
     * Set flag to count simularity on this product.
     *
     * @param simulariry count simularity on this product.
     */
    void setSimulariry(boolean simulariry);

    /**
     * Use for attribute navigation.
     *
     * @return true if attribute used for attribute navigation.
     */
    boolean isNavigation();

    /**
     * Set to true if attribute will be used for filtered navigation.
     *
     * @param navigation true if attribute will be used for filtered navigation.
     */
    void setNavigation(boolean navigation);

    /**
     * Get the filtered navigation type: S - single,  R - range.
     * Xml range list must be defined for for range navigation.
     *
     * @return navigation type.
     */
    String getNavigationType();

    /**
     * Navigation type.
     *
     * @param navigationType
     */
    void setNavigationType(String navigationType);

    /**
     * Get the xml description of value ranges.
     *
     * @return xml description of value ranges.
     */
    String getRangeNavigation();

    /**
     * Set the description of value ranges in xml
     *
     * @param rangeNavigation description of value ranges in xml.
     */
    void setRangeNavigation(String rangeNavigation);


}
