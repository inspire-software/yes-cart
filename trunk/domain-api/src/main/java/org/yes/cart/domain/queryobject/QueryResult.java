package org.yes.cart.domain.queryobject;


import java.util.List;

/**
 *
 * Represent query result that has metadata
 * and data.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/31/11
 * Time: 5:14 AM
 */
public interface QueryResult {

    /**
     * Get query column names to have more convenient work with list of result objects.
     * @return {@link QueryResultMetaData}
     */
    List<String> getColumnNames();

    /**
     * Get actual data.
     * @return  list of objects.
     */
    List getData();



}
