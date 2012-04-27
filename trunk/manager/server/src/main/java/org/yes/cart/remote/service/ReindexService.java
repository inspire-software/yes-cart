package org.yes.cart.remote.service;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public interface ReindexService {

    /**
     * Reindex all products
     * @return quantity product in created index.
     */
    int reindexAllProducts();

    /**
     * Reindex product by given sku code.
     *
     * @param pk product primary key
     * @return quantity product in created index.
     */
    int reindexProduct( long pk);

}
