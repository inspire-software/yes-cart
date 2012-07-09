package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ProductAssociationDTO;

/**
 ** User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ProductAssociationDTOImpl implements ProductAssociationDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "productassociationId")
    private long productassociationId;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "association",
            /*dtoBeanKeys = "org.yes.cart.domain.dto.AssociationDTO",*/
            entityBeanKeys = "org.yes.cart.domain.entity.Association",
            converter = "associationDTO2Association"
    )
    private long associationId;


    @DtoField(
            value = "product",
            converter = "productId2Product",
            entityBeanKeys = "org.yes.cart.domain.entity.Product"
    )
    private long productId;


    @DtoField(
            value = "productAssociated",
            converter = "productId2Product",
            entityBeanKeys = "org.yes.cart.domain.entity.Product"
    )
    private long associatedProductId;

    @DtoField(value = "productAssociated.code", readOnly = true)
    private String associatedCode;

    @DtoField(value = "productAssociated.name", readOnly = true)
    private String associatedName;

    @DtoField(value = "productAssociated.description", readOnly = true)
    private String associatedDescrition;

    /** {@inheritDoc} */
    public long getProductassociationId() {
        return productassociationId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return productassociationId;
    }

    /** {@inheritDoc} */
    public void setProductassociationId(final long productassociationId) {
        this.productassociationId = productassociationId;
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
    public long getAssociationId() {
        return associationId;
    }

    /** {@inheritDoc} */
    public void setAssociationId(final long associationId) {
        this.associationId = associationId;
    }

    /** {@inheritDoc} */
    public long getProductId() {
        return productId;
    }

    /** {@inheritDoc} */
    public void setProductId(final long productId) {
        this.productId = productId;
    }

    /** {@inheritDoc} */
    public long getAssociatedProductId() {
        return associatedProductId;
    }

    /** {@inheritDoc} */
    public void setAssociatedProductId(final long associatedProductId) {
        this.associatedProductId = associatedProductId;
    }

    /** {@inheritDoc} */
    public String getAssociatedCode() {
        return associatedCode;
    }

    /** {@inheritDoc} */
    public void setAssociatedCode(final String associatedCode) {
        this.associatedCode = associatedCode;
    }

    /** {@inheritDoc} */
    public String getAssociatedName() {
        return associatedName;
    }

    /** {@inheritDoc} */
    public void setAssociatedName(final String associatedName) {
        this.associatedName = associatedName;
    }

    /** {@inheritDoc} */
    public String getAssociatedDescrition() {
        return associatedDescrition;
    }

    /** {@inheritDoc} */
    public void setAssociatedDescrition(final String associatedDescrition) {
        this.associatedDescrition = associatedDescrition;
    }
}
