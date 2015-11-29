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

package org.yes.cart.bulkexport.csv.impl;

import au.com.bytecode.opencsv.CSVWriter;
import org.yes.cart.bulkexport.csv.CsvFileWriter;

import java.io.*;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 11:24
 */
public class CsvFileWriterImpl implements CsvFileWriter {


    private int rowsWritten;

    private String[] headers;

    private CSVWriter csvWriter = null;

    private FileOutputStream fileOutputStream = null;

    private OutputStreamWriter outputStreamWriter = null;

    boolean printHeaders;


    /**
     * {@inheritDoc}
     */
    public void open(final String csvFileName,
                     final String[] headers,
                     final char columnDelimiter,
                     final char textQualifier,
                     final String encoding,
                     final boolean printHeaders) throws FileNotFoundException, UnsupportedEncodingException {

        fileOutputStream = new FileOutputStream(csvFileName);
        outputStreamWriter = new OutputStreamWriter(fileOutputStream, encoding);
        csvWriter = new CSVWriter(new BufferedWriter(outputStreamWriter), columnDelimiter, textQualifier);
        this.printHeaders = printHeaders;
        this.headers = headers;
        this.rowsWritten = 0;
    }

    /**
     * {@inheritDoc}
     */
    public void writeLine(final String[] line) throws IOException {
        if (rowsWritten == 0 && printHeaders) {
            rowsWritten++;
            csvWriter.writeNext(headers);
        }
        if (line != null) {
            csvWriter.writeNext(line);
            rowsWritten++;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        if (csvWriter != null) {
            csvWriter.close();
        }
        if (outputStreamWriter != null) {
            outputStreamWriter.close();
        }
        if (fileOutputStream != null) {
            fileOutputStream.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getRowsWritten() {
        return rowsWritten;
    }

}
