/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.dao.impl;

import org.hibernate.ScrollableResults;
import org.hibernate.search.util.impl.HibernateHelper;
import org.yes.cart.dao.ResultsIterator;

/**
 * User: denispavlov
 * Date: 07/11/2013
 * Time: 07:55
 */
public class ResultsIteratorImpl<T> implements ResultsIterator<T> {

    private final ScrollableResults scrollableResults;
    private boolean hasNext = false;


    public ResultsIteratorImpl(final ScrollableResults scrollableResults) {
        this.scrollableResults = scrollableResults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        hasNext = scrollableResults.next();
        return hasNext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T next() {
        if (hasNext) {
            return (T) HibernateHelper.unproxy(scrollableResults.get(0));
        }
        throw new ArrayIndexOutOfBoundsException("Check that hasNext() returns true first");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("This is a read only iterator");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        scrollableResults.close();
    }

}
