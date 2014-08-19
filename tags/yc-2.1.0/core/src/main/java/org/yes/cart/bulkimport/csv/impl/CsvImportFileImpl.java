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
    private String entityName;

    /**
     * Get the entity interface name.
     *
     * @return entity interface name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Set the entity interface name.
     *
     * @param entityName entity interface name
     */
    public void setEntityName(final String entityName) {
        this.entityName = entityName;
    }

    /**
     * {@inheritDoc
     */
    public char getColumnDelimiter() {
        return columnDelimiter;
    }

    /**
     * {@inheritDoc
     */
    public void setColumnDelimiter(char columnDelimiter) {
        this.columnDelimiter = columnDelimiter;
    }

    /**
     * {@inheritDoc
     */
    public char getTextQualifier() {
        return textQualifier;
    }

    /**
     * {@inheritDoc
     */
    public void setTextQualifier(char textQualifier) {
        this.textQualifier = textQualifier;
    }

    /**
     * {@inheritDoc
     */
    public boolean isIgnoreFirstLine() {
        return ignoreFirstLine;
    }

    /**
     * {@inheritDoc
     */
    public void setIgnoreFirstLine(boolean ignoreFirstLine) {
        this.ignoreFirstLine = ignoreFirstLine;
    }

    /**
     * {@inheritDoc
     */
    public String getFileNameMask() {
        return fileNameMask;
    }

    /**
     * {@inheritDoc
     */
    public void setFileNameMask(String fileNameMask) {
        this.fileNameMask = fileNameMask;
    }

    /**
     * {@inheritDoc
     */
    public String getFileEncoding() {
        return fileEncoding;
    }

    /**
     * {@inheritDoc
     */
    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }
}
