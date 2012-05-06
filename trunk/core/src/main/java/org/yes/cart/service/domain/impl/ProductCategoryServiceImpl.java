package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.domain.entityindexer.ProductIndexer;
import org.yes.cart.service.domain.ProductCategoryService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductCategoryServiceImpl extends BaseGenericServiceImpl<ProductCategory> implements ProductCategoryService {

    private final static int RANK_STEP = 50;


    private final ProductIndexer productIndexer;

    /**
     * Construct product category service.
     * @param productCategoryDao product category dao to use.
     * @param productIndexer indexer
     */
    public ProductCategoryServiceImpl(final GenericDAO<ProductCategory, Long> productCategoryDao, final ProductIndexer productIndexer) {
        super(productCategoryDao);
        this.productIndexer = productIndexer;
    }

    /** {@inheritDoc} */
    public void removeByCategoryProductIds(final long categoryId, final long productId) {
        getGenericDao().executeNativeUpdate(
                "delete from tproductcategory where category_id = :1 and product_id = :2",
                categoryId,
                productId);
    }

    /** {@inheritDoc} */
    public int getNextRank(final long categoryId) {
        Integer maxRank = (Integer)getGenericDao().getScalarResultByNamedQuery("GET.MAX.RANK", categoryId);
        if (maxRank == null) {
            return RANK_STEP;
        } else {            
            return maxRank + RANK_STEP;
        }
    }

    /** {@inheritDoc} */
    public ProductCategory create(ProductCategory instance) {
        final ProductCategory rez = super.create(instance);
        productIndexer.submitIndexTask(instance.getProduct().getProductId());
        return rez;
    }

    /** {@inheritDoc} */
    public ProductCategory update(ProductCategory instance) {
        final ProductCategory rez = super.update(instance);
        productIndexer.submitIndexTask(instance.getProduct().getProductId());
        return rez;
    }

    /** {@inheritDoc} */
    public void delete(ProductCategory instance) {
        super.delete(instance);
        productIndexer.submitIndexTask(instance.getProduct().getProductId());
    }
}
