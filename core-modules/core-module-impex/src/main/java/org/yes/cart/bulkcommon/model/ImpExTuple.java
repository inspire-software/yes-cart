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

package org.yes.cart.bulkcommon.model;

import java.util.List;

/**
 * Single tuple of data from import source.
 *
 * User: denispavlov
 * Date: 12-08-11
 * Time: 12:54 PM
 */
public interface ImpExTuple<S, T, D extends ImpExDescriptor, C extends ImpExColumn> {

    /**
     * @return id to trace back to the import source.
     */
    S getSourceId();

    /**
     * @return data to be imported
     */
    T getData();

    /**
     * @param column column descriptor
     * @param adapter value adapter
     * @return column value (or values) depending on data
     */
    Object getColumnValue(C column, ValueAdapter adapter);

    /**
     * @param importDescriptor import descriptor
     * @param column column descriptor
     * @param adapter value adapter
     * @return sub tuple from a column
     */
    <I extends ImpExTuple<S, T, D, C>> List<I> getSubTuples(D importDescriptor, C column, ValueAdapter adapter);

}
