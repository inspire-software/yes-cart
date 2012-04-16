package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.ProductSkuService;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductSkuServiceImpl extends BaseGenericServiceImpl<ProductSku> implements ProductSkuService {

    private final GenericDAO<Product, Long> productDao;

    public ProductSkuServiceImpl(final GenericDAO<ProductSku, Long> productSkuDao,
                                 final GenericDAO<Product, Long> productDao) {
        super(productSkuDao);
        this.productDao = productDao;
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
        productDao.fullTextSearchReindex(instance.getProduct().getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku update(ProductSku instance) {
        final ProductSku rez = super.update(instance);
        productDao.fullTextSearchReindex(instance.getProduct().getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(ProductSku instance) {
        super.delete(instance);
        productDao.fullTextSearchPurge(instance.getProduct().getProductId());
    }
}
