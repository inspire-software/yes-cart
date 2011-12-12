package org.yes.cart.bulkimport.service;

import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/12/11
 * Time: 2:34 PM
 */
public interface ImportService {

     public enum BulkImportResult {
        OK("OK"),
        ERROR("ERROR");

        private final String result;

        BulkImportResult(final String result) {
            this.result = result;
        }
    }





    /**
     * Perform bulk import.
     *
     *
     * @param errorReport   error report place holder
     * @param importedFiles imported files
     * @param fileName optional file  name
     * @return {@link BulkImportResult}
     */
    BulkImportResult doImport(StringBuilder errorReport, Set<String> importedFiles, String fileName, String pathToImportFolder);

}
