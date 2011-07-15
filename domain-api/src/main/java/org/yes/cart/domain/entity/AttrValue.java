package org.yes.cart.domain.entity;


/**
 * Attr value object.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */

public interface AttrValue extends Auditable {

    /**
     * Get primary key.
     *
     * @return pk value
     */
    long getAttrvalueId();

    /**
     * Set pk value.
     *
     * @param attrvalueId pk value.
     */
    void setAttrvalueId(long attrvalueId);

    /**
     * Get the string representation of attibute value.
     *
     * @return attribute value.
     */
    String getVal();

    /**
     * Set attribute value.
     *
     * @param val value
     */
    void setVal(String val);

    /**
     * Get the attribute.
     *
     * @return {@link Attribute}
     */
    Attribute getAttribute();

    /**
     * Set attribute.
     *
     * @param attribute attribute.
     */
    void setAttribute(Attribute attribute);


}


