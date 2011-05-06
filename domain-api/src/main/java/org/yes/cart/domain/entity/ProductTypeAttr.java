
package org.yes.cart.domain.entity;

import org.yes.cart.domain.entity.Rankable;
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.domain.misc.navigation.range.RangeNode;

/**
 * Attributes of product type.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductTypeAttr extends Auditable, Rankable {


    /**
     * Get the primary key.
     *
     * @return primary key.
     */
    long getProductTypeAttrId();

    /**
     * Set pk value.
     * @param productTypeAttrId
     */
    void setProductTypeAttrId(long productTypeAttrId);

    /**
     * Get {@link Attribute}.
     * @return {@link Attribute}
     */
    Attribute getAttribute();

    /**
     * Set {@link Attribute}
     * @param attribute {@link Attribute} to use.
     */
    void setAttribute(Attribute attribute);

    /**
     * Get default product type if any.
     * @return default product type.
     */
    ProductType getProducttype();

    /**
     * Set product type {@link ProductType}
     * @param producttype Product Type
     */
    void setProducttype(ProductType producttype);

    /**
     * Rank of navigation record in case of range navigation.
     * @return rank
     */
    int getRank();

     /**
     * Rank of navigation record in case of range navigation.
     * @param rank to set.
     */
    void setRank(int rank);

    /**
     * Is this attibute visible on storefront ?
     * @return true if attribute is vesible.
     */
    boolean isVisible();

    /**
     * Set attribute visible to storefront.
     * @param visible is attribute visible to storefront.
     */
    void setVisible(boolean visible);

    /**
     * Is this attribute will be taken to count product simularity.
     * @return true if this attribute will be taken to count product simularity.
     */
    boolean isSimulariry();

    /**
     * Set flag to count simularity on this product.
     * @param simulariry count simularity on this product.
     */
    void setSimulariry(boolean simulariry);

    /**
     * Use for attribute navigation.
     * @return true if attribute used for attribute navigation.
     */
    boolean isNavigation();

    /**
     * Set to true if attribute will be used for filtered navigation.
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


    /**
     * Get the value ranges list.
     *
     * @return {@link RangeList}
     */
    RangeList<RangeNode> getRangeList();

    /**
     * Set value ranges list.
     *
     * @param rangeList {@link RangeList}
     */
    void setRangeList(RangeList rangeList);

}
