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