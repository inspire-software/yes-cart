package org.yes.cart.bulkimport.service.impl;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkimport.service.BulkImportImagesService;
import org.yes.cart.bulkimport.service.BulkImportService;
import org.yes.cart.bulkimport.service.ImportDirectorService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Import Director class to perform import via {@link BulkImportService}
 * collect imported files and move it to archive folder.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ImportDirectorImplService implements ImportDirectorService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportDirectorImplService.class);

    private BulkImportService bulkImportService;

    private BulkImportImagesService bulkImportImagesService;

    private List<String> importDescriptors;

    private String pathToImportDescriptors;

    private String pathToArchiveFolder;

    private String pathToImportImagesFolder;

    private SystemService systemService;

    private ProductService productService;


    /**
     * IoC. Set product service to use.
     *
     * @param productService product service.
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    /**
     * IoC.
     * Set system service to get the imports path.
     *
     * @param systemService {@link SystemService}
     */
    public void setSystemService(final SystemService systemService) {
        this.systemService = systemService;
    }

    /**
     * Set path to folder where located images to import.
     *
     * @param pathToImportImagesFolder import images folder.
     */
    public void setPathToImportImagesFolder(final String pathToImportImagesFolder) {
        this.pathToImportImagesFolder = pathToImportImagesFolder;
    }

    /**
     * Set path to import descriptors.
     *
     * @param pathToImportDescriptors path to use.
     */
    public void setPathToImportDescriptors(final String pathToImportDescriptors) {
        this.pathToImportDescriptors = pathToImportDescriptors;
    }


    /**
     * Perform bulk import.
     *
     * @param errorReport   error report place holder
     * @param importedFiles imported files
     */
    public void doImportInternal(final StringBuilder errorReport, final Set<String> importedFiles, final String fileName) {
        doDataImport(errorReport, importedFiles, fileName);
        //doImageImport(errorReport, importedFiles, fileName);
        moveImportFilesToArchive(importedFiles);
    }

    /**
     * Perform bulk import.
     *
     * @return error report
     */
    public String doImport() {
        return doImport(null);
    }

    /**
     * Perform bulk import.
     *
     * @return error report
     */
    public String doImport(final String fileName) {


        this.setPathToImportDescriptors(systemService.getImportDescritorsDirectory());
        this.setPathToImportImagesFolder(systemService.getImportDirectory());
        this.setPathToArchiveFolder(systemService.getImportArchiveDirectory());


        Set<String> importedFiles = new HashSet<String>();
        StringBuilder stringBuilder = new StringBuilder();
        doImportInternal(stringBuilder, importedFiles, fileName);

        productService.clearEmptyAttributes();

        return stringBuilder.toString();


    }

    /*private void doImageImport(final StringBuilder errorReport, final Set<String> importedFiles, final String fileName) {
        bulkImportImagesService.setPathToImportFolder(pathToImportImagesFolder);
        bulkImportImagesService.doImport(errorReport, importedFiles);
    } */

    private void doDataImport(final StringBuilder errorReport, final Set<String> importedFiles, final String fileName) {
        String fullPathToDescriptor = pathToImportDescriptors;
        for (String descriptor : importDescriptors) {
            bulkImportService.setPathToImportDescriptor(
                    fullPathToDescriptor + descriptor
            );
            bulkImportService.doImport(errorReport, importedFiles, fileName);
        }
    }

    /**
     * Move import files to archive folder
     *
     * @param importedFiles set of absolute file names.
     */
    protected void moveImportFilesToArchive(final Set<String> importedFiles) {
        if (!importedFiles.isEmpty()) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd-hh-mm-ss");
            final String fullPathToArchiveFolder = pathToArchiveFolder;
            File dir = new File(fullPathToArchiveFolder + dateFormat.format(new Date()) + File.separator);
            dir.mkdirs();
            for (String importFileName : importedFiles) {
                try {
                    File importFile = new File(importFileName);
                    FileUtils.copyFileToDirectory(importFile, dir, true);
                    importFile.delete();
                } catch (IOException e) {
                    LOG.error(
                            MessageFormat.format("Cant move file {0} to folder {1}", importFileName, dir.getAbsolutePath()),
                            e
                    );
                }
            }
        }
    }


    /**
     * IoC. Set the {@link BulkImportService}
     *
     * @param bulkImportService {@link BulkImportService}
     */
    public void setBulkImportService(final BulkImportService bulkImportService) {
        this.bulkImportService = bulkImportService;
    }

    /**
     * IoC. Set the list of import descriptors.
     *
     * @param importDescriptors import descriptors
     */
    public void setImportDescriptors(final List<String> importDescriptors) {
        this.importDescriptors = importDescriptors;
    }

    /**
     * Construct the import director
     *
     * @param bulkImportService       {@link BulkImportService}
     * @ par am bulkImportImagesService {@link BulkImportImagesService}
     * @param importDescriptors       import descriptors
     */
    public ImportDirectorImplService(
            final BulkImportService bulkImportService,
            //final BulkImportImagesService bulkImportImagesService,
            final List<String> importDescriptors) {
        this.bulkImportService = bulkImportService;
        this.importDescriptors = importDescriptors;
        //this.bulkImportImagesService = bulkImportImagesService;
    }

    /**
     * Get path to archive folder.
     *
     * @return path to archive folder
     */
    protected String getPathToArchiveFolder() {
        return pathToArchiveFolder;
    }

    /**
     * IoC or config. Set path to archive folder where files will be moved after import procedure.
     *
     * @param pathToArchiveFolder path to archive folder.
     */
    public void setPathToArchiveFolder(final String pathToArchiveFolder) {
        this.pathToArchiveFolder = pathToArchiveFolder;
    }

    /**
     * IoC. Set the {@link BulkImportImagesService} to use.
     *
     * @param bulkImportImagesService the {@link BulkImportImagesService} to use.
     */
    public void setBulkImportImagesService(final BulkImportImagesService bulkImportImagesService) {
        this.bulkImportImagesService = bulkImportImagesService;
    }
}
