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

import org.yes.cart.bulkimport.model.ImportJobStatus;

import java.util.Set;

/**
 *
 * Import Director class to perform import via {@link org.yes.cart.bulkimport.service.BulkImportService}
 *   collect imported files and move it to archive folder.

 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface ImportDirectorService {


    /**
     * Perform bulk import.
     * @param async if true then perform asynchronous import
     * @return status object token
     */
    String doImport(boolean async);


    /**
     * Perform bulk import.
     * @param fileName optional full filename to import
     * @param async if true then perform asynchronous import
     * @return status object token
     */
    String doImport(String fileName, boolean async);

    /**
     * Get latest job status update for given token
     * @param token job token from #doImport
     * @return status object
     */
    ImportJobStatus getImportStatus(String token);

}
