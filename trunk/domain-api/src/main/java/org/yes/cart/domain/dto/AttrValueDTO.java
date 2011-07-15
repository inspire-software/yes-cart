package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * Attribute value DTO.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface AttrValueDTO extends Identifiable {

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
     * @return {@link org.yes.cart.domain.entity.Attribute}
     */
    AttributeDTO getAttributeDTO();

    /**
     * Set attribute.
     *
     * @param attribute attribute.
     */
    void setAttributeDTO(AttributeDTO attribute);

}
