package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.ProductCategoryDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.ReindexService;
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


    private final ReindexService reindexService;


    /**
     * Construct remote service.
     *
     * @param dtoProductCategoryService dto service to use.
     * @param reindexService product reindex service
     */
    public RemoteProductCategoryServiceImpl(
            final DtoProductCategoryService dtoProductCategoryService,
            final ReindexService reindexService) {
        super(dtoProductCategoryService);
        this.reindexService = reindexService;
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

    /**
     * {@inheritDoc}
     */
    public ProductCategoryDTO create(ProductCategoryDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductCategoryDTO rez = super.create(instance);
        reindexService.reindexProduct(rez.getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public ProductCategoryDTO update(ProductCategoryDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductCategoryDTO rez = super.update(instance);
        reindexService.reindexProduct(rez.getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public void remove(long id) {
        super.remove(id);    //Todo reindex
    }
}
