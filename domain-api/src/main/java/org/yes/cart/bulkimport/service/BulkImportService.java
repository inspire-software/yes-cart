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

package org.yes.cart.bulkimport.service;


import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.service.async.JobStatusListener;

/**
 * Bulk Import desriptor service.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface BulkImportService extends ImportService {

    /**
     * Set path to import descriptor.
     *
     * @param pathToImportDescriptor path to use.
     */
    void setPathToImportDescriptor(String pathToImportDescriptor);

    /**
     * Import single tuple of data. This method can be called recursive in case
     * of cumulative imports.
     *
     * @param statusListener   status listener
     * @param tuple             single line from csv file
     * @param importDescriptor import descriptor
     * @param masterObject     optional master object if found sub import
     */
    void doImport(JobStatusListener statusListener,
                  ImportTuple tuple,
                  ImportDescriptor importDescriptor,
                  Object masterObject);


}
