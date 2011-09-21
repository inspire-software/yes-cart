package org.yes.cart.service.misc;

import org.yes.cart.domain.queryobject.QueryResult;
import org.yes.cart.domain.queryobject.QueryType;

/**
 * Dynamic query service responsible to execute particular query type and return result.
 * Designated for management console and scripting usage.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/31/11
 * Time: 6:13 AM
 * TODO implementation
 */
public interface DynamicQueryService {

    /**
     * Execute givin query as given type.
     * @param query      query.
     * @param queryType  query type
     * @return {@link QueryResult} in case of DML SQL will hold 1 cell with affected rows count.
     */
    QueryResult execute(String query, QueryType queryType);

}
