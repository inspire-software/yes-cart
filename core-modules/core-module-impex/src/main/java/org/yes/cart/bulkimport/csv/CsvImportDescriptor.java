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

package org.yes.cart.bulkimport.csv;

import org.yes.cart.bulkcommon.csv.CsvImpExDescriptor;
import org.yes.cart.bulkimport.model.ImportDescriptor;

import java.util.Collection;

/**
 * Csv Import descriptor.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 11:49 AM
 */
public interface CsvImportDescriptor
        extends ImportDescriptor<CsvImportContext>, CsvImpExDescriptor<CsvImportContext, CsvImportColumn> {

    enum ImportMode { MERGE, DELETE, INSERT_ONLY, UPDATE_ONLY }

    /**
     * Get import mode for given descriptor.
     *
     * @return import mode
     */
    ImportMode getMode();

    /**
     * Get import mode for given descriptor.
     *
     * @return import mode
     */
    String getModeName();

    /**
     * Get the import file description.
     *
     * @return {@link CsvImportFile}
     */
    @Override
    CsvImportFile getImportFileDescriptor();

    /**
     * Get select command, which used to look up objects that are to
     * be modified (if they exist).
     * @return        select command
     */
    String getSelectCmd();

    /**
     * Get insert command, which used instead of hibernate object save to
     * speed up bulk import.
     *
     * @return        insert command
     */
    String getInsertCmd();

    /**
     * Get delete command, which used instead of hibernate object delete to
     * speed up bulk import.
     *
     * @return        delete command
     */
    String getDeleteCmd();

    /**
     * Get the collection of export columns.
     *
     * @return collection of export columns
     */
    Collection<CsvImportColumn> getColumns();


    /**
     * @param columnName column name
     * @return get column by name
     */
    CsvImportColumn getColumn(String columnName);

    /**
     * Get the collection of export columns filtered by given field type.
     *
     * @param fieldType Field type constant discriminator.
     * @return collection of export columns
     */
    Collection<CsvImportColumn> getColumns(String fieldType);


}
