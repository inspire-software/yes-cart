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

package org.yes.cart.bulkimport.csv;

import org.yes.cart.bulkimport.model.FieldTypeEnum;
import org.yes.cart.bulkimport.model.ImportColumn;
import org.yes.cart.bulkimport.model.ImportDescriptor;

import java.util.Collection;

/**
 * Csv Import descriptor.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 11:49 AM
 */
public interface CvsImportDescriptor extends ImportDescriptor {


    /**
     * Get the import file decription.
     *
     * @return {@link CsvImportFile}
     */
    CsvImportFile getImportFile();

    /**
     * Get the collection of all import columns.
     *
     * @return collection of import columns
     */
    Collection<ImportColumn> getImportColumns();

    /**
     * Get the collection of import columns filtered by given field type.
     *
     * @param fieldType {@link FieldTypeEnum} discriminator.
     * @return collection of import columns
     */
    Collection<ImportColumn> getImportColumns(FieldTypeEnum fieldType);


    /**
     * Get the {@link ImportColumn} for object lookup.
     *
     * @return {@link ImportColumn} if found, otherwise null.
     */
    ImportColumn getPrimaryKeyColumn();

}
