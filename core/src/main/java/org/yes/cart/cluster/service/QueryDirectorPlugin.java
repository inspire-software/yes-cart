package org.yes.cart.cluster.service;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 16:30
 */
public interface QueryDirectorPlugin {

    /**
     * List of supported types.
     *
     * @return list of supported types (usually one)
     */
    List<String> supports();

    /**
     * Execute query and return a result.
     *
     * @param query query to execute.
     *
     * @return list of rows
     */
    List<Object[]> runQuery(String query);

}
