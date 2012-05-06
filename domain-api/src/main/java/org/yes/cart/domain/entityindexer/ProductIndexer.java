package org.yes.cart.domain.entityindexer;

/**
 *
 * Reindex product in background.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/5/12
 * Time: 12:00 PM
 */
public interface ProductIndexer {

    /**
     * Add given product's pk  to reindexing.
     * @param productPkValue product primary key value.
     */
    void submitIndexTask(Long productPkValue);

}
