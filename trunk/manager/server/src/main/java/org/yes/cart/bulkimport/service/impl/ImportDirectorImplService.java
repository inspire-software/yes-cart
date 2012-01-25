package org.yes.cart.bulkimport.service.impl;

import flex.messaging.FlexContext;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.yes.cart.bulkimport.service.BulkImportImagesService;
import org.yes.cart.bulkimport.service.BulkImportService;
import org.yes.cart.bulkimport.service.ImportDirectorService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.utils.impl.ZipUtils;

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
public class ImportDirectorImplService implements ImportDirectorService, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private final BulkImportService bulkImportService;

    private final BulkImportImagesService bulkImportImagesService;

    private final List<String> importDescriptors;

    private final String pathToImportDescriptors;

    private final String pathToArchiveFolder;

    private final String pathToImportFolder;

    private final ProductService productService;

    private ApplicationContext applicationContext;


    /**
     * Construct the import director
     *
     * @param bulkImportService       {@link BulkImportService}
     * @param importDescriptors       import descriptors
     * @param pathToArchiveFolder     path to archive folder.
     * @param pathToImportDescriptors path to use.
     * @param pathToImportFolder      path to use.
     * @ par am bulkImportImagesService {@link BulkImportImagesService}
     */
    public ImportDirectorImplService(
            final BulkImportService bulkImportService,
            final BulkImportImagesService bulkImportImagesService,
            final List<String> importDescriptors,
            final String pathToArchiveFolder,
            final String pathToImportDescriptors,
            final String pathToImportFolder,
            final ProductService productService) {
        this.bulkImportService = bulkImportService;
        this.pathToImportDescriptors = pathToImportDescriptors;
        this.pathToArchiveFolder = pathToArchiveFolder;
        this.pathToImportFolder = pathToImportFolder;
        this.importDescriptors = importDescriptors;
        this.bulkImportImagesService = bulkImportImagesService;
        this.productService = productService;
    }


    /**
     * Perform bulk import.
     *
     * @param errorReport   error report place holder
     * @param importedFiles imported files
     * @param fileName      file name to import
     */
    public void doImportInternal(final StringBuilder errorReport, final Set<String> importedFiles, final String fileName) throws IOException {
        doDataImport(errorReport, importedFiles, fileName);
        doImageImport(errorReport, importedFiles, fileName);
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
        Set<String> importedFiles = new HashSet<String>();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (fileName.endsWith(".zip")) {
                importedFiles.add(fileName);
                ZipUtils.unzipArchive(fileName, pathToImportFolder);
                doImportInternal(stringBuilder, importedFiles, null);
            } else {
                doImportInternal(stringBuilder, importedFiles, fileName); //single file import
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR " + e.getMessage();

        }
        productService.clearEmptyAttributes();
        return stringBuilder.toString();
    }

    private void doImageImport(final StringBuilder errorReport, final Set<String> importedFiles, final String fileName) {
        bulkImportImagesService.setPathToRepository(FlexContext.getServletContext().getRealPath("/../yes-shop/default/imagevault") + File.separator); //TODO remove this hardcode. alsoo see remoteImageServiceimpl
        bulkImportImagesService.doImport(errorReport, importedFiles, fileName, this.pathToImportFolder);
    }

    private void doDataImport(final StringBuilder errorReport, final Set<String> importedFiles, final String fileName) throws IOException {
        for (String descriptor : importDescriptors) {
            Resource res = applicationContext.getResource("WEB-INF" + File.separator + pathToImportDescriptors + File.separator + descriptor);
            bulkImportService.setPathToImportDescriptor(res.getFile().getAbsolutePath());
            bulkImportService.doImport(errorReport, importedFiles, fileName, this.pathToImportFolder);
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
            File dir = new File(fullPathToArchiveFolder + File.separator + dateFormat.format(new Date()) + File.separator);
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
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
