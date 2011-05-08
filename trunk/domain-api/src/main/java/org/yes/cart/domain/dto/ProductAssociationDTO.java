package org.yes.cart.domain.dto;

import java.io.Serializable;

/**
 * Associated products.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductAssociationDTO extends Serializable {

    /**
     * Pk Value.
     *
     * @return pk value.
     */
    long getProductassociationId();

    /**
     * Set pk value.
     *
     * @param productassociationId pk value.
     */
    void setProductassociationId(long productassociationId);

    /**
     * Rank.
     *
     * @return rank.
     */
    int getRank();

    /**
     * Set rank of asscosiation.
     *
     * @param rank rank of asscosiation.
     */
    void setRank(int rank);

    /**
     * Association type.
     *
     * @return association type.
     */
    long getAssociationId();

    /**
     * Set association type.
     *
     * @param associationId association type.
     */
    void setAssociationId(long associationId);


    /**
     * Get the main(source) product id.
     *
     * @return main product id.
     */
    long getProductId();

    /**
     * Set main product id.
     *
     * @param productId main product id.
     */
    void setProductId(long productId);

    /**
     * Get product pk.
     *
     * @return product pk.
     */
    long getAssociatedProductId();

    /**
     * Set product pk.
     *
     * @param associatedProductId product pk.
     */
    void setAssociatedProductId(long associatedProductId);

    /**
     * Get the product code.
     *
     * @return product code.
     */
    String getAssociatedCode();

    /**
     * Product code.
     *
     * @param associatedCode
     */
    void setAssociatedCode(String associatedCode);

    /**
     * Get the product name.
     *
     * @return product name.
     */
    String getAssociatedName();

    /**
     * Product name.
     *
     * @param associatedName
     */
    void setAssociatedName(String associatedName);

    /**
     * Get the product descrition.
     *
     * @return product descrition.
     */
    String getAssociatedDescrition();

    /**
     * Product descrition.
     *
     * @param associatedDescrition
     */
    void setAssociatedDescrition(String associatedDescrition);

}
