package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entityindexer.ProductIndexer;
import org.yes.cart.service.domain.ProductSkuService;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductSkuServiceImpl extends BaseGenericServiceImpl<ProductSku> implements ProductSkuService {


    private final ProductIndexer productIndexer;

    private final GenericDAO<Product, Long> productDao;


    /**
     * Construct  service.
     * @param productSkuDao sku dao
     * @param productDao    product dao
     * @param productIndexer  product indexer.
     */
    public ProductSkuServiceImpl(final GenericDAO<ProductSku, Long> productSkuDao,
                                 final GenericDAO<Product, Long> productDao,
                                 final ProductIndexer productIndexer) {
        super(productSkuDao);
        this.productDao = productDao;
        this.productIndexer = productIndexer ;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ProductSku> getAllProductSkus(final long productId) {
        final Product product = productDao.findById(productId);
        return product.getSku();
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku getProductSkuBySkuCode(final String skuCode) {
        return getGenericDao().findSingleByCriteria(
                Restrictions.eq("code", skuCode)
        );
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku create(ProductSku instance) {
        final ProductSku rez = super.create(instance);
        productIndexer.submitIndexTask(instance.getProduct().getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku update(ProductSku instance) {
        final ProductSku rez = super.update(instance);
        productIndexer.submitIndexTask(instance.getProduct().getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(ProductSku instance) {
        super.delete(instance);
        productIndexer.submitIndexTask(instance.getProduct().getProductId());
    }
}
