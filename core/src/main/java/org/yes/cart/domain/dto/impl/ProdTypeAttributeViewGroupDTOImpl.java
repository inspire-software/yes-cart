package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ProdTypeAttributeViewGroupDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/28/12
 * Time: 10:15 PM
 */
@Dto
public class ProdTypeAttributeViewGroupDTOImpl  implements ProdTypeAttributeViewGroupDTO {


    private static final long serialVersionUID = 20120628L;


    @DtoField(value = "prodTypeAttributeViewGroupId", readOnly = true)
    private long prodTypeAttributeViewGroupId;


    @DtoField(
            value = "producttype",
            converter = "producttypeId2ProductType",
            entityBeanKeys = "org.yes.cart.domain.entity.ProductType"
    )
    private long producttypeId;

    @DtoField(value = "attrCodeList")
    private String attrCodeList;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "name")
    private String name;

    /** {@inheritDoc} */
    public long getId() {
        return prodTypeAttributeViewGroupId;
    }

    /** {@inheritDoc} */
    public long getProdTypeAttributeViewGroupId() {
        return prodTypeAttributeViewGroupId;
    }

    /** {@inheritDoc} */
    public void setProdTypeAttributeViewGroupId(final long prodTypeAttributeViewGroupId) {
        this.prodTypeAttributeViewGroupId = prodTypeAttributeViewGroupId;
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
    public String getAttrCodeList() {
        return attrCodeList;
    }

    /** {@inheritDoc} */
    public void setAttrCodeList(final String attrCodeList) {
        this.attrCodeList = attrCodeList;
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
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.name = name;
    }
}
