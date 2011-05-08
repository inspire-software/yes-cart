package org.yes.cart.domain.entity;

/**
 * Product associations.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductAssociation extends Auditable {

    /**
     * Pk Value.
     *
     * @return pk value.
     */
    public long getProductassociationId();

    /**
     * Set pk value.
     *
     * @param productassociationId pk value.
     */
    public void setProductassociationId(long productassociationId);

    /**
     * Rank.
     *
     * @return rank.
     */
    public int getRank();

    /**
     * Set rank of asscosiation.
     *
     * @param rank rank of asscosiation.
     */
    public void setRank(int rank);

    /**
     * Association type.
     *
     * @return association type.
     */
    public Association getAssociation();

    /**
     * Set association type.
     *
     * @param association association type.
     */
    public void setAssociation(Association association);

    /**
     * Get the main(source) product.
     *
     * @return main product.
     */
    public Product getProduct();

    /**
     * Set main product.
     *
     * @param product main product.
     */
    public void setProduct(Product product);

    /**
     * Get accosiated(destiation) product.
     *
     * @return accosiated product.
     */
    public Product getProductAssociated();

    /**
     * Set accosiated product.
     *
     * @param productAssociated accosiated product.
     */
    public void setProductAssociated(Product productAssociated);

}


