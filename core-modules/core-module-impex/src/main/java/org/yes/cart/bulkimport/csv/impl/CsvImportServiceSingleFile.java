package org.yes.cart.bulkimport.csv.impl;

import org.yes.cart.bulkcommon.service.ImportService;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.service.async.JobStatusListener;

import java.io.File;

/**
 * User: denispavlov
 * Date: 20/01/2016
 * Time: 14:04
 */
public interface CsvImportServiceSingleFile {

    /**
     * Perform import for single file.
     *
     * @param statusListener      error report
     * @param fileToImport        array of files to import
     * @param csvImportDescriptorName file name of the descriptor
     * @param csvImportDescriptor import descriptor.
     *
     * @return result of the import
     */
    ImportService.BulkImportResult doSingleFileImport(final JobStatusListener statusListener,
                                                      final File fileToImport,
                                                      final String csvImportDescriptorName,
                                                      final CsvImportDescriptor csvImportDescriptor) throws Exception;

}
