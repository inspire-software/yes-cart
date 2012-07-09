package org.yes.cart.bulkimport.service;


import org.yes.cart.bulkimport.csv.CvsImportDescriptor;
import org.yes.cart.bulkimport.model.ImportColumn;

/**
 * Bulk Import desriptor service.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface BulkImportService extends ImportService {

    /**
     * Set path to import descriptor.
     *
     * @param pathToImportDescriptor path to use.
     */
    void setPathToImportDescriptor(String pathToImportDescriptor);

    /**
     * Import single line. This method can be called recursive in case of sum imports.
     *
     * @param errorReport      error report
     * @param line             single line from csv file
     * @param importDescriptor import desciptor
     * @param pkColumn         column to locate object.
     * @param masterObject     optional master object if found sub import
     */
    void doImport(StringBuilder errorReport,
                  String[] line,
                  CvsImportDescriptor importDescriptor,
                  ImportColumn pkColumn,
                  Object masterObject);


}
