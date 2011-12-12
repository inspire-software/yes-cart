package org.yes.cart.bulkimport.service;


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


}
