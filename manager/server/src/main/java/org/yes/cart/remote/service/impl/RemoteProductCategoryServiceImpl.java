package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.ProductCategoryDTO;
import org.yes.cart.remote.service.RemoteProductCategoryService;
import org.yes.cart.service.dto.DtoProductCategoryService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductCategoryServiceImpl
        extends AbstractRemoteService<ProductCategoryDTO>
        implements RemoteProductCategoryService {


    /**
     * Construct remote service.
     *
     * @param dtoProductCategoryService dto service to use.
     */
    public RemoteProductCategoryServiceImpl(final DtoProductCategoryService dtoProductCategoryService) {
        super(dtoProductCategoryService);
    }


    /**
     * {@inheritDoc}
     */
    public void removeByCategoryProductIds(final long categoryId, final long productId) {
        ((DtoProductCategoryService) getGenericDTOService()).removeByCategoryProductIds(categoryId, productId);
    }

    /**
     * {@inheritDoc}
     */
    public int getNextRank(final long categoryId) {
        return ((DtoProductCategoryService) getGenericDTOService()).getNextRank(categoryId);

    }

}
