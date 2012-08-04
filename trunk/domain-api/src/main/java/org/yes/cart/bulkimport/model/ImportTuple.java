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

package org.yes.cart.bulkimport.model;

import org.yes.cart.bulkimport.csv.CsvImportDescriptor;

import java.util.List;

/**
 * Single tuple of data from import source.
 *
 * User: denispavlov
 * Date: 12-07-31
 * Time: 7:59 AM
 */
public interface ImportTuple<S, T> {

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
     * @return column value (or values) depending on data
     */
    Object getColumnValue(ImportColumn column);

    /**
     * @param importDescriptor import descriptor
     * @param column column descriptor
     * @return sub tuple from a column
     */
    List<ImportTuple<S, T>> getSubTuples(CsvImportDescriptor importDescriptor, ImportColumn column);

}
