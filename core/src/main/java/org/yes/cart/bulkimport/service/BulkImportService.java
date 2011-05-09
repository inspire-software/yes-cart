package org.yes.cart.bulkimport.service;

import java.util.Set;

/**
 * Bulk Import desriptor service. All implementations in particular modules.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface BulkImportService {

    public enum BulkImportResult {
        OK("OK"),
        ERROR("ERROR");

        private final String result;

        BulkImportResult(final String result) {
            this.result = result;
        }
    }


    /**
     * Set path to import descriptor.
     *
     * @param pathToImportDescriptor path to use.
     */
    void setPathToImportDescriptor(String pathToImportDescriptor);


    /**
     * Perform bulk import.
     *
     * @param errorReport   error report place holder
     * @param importedFiles imported files
     * @return {@link BulkImportResult}
     */
    BulkImportResult doImport(StringBuilder errorReport, Set<String> importedFiles);


}
