package org.yes.cart.remote.service.impl;

import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.service.domain.ProductService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class ReindexServiceImpl implements ReindexService {

    private final ProductService productService;

    /**
     * Construct reindex service.
     *
     * @param productService product service to use.
     */
    public ReindexServiceImpl(final ProductService productService) {
        this.productService = productService;
    }

    /**
     * Reindex all products
     *
     * @return quantity product in created index.
     */
    public int reindexAllProducts() {
        return productService.reindexProducts();
    }

    /**
     * Reindex product by given sku code.
     *
     * @param pk product primary key
     * @return quantity product in created index.
     */
    public int reindexProduct(long pk) {
        return productService.reindexProduct(pk);
    }

}
