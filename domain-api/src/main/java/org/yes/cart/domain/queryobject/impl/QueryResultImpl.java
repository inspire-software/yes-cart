package org.yes.cart.domain.queryobject.impl;

import org.yes.cart.domain.queryobject.QueryResult;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/31/11
 * Time: 6:00 AM
 */
public class QueryResultImpl implements QueryResult {

    private final List<String> columnNames;

    private final List data;

    /**
     * Construct query result.
     */
    public QueryResultImpl() {

        columnNames = new ArrayList<String>();

        data = new ArrayList();

    }

    /** {@inheritDoc} */
    public List<String> getColumnNames() {
        return columnNames;
    }

    /** {@inheritDoc} */
    public List getData() {
        return data;
    }
}
