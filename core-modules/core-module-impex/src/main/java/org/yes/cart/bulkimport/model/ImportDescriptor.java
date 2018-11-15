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

package org.yes.cart.bulkimport.model;

import org.yes.cart.bulkcommon.model.ImpExContext;
import org.yes.cart.bulkcommon.model.ImpExDescriptor;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public interface ImportDescriptor<C extends ImpExContext> extends ImpExDescriptor<C> {

    /**
     * Get the import file description.
     *
     * @return {@link org.yes.cart.bulkimport.model.ImportFile}
     */
    ImportFile getImportFileDescriptor();

    /**
     * Get the directory with stored file to import.
     *
     * @return configured import folder.
     */
    String getImportDirectory();

    /**
     * Set the import directory.
     *
     * @param importDirectory import directory to use.
     */
    void setImportDirectory(String importDirectory);

}
