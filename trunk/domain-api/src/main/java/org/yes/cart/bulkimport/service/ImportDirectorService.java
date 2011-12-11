package org.yes.cart.bulkimport.service;

import java.util.Set;

/**
 *
 * Import Director class to perform import via {@link org.yes.cart.bulkimport.service.BulkImportService}
 *   collect imported files and move it to archive folder.

 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface ImportDirectorService {


    /**
     * Perform bulk import.
     * @return error report
     */
    String doImport();


    /**
     * Perform bulk import.
     * @param fileName optional full filename to import
     * @return error report
     */
    String doImport(String fileName);

}
