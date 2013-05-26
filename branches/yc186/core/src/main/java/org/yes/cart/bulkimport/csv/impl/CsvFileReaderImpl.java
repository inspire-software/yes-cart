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

package org.yes.cart.bulkimport.csv.impl;

import au.com.bytecode.opencsv.CSVReader;
import org.yes.cart.bulkimport.csv.CsvFileReader;

import java.io.*;

/**
 * Csv file reader implementation.
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 12:02 PM
 */
public class CsvFileReaderImpl implements CsvFileReader {

    private int rowsRead;

    private CSVReader csvReader = null;

    private FileInputStream fileInputStream = null;

    private InputStreamReader inputStreamReader = null;

    boolean ignoreFirstLine;


    /**
     * {@inheritDoc}
     */
    public void open(final String csvFileName,
                     final char columnDelimeter,
                     final char textQualifier,
                     final String encoding,
                     final boolean ignoreFirstLine) throws FileNotFoundException, UnsupportedEncodingException {

        fileInputStream = new FileInputStream(csvFileName);
        inputStreamReader = new InputStreamReader(fileInputStream, encoding);
        csvReader = new CSVReader(new BufferedReader(inputStreamReader), columnDelimeter, textQualifier);
        this.ignoreFirstLine = ignoreFirstLine;
        this.rowsRead = 0;
    }

    /**
     * {@inheritDoc}
     */
    public String[] readLine() throws IOException {
        if (rowsRead == 0 && ignoreFirstLine) {
            rowsRead++;
            csvReader.readNext();
        }
        final String[] line = csvReader.readNext();
        if (line != null) {
            rowsRead++;
        }
        return line;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        if (csvReader != null) {
            csvReader.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (fileInputStream != null) {
            fileInputStream.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getRowsRead() {
        return rowsRead;
    }

}
