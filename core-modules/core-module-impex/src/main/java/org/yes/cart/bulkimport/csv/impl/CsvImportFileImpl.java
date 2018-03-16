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

package org.yes.cart.bulkimport.csv.impl;

import org.yes.cart.bulkimport.csv.CsvImportFile;

import java.io.Serializable;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 12:06 PM
 */
public class CsvImportFileImpl implements CsvImportFile, Serializable {

    private char columnDelimiter;
    private char textQualifier;
    private boolean ignoreFirstLine;
    private String fileNameMask;
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
    public boolean isIgnoreFirstLine() {
        return ignoreFirstLine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIgnoreFirstLine(boolean ignoreFirstLine) {
        this.ignoreFirstLine = ignoreFirstLine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileNameMask() {
        return fileNameMask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileNameMask(String fileNameMask) {
        this.fileNameMask = fileNameMask;
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
                ", ignoreFirstLine=" + ignoreFirstLine +
                ", fileNameMask='" + fileNameMask + '\'' +
                ", fileEncoding='" + fileEncoding + '\'' +
                '}';
    }
}
