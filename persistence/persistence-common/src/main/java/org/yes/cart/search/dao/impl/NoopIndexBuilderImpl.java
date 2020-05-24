/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.search.dao.impl;

import org.yes.cart.search.dao.IndexBuilder;

import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 20/03/2018
 * Time: 09:19
 */
public class NoopIndexBuilderImpl<T, PK extends Serializable> implements IndexBuilder<T, PK> {

    @Override
    public String getName() {
        return "noop";
    }

    @Override
    public FTIndexState getFullTextIndexState() {
        return new FTIndexState() {
            @Override
            public boolean isFullTextSearchReindexInProgress() {
                return false;
            }

            @Override
            public boolean isFullTextSearchReindexCompleted() {
                return false;
            }

            @Override
            public long getLastIndexCount() {
                return 0;
            }
        };
    }

    @Override
    public void fullTextSearchReindex(final boolean async, final int batchSize) {
        // noop
    }

    @Override
    public void fullTextSearchReindex(final PK primaryKey) {
        // noop
    }

    @Override
    public void fullTextSearchReindex(final PK primaryKey, final boolean purgeOnly) {
        // noop
    }
}
