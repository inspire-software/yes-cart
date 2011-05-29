package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;

/**
 * Attribute value DTO.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface AttrValueDTO extends Unique {

    /**
     * Get primary key.
     *
     * @return pk value
     */
    public long getAttrvalueId();

    /**
     * Set pk value.
     *
     * @param attrvalueId pk value.
     */
    public void setAttrvalueId(long attrvalueId);

    /**
     * Get the string representation of attibute value.
     *
     * @return attribute value.
     */
    public String getVal();

    /**
     * Set attribute value.
     *
     * @param val value
     */
    public void setVal(String val);

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

}
