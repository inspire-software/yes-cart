package org.yes.cart.bulkimport.service;

import org.yes.cart.bulkcommon.service.ImportService;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.service.async.JobStatusListener;

import java.io.File;

/**
 * User: denispavlov
 * Date: 20/01/2016
 * Time: 14:04
 */
public interface ImportServiceSingleFile<ID extends ImportDescriptor> {

    /**
     * Perform import for single file.
     *
     * @param statusListener       error report
     * @param fileToImport         array of files to import
     * @param importDescriptorName file name of the descriptor
     * @param importDescriptor     import descriptor.
     *
     * @return result of the import
     */
    ImportService.BulkImportResult doSingleFileImport(final JobStatusListener statusListener,
                                                      final File fileToImport,
                                                      final String importDescriptorName,
                                                      final ID importDescriptor) throws Exception;

}
