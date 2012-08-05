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

import org.yes.cart.service.async.JobStatusListener;

import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/12/11
 * Time: 2:34 PM
 */
public interface ImportService {

     public enum BulkImportResult {
        OK("OK"),
        ERROR("ERROR");

        private final String result;

        BulkImportResult(final String result) {
            this.result = result;
        }
    }

    /**
     * Perform bulk import.
     *
     * @param listener   error report place holder
     * @param importedFiles imported files
     * @param fileName optional file  name
     * @return {@link BulkImportResult}
     */
    BulkImportResult doImport(JobStatusListener listener, Set<String> importedFiles, String fileName, String pathToImportFolder);

}
