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

package org.yes.cart.bulkexport.csv;

import org.yes.cart.bulkcommon.csv.CsvImpExDescriptor;
import org.yes.cart.bulkexport.model.ExportDescriptor;

import java.util.Collection;

/**
 * Csv export descriptor.
 * <p/>
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 08:00
 */
public interface CsvExportDescriptor extends
        ExportDescriptor<CsvExportContext>, CsvImpExDescriptor<CsvExportContext, CsvExportColumn> {

    /**
     * Get the collection of export columns.
     *
     * @return collection of export columns
     */
    Collection<CsvExportColumn> getColumns();


    /**
     * @param columnName column name
     * @return get column by name
     */
    CsvExportColumn getColumn(String columnName);

    /**
     * Get the collection of export columns filtered by given field type.
     *
     * @param fieldType Field type constant discriminator.
     * @return collection of export columns
     */
    Collection<CsvExportColumn> getColumns(String fieldType);


    /**
     * Get the export file description.
     *
     * @return {@link CsvExportFile}
     */
    @Override
    CsvExportFile getExportFileDescriptor();

}
