package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import dp.lib.dto.geda.annotations.DtoParent;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.ProductTypeAttrDTO;

/**
 */
@Dto
public class ProductTypeAttrDTOImpl implements ProductTypeAttrDTO {


    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "productTypeAttrId")
    private long productTypeAttrId;




    @DtoField(value = "attribute",
            dtoBeanKey = "org.yes.cart.domain.dto.AttributeDTO",
            entityBeanKeys = "org.yes.cart.domain.entity.Attribute")
    @DtoParent(value = "attributeId", retriever = "attributeDTO2Attribute")
    private AttributeDTO attributeDTO;

    @DtoField(
            value = "producttype",
            converter = "producttypeId2ProductType",
            entityBeanKeys = "org.yes.cart.domain.entity.ProductType"
    )
    private long producttypeId;


    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "visible")
    private boolean visible;

    @DtoField(value = "simulariry")
    private boolean simulariry;

    @DtoField(value = "navigation")
    private boolean navigation;

    @DtoField(value = "navigationType")
    private String navigationType;

    @DtoField(value = "rangeNavigation")
    private String rangeNavigation;


    /** {@inheritDoc} */
    public long getProductTypeAttrId() {
        return productTypeAttrId;
    }

     /**
     * {@inheritDoc}
     */
    public long getId() {
        return productTypeAttrId;
    }

    /** {@inheritDoc} */
    public void setProductTypeAttrId(final long productTypeAttrId) {
        this.productTypeAttrId = productTypeAttrId;
    }

    /** {@inheritDoc} */
    public AttributeDTO getAttributeDTO() {
        return attributeDTO;
    }

    /** {@inheritDoc} */
    public void setAttributeDTO(final AttributeDTO attributeDTO) {
        this.attributeDTO = attributeDTO;
    }

    /** {@inheritDoc} */
    public long getProducttypeId() {
        return producttypeId;
    }

    /** {@inheritDoc} */
    public void setProducttypeId(final long producttypeId) {
        this.producttypeId = producttypeId;
    }

    /** {@inheritDoc} */
    public int getRank() {
        return rank;
    }

    /** {@inheritDoc} */
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /** {@inheritDoc} */
    public boolean isVisible() {
        return visible;
    }

    /** {@inheritDoc} */
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    /** {@inheritDoc} */
    public boolean isSimulariry() {
        return simulariry;
    }

    /** {@inheritDoc} */
    public void setSimulariry(final boolean simulariry) {
        this.simulariry = simulariry;
    }

    /** {@inheritDoc} */
    public boolean isNavigation() {
        return navigation;
    }

    /** {@inheritDoc} */
    public void setNavigation(final boolean navigation) {
        this.navigation = navigation;
    }

    /** {@inheritDoc} */
    public String getNavigationType() {
        return navigationType;
    }

    /** {@inheritDoc} */
    public void setNavigationType(final String navigationType) {
        this.navigationType = navigationType;
    }

    /** {@inheritDoc} */
    public String getRangeNavigation() {
        return rangeNavigation;
    }

    /** {@inheritDoc} */
    public void setRangeNavigation(final String rangeNavigation) {
        this.rangeNavigation = rangeNavigation;
    }
}
