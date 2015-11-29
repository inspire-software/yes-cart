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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Csv string writer.
 * <p/>
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 08:00
 */
public interface CsvStringWriter {

    /**
     * Open a csv file for export.
     *
     * @param headers         the column headers
     * @param columnDelimiter the column delimiter
     * @param textQualifier   the text qualifer
     * @param encoding        csv file encoding
     * @param printHeaders    print headers flag.
     * @throws UnsupportedEncodingException
     *                                       in case if given encoding not supported.
     */
    void open(String[] headers,
              char columnDelimiter,
              char textQualifier,
              String encoding,
              boolean printHeaders) throws UnsupportedEncodingException;

    /**
     * Write the line to csv file.
     *
     * @param line line for csv file.
     *
     * @throws IOException if case of error.
     */
    void writeLine(String[] line) throws IOException;

    /**
     * Close csv string.
     */
    String close() throws IOException;

    /**
     * Get the quantity of written rows, printHeaders parameter not affect the write rows counter.
     *
     * @return rows quantity.
     */
    int getRowsWritten();

}
