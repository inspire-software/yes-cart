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

package org.yes.cart.bulkexport.csv;

import org.yes.cart.bulkexport.model.ExportFile;

/**
 * Csv specific export file descriptor with additional information about
 * delimiter, qualifier.
 * <p/>
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 08:00
 */
public interface CsvExportFile extends ExportFile {

    /**
     * Print header line during import. Csv file can have first line as table description header.
     *
     * @return true if header must be output.
     */
    boolean isPrintHeader();

    /**
     * Set ignore header line flag.
     *
     * @param printHeader print header line flag.
     */
    void setPrintHeader(boolean printHeader);

    /**
     * Get the column delimiter.
     *
     * @return column delimiter
     */
    char getColumnDelimiter();

    /**
     * Set column delimiter.
     *
     * @param columnDelimeter column delimiter
     */
    void setColumnDelimiter(char columnDelimeter);

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