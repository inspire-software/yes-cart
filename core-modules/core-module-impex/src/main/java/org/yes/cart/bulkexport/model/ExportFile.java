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

package org.yes.cart.bulkexport.model;

/**
 * Common descriptor for import file.
 * <p/>
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 08:00
 */
public interface ExportFile {

    /**
     * Get the file name. For example products-{timestamp}.csv
     *
     * @return File name mask
     */
    String getFileName();

    /**
     * Set the file name.
     *
     * @param fileName file name
     */
    void setFileName(String fileName);

    /**
     * Fet the file encoding.
     *
     * @return file encoding
     */
    String getFileEncoding();

    /**
     * Set file encoding.
     *
     * @param fileEncoding file encoding.
     */
    void setFileEncoding(String fileEncoding);



}
