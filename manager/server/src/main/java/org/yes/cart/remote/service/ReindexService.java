package org.yes.cart.remote.service;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public interface ReindexService {

    /**
     * Reindex all products
     * @param credentials current credentials
     * @return quantity product in created index.
     */
    int reindexAllProducts(String credentials);

    /**
     * Reindex product by given sku code.
     *
     * @param pk product primary key
     * @param credentials current credentials
     * @return quantity product in created index.
     */
    int reindexProduct(String credentials, long pk);

}
