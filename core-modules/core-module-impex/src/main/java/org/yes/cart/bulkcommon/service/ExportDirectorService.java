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

package org.yes.cart.bulkcommon.service;

import org.yes.cart.service.async.model.JobStatus;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12-08-11
 * Time: 12:54 PM
 */
public interface ExportDirectorService {

    /**
     * List of import groups. Each group is represented by a map and contains the following
     * keys: "name", "label".
     *
     * @param language language
     *
     * @return configured import groups
     */
    List<Map<String, String>> getExportGroups(String language);

    /**
     * Absolute path to export directory.
     *
     * @return path to export
     */
    String getExportDirectory();

    /**
     * Perform bulk export.
     * @param descriptorGroup descriptor group marker
     * @param fileName optional override full filename to export
     * @param async if true then perform asynchronous export
     * @return status object token
     */
    String doExport(String descriptorGroup, String fileName, boolean async);

    /**
     * Get latest job status update for given token
     * @param token job token from #doExport
     * @return status object
     */
    JobStatus getExportStatus(String token);

}
