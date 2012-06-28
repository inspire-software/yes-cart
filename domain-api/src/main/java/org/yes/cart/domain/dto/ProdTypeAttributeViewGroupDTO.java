package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.entity.ProductType;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/28/12
 * Time: 10:12 PM
 */
public interface ProdTypeAttributeViewGroupDTO extends Identifiable {

     /**
     * Primary key.
     * @return     primary key.
     */
    long getProdTypeAttributeViewGroupId();

    /**
     * Set pk value.
     * @param prodTypeAttributeViewGroupId pk value.
     */
    void setProdTypeAttributeViewGroupId(long prodTypeAttributeViewGroupId);

    /**
     * Comma separated attribute list. For example WEIGHT,LENGHT,HEIGHT
     *
     * @return comm  separated attribute list.
     */
    String getAttrCodeList();

    /**
     * Attribute code list.
     * @param attrCodeList code list.
     */
    void setAttrCodeList(String attrCodeList);

    /**
     * Product type.
     * @return      product type.
     */
    long getProducttypeId();

    /**
     * Set product type.
     * @param producttypeId product type.
     */
    void setProducttypeId(long producttypeId);

    /**
     * Rank .
     * @return rank.
     */
    int getRank();

    /**
     * Rank .
     * @param rank rank.
     */
    void setRank(int rank);

     /**
     * Get name.
     *
     * @return name of view group.
     */
    String getName();

    /**
     * Set name of view group.
     *
     * @param name name.
     */
    void setName(String name);

}
