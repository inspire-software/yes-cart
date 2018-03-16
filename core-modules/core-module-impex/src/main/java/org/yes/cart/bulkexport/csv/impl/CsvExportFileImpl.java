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

import org.yes.cart.bulkexport.csv.CsvExportFile;

import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 11:24
 */
public class CsvExportFileImpl implements CsvExportFile, Serializable {

    private char columnDelimiter;
    private char textQualifier;
    private String lineEnd;
    private boolean printHeader;
    private String fileName;
    private String fileEncoding;

    /**
     * {@inheritDoc}
     */
    @Override
    public char getColumnDelimiter() {
        return columnDelimiter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColumnDelimiter(char columnDelimiter) {
        this.columnDelimiter = columnDelimiter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char getTextQualifier() {
        return textQualifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextQualifier(char textQualifier) {
        this.textQualifier = textQualifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLineEnd() {
        return lineEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineEnd(final String lineEnd) {
        this.lineEnd = lineEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrintHeader() {
        return printHeader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrintHeader(boolean printHeader) {
        this.printHeader = printHeader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileEncoding() {
        return fileEncoding;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "CsvImportFileImpl{" +
                "columnDelimiter=" + columnDelimiter +
                ", textQualifier=" + textQualifier +
                ", printHeader=" + printHeader +
                ", fileName='" + fileName + '\'' +
                ", fileEncoding='" + fileEncoding + '\'' +
                '}';
    }
}
