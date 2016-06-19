/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.bulkcommon.service.support.impl;

import org.yes.cart.bulkcommon.service.support.LookUpQuery;

import java.util.Arrays;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 9:23 AM
 */
public class NativeSQLQuery implements LookUpQuery {

    private static final Object[] NULL = new Object[0];

    private final String sql;
    private final Object[] params;

    public NativeSQLQuery(final String sql, final Object[] params) {
        this.sql = sql;
        if (params == null || params.length == 1 && params[0] == null) {
            this.params = NULL;
        } else {
            this.params = params;
        }
    }

    /** {@inheritDoc} */
    public String getQueryType() {
        return NAITIVE;
    }

    /** {@inheritDoc} */
    public String getQueryString() {
        return sql;
    }

    /** {@inheritDoc} */
    public Object[] getParameters() {
        return params;
    }

    @Override
    public String toString() {
        return "NativeSQLQuery{" +
                "sql='" + sql + '\'' +
                ", params=" + (params == null ? null : Arrays.asList(params)) +
                '}';
    }
}
