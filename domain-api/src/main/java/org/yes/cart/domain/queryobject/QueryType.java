package org.yes.cart.domain.queryobject;

/**
 *
 * Dynamic query type.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/31/11
 * Time: 6:08 AM
 */
public interface QueryType {

    /**
     * Execute direct sql query.
     */
    String SQL = "sql";

    /**
     * Execute hibernate query.
     */
    String HQL = "hql";

    /**
     * Execute lucene query and return domain objects.
     */
    String HSEARCH = "hsearch";

    /**
     * Execute lucene query and return lucene result.
     * Lucene result show the index information.
     */
    String LUCENE = "lucene";
}
