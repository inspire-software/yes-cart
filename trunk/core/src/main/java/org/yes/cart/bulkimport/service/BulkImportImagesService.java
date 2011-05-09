package org.yes.cart.bulkimport.service;

import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface BulkImportImagesService {

    /**
     * Set path to import folder, that hold images.
     *
     * @param pathToImportFolder path to use.
     */
    void setPathToImportFolder(String pathToImportFolder);

    /**
     * Perform images bulk import.
     *
     * @param errorReport   error report place holder
     * @param importedFiles imported files
     */
    void doImport(StringBuilder errorReport, Set<String> importedFiles);
}
