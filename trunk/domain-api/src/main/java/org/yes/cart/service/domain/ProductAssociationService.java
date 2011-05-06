package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.ProductAssociation;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ProductAssociationService  
        extends GenericService<ProductAssociation> {

    /**
     * Get all product associations.
     * @param productId product primary key
     * @return list of product assotiations
     */
    List<ProductAssociation> getProductAssociations(Long productId);

    /**
     * Get all product associations by association type.
     * @param productId product primary key
     * @param accosiationCode accosiation code [up, cross, etc]
     * @return list of product assotiations
     */
    List<ProductAssociation> getProductAssociations(Long productId, String accosiationCode);

}
