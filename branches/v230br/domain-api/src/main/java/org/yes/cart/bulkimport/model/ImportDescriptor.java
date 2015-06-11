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

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public interface ImportDescriptor {

    enum ImportMode { MERGE, DELETE }

    /**
     * Get import mode for given descriptor.
     *
     * @return import mode
     */
    ImportMode getMode();

    /**
     * Get import context if one provided or null.
     *
     * @return import context
     */
    ImportContext getContext();

    /**
     * Get import mode for given descriptor.
     *
     * @return import mode
     */
    String getModeName();

    /**
     * Get full qualified entity interface. For example - org.yes.cart.domain.entity.Brand
     *
     * @return full qualified entity interface
     */
    String getEntityType();

    /**
     * Get entity interface. For example - org.yes.cart.domain.entity.Brand
     *
     * @return entity interface
     */
    Class getEntityTypeClass();

    /**
     * Get the import file description.
     *
     * @return {@link org.yes.cart.bulkimport.model.ImportFile}
     */
    ImportFile getImportFileDescriptor();

    /**
     * Get the collection of import columns.
     *
     * @return collection of import columns
     */
    Collection<ImportColumn> getImportColumns();


    /**
     * @param columnName column name
     * @return get column by name
     */
    ImportColumn getImportColumn(String columnName);

    /**
     * Get the collection of import columns filtered by given field type.
     *
     * @param fieldType {@link FieldTypeEnum} discriminator.
     * @return collection of import columns
     */
    Collection<ImportColumn> getImportColumns(FieldTypeEnum fieldType);


    /**
     * Get the folder with stored file to import.
     *
     * @return configured import folder.
     */
    String getImportDirectory();

    /**
     * Set the import folder.
     *
     * @param importFolder import folder to use.
     */
    void setImportDirectory(String importFolder);

    /**
     * Get select sql, which used to look up objects that are to
     * be modified (if they exist).
     * @return        select sql
     */
    String getSelectSql();

    /**
     * Get insert sql, which used instead of hibernate object save to
     * speed up bulk import.
     *
     * @return        insert sql
     */
    String getInsertSql();

    /**
     * Get delete sql, which used instead of hibernate object delete to
     * speed up bulk import.
     *
     * @return        delete sql
     */
    String getDeleteSql();


}
