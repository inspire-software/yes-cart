package org.yes.cart.domain.entity;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProdTypeAttributeViewGroup extends Auditable {

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
    ProductType getProducttype();

    /**
     * Set product type.
     * @param producttype product type.
     */
    void setProducttype(ProductType producttype);

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
