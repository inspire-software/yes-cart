package org.yes.cart.cluster.service;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 16:30
 */
public interface QueryDirectorPlugin {

    /**
     * Check if this plugin supports query of given type.
     *
     * @param type type
     *
     * @return true if supports, false otherwise
     */
    boolean supports(String type);

    /**
     * Execute query and return a result.
     *
     * @param query query to execute.
     *
     * @return list of rows
     */
    List<Object[]> runQuery(String query);

}
