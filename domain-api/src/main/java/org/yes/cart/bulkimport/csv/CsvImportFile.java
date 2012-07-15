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

import org.yes.cart.bulkimport.model.ImportFile;

/**
 * Csv specific import file decriptor with additional information about
 * delimiter, qualifier.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 11:43 AM
 */
public interface CsvImportFile extends ImportFile {

    /**
     * Ignore first line during import. Csv file can have first line as table description header.
     *
     * @return true if first line must be ignored.
     */
    boolean isIgnoreFirstLine();

    /**
     * Set ignore first line flag.
     *
     * @param ignoreFirstLine ignore first line flag.
     */
    void setIgnoreFirstLine(boolean ignoreFirstLine);

    /**
     * Get the column delimiter.
     *
     * @return column delimiter
     */
    char getColumnDelimeter();

    /**
     * Set column delimiter.
     *
     * @param columnDelimeter column delimiter
     */
    void setColumnDelimeter(char columnDelimeter);

    /**
     * Get text qualifier.
     *
     * @return text qualifier
     */
    char getTextQualifier();

    /**
     * Set text qualifier.
     *
     * @param textQualifier text qualifier
     */
    void setTextQualifier(char textQualifier);


}