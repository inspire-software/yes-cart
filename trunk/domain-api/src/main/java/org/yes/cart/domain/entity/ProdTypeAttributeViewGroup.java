package org.yes.cart.domain.entity;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProdTypeAttributeViewGroup extends Auditable {

    long getProdTypeAttributeViewGroupId();

    void setProdTypeAttributeViewGroupId(long prodTypeAttributeViewGroupId);

    /**
     * Comma separated attribute list. For example WEIGHT,LENGHT,HEIGHT
     *
     * @return comm  separated attribute list.
     */
    String getAttrCodeList();

    void setAttrCodeList(String attrCodeList);

    ProductType getProducttype();

    void setProducttype(ProductType producttype);

    int getRank();

    void setRank(int rank);

    AttributeViewGroup getAttributeViewGroup();

    void setAttributeViewGroup(AttributeViewGroup attributeViewGroup);
}
