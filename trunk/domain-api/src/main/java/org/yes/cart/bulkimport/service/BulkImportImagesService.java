package org.yes.cart.bulkimport.service;

import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface BulkImportImagesService extends ImportService {

    /**
     * Set path to import folder.
     * @param pathToImportFolder import folder.
     */
    public void setPathToImportFolder(String pathToImportFolder);


    /**
     * The path to product image repository.
     * @param pathToRepository  path to product image repository.
     */
    public void setPathToRepository(String pathToRepository);

}
