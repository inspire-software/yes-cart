
package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.service.domain.ProductAssociationService;

import java.util.List;

/**
 * Product association services.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductAssociationServiceImpl
    extends BaseGenericServiceImpl<ProductAssociation>
        implements ProductAssociationService {

    private final GenericDAO<ProductAssociation, Long> productAssociationDao;

    /**
     * Construct product association services.
     *
     * @param productAssociationDao dao to use.
     */
    public ProductAssociationServiceImpl(final GenericDAO<ProductAssociation, Long> productAssociationDao) {
        super(productAssociationDao);
        this.productAssociationDao = productAssociationDao;
    }

    /**
     * Get all product associations.
     *
     * @param productId product primary key
     * @return list of product assotiations
     */
    public List<ProductAssociation> getProductAssociations(final Long productId) {
        return productAssociationDao.findByNamedQuery("PRODUCT.ASSOCIATIONS", productId);
    }

    /**
     * Get all product associations by association type.
     *
     * @param productId       product primary key
     * @param accosiationCode accosiation code [up, cross, etc]
     * @return list of product assotiations
     */
    public List<ProductAssociation> getProductAssociations(final Long productId, final String accosiationCode) {
        return productAssociationDao.findByNamedQuery("PRODUCT.ASSOCIATIONS.BY.TYPE", productId, accosiationCode);
    }


}
