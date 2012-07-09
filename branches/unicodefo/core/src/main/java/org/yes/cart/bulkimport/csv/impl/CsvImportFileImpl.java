package org.yes.cart.bulkimport.csv.impl;

import org.yes.cart.bulkimport.csv.CsvImportFile;

import java.io.Serializable;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 12:06 PM
 */
public class CsvImportFileImpl implements CsvImportFile, Serializable {

    private char columnDelimeter;
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
    public char getColumnDelimeter() {
        return columnDelimeter;
    }

    /**
     * {@inheritDoc
     */
    public void setColumnDelimeter(char columnDelimeter) {
        this.columnDelimeter = columnDelimeter;
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
