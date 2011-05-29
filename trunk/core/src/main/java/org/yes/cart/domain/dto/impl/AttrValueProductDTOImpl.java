package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueProductDTO;
import org.yes.cart.domain.dto.AttributeDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class AttrValueProductDTOImpl implements AttrValueProductDTO {

    private static final long serialVersionUID = 20100717L;

    @DtoField(value = "attrvalueId", readOnly = true)
    private long attrvalueId;

    @DtoField(value = "val")
    private String val;

    @DtoField(value = "attribute",
            dtoBeanKey = "org.yes.cart.domain.dto.AttributeDTO", readOnly = true)
    private AttributeDTO attributeDTO;

    @DtoField(value = "product.productId", readOnly = true)
    private long productId;

    /** {@inheritDoc */
    public long getAttrvalueId() {
        return attrvalueId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return attrvalueId;
    }

    /** {@inheritDoc */
    public void setAttrvalueId(final long attrvalueId) {
        this.attrvalueId = attrvalueId;
    }

    /** {@inheritDoc */
    public String getVal() {
        return val;
    }

    /** {@inheritDoc */
    public void setVal(final String val) {
        this.val = val;
    }

    /** {@inheritDoc */
    public AttributeDTO getAttributeDTO() {
        return attributeDTO;
    }

    /** {@inheritDoc */
    public void setAttributeDTO(final AttributeDTO attributeDTO) {
        this.attributeDTO = attributeDTO;
    }

    /** {@inheritDoc */
    public long getProductId() {
        return productId;
    }

    /** {@inheritDoc */
    public void setProductId(final long productId) {
        this.productId = productId;
    }
}
