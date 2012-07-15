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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Csv file reader.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 11:41 AM
 */
public interface CsvFileReader {

    /**
     * Open a csv file for import.
     *
     * @param csvFileName     the csv file name
     * @param columnDelimeter the column delimeter
     * @param textQualifier   the text qualifer
     * @param encoding        csv file encoding
     * @param ignoreFirstLine ignore first line flag.
     * @throws java.io.FileNotFoundException if give file can not be found.
     * @throws java.io.UnsupportedEncodingException
     *                                       in case if given encoding not supported.
     */
    void open(String csvFileName,
              char columnDelimeter,
              char textQualifier,
              String encoding,
              boolean ignoreFirstLine) throws FileNotFoundException, UnsupportedEncodingException;

    /**
     * Read the line from csv file.
     *
     * @return line from cvs file of null in case eof.
     * @throws java.io.IOException if case of error.
     */
    String[] readLine() throws IOException;

    /**
     * Close csv file.
     *
     * @throws IOException in case if cvs reader can not close the csv file.
     */
    void close() throws IOException;

    /**
     * Get the quantity of readed rows, ignoreFirstLine parameter not affect the read rows counter.
     *
     * @return rows quantity.
     */
    int getRowsRead();

}
