package org.yes.cart.remote.service;

import java.util.List;

/**
 * User: iazarny@yahoo.com Igor Azarny
 * Date: 4 - feb - 12
 * Time: 5:40 PM
 */
public interface RemoteBackdoorService {

    /**
     * Reindex all products.
     * @return quantity product in index.
     */
    int reindexAllProducts();

    /**
     * Reindex single products.
     * @param productPk product pk.
     * @return quantity of objects in index
     */
    int reindexProduct(long productPk);


    /**
     * Reindex given set of products.
     * @param productPks product PKs to reindex
     * @return quantity of objects in index
     */
    int reindexProducts(long[] productPks);


    /**
     * Execute sql and return result.
     * DML operatin also allowed, in this case result has quantity of affected rows.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    List<Object[]> sqlQuery(String query);

    /**
     * Execute hsql and return result.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    List<Object[]> hsqlQuery(String query);

    /**
     * Execute lucene and return result.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    List<Object[]> luceneQuery(String query);


}
