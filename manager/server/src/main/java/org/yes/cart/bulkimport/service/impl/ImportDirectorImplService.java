/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.bulkimport.service.impl;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.bulkimport.model.ImportJobStatus;
import org.yes.cart.bulkimport.service.*;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.utils.impl.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

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

    private final TaskExecutor executor;

    private final ReentrantLock lock = new ReentrantLock();

    private final Map<String, BulkImportStatusListener> jobListeners = new HashMap<String, BulkImportStatusListener>();


    /**
     * Construct the import director
     *
     * @param bulkImportService       {@link org.yes.cart.bulkimport.service.BulkImportService}
     * @param importDescriptors       import descriptors
     * @param pathToArchiveFolder     path to archive folder.
     * @param pathToImportDescriptors path to use.
     * @param pathToImportFolder      path to use.
     * @param bulkImportImagesService {@link org.yes.cart.bulkimport.service.BulkImportImagesService}
     * @param executor                async executor
     */
    public ImportDirectorImplService(
            final BulkImportService bulkImportService,
            final BulkImportImagesService bulkImportImagesService,
            final List<String> importDescriptors,
            final String pathToArchiveFolder,
            final String pathToImportDescriptors,
            final String pathToImportFolder,
            final ProductService productService, final TaskExecutor executor) {
        this.bulkImportService = bulkImportService;
        this.pathToImportDescriptors = pathToImportDescriptors;
        this.pathToArchiveFolder = pathToArchiveFolder;
        this.pathToImportFolder = pathToImportFolder;
        this.importDescriptors = importDescriptors;
        this.bulkImportImagesService = bulkImportImagesService;
        this.productService = productService;
        this.executor = executor;
    }


    /**
     * Perform bulk import.
     *
     * @param listener      error report place holder
     * @param importedFiles imported files
     * @param fileName      file name to import
     */
    public void doImportInternal(final BulkImportStatusListener listener, final Set<String> importedFiles, final String fileName) throws IOException {
        doDataImport(listener, importedFiles, fileName);
        doImageImport(listener, importedFiles, fileName);
        moveImportFilesToArchive(importedFiles);
    }

    /** {@inheritDoc} */
    public ImportJobStatus getImportStatus(final String token) {
        if (token == null || !jobListeners.containsKey(token)) {
            throw new IllegalArgumentException("Job token: " + token + " unknown");
        }
        final BulkImportStatusListener listener = jobListeners.get(token);
        final ImportJobStatus status = listener.getLatestStatus();
        if (status.getState() == ImportJobStatus.State.FINISHED || status.getState() == ImportJobStatus.State.UNDEFINED) {
            jobListeners.remove(token); // remove those for which we ask for the last time
        }
        return status;
    }

    /** {@inheritDoc} */
    public String doImport(final boolean async) {
        return doImport(null, async);
    }

    /** {@inheritDoc} */
    public String doImport(final String fileName, final boolean async) {

        if (lock.isLocked()) {
            if (jobListeners.isEmpty()) {
                lock.unlock(); // if we do not have any listeners then it is all done
            } else {
                // check for locks. it would be better to do this at the end of runnable
                // however if that runnable crashes we will be locked forever.
                boolean shouldUnlock = true;
                for (final BulkImportStatusListener listener : jobListeners.values()) {
                    if (!listener.isCompleted() && !listener.isTimedOut()) {
                        shouldUnlock = false;
                        break;
                    }
                }
                if (shouldUnlock) {
                    lock.unlock(); // unlock because all is completed or timed out
                }
            }
        }

        if (!lock.isLocked()) {
            lock.lock();

            /*
             * Max 10K char of report to UI since it get huge and simply will crash the UI,
             * not to mention traffic cost.
             * Timeout is set to 60sec - just in case runnable crashes and we need to unlock through
             * timeout.
             */
            final BulkImportStatusListener listener = new BulkImportStatusListenerImpl(10000, 60000);
            jobListeners.put(listener.getJobToken(), listener);

            final Runnable job = createImportJobRunnable(fileName, listener);
            if (async) {
                executor.execute(job);
            } else {
                job.run();
            }

            return listener.getJobToken();
        } else {
            final BulkImportStatusListener listener = new BulkImportStatusListenerNullImpl("ERROR: Import job is already running");
            jobListeners.put(listener.getJobToken(), listener);
            return listener.getJobToken();
        }
    }

    private Runnable createImportJobRunnable(final String fileName, final BulkImportStatusListener statusListener) {
        return new Runnable() {

            private final String file = fileName;
            private final BulkImportStatusListener listener = statusListener;

            public void run() {
                try {
                    Set<String> importedFiles = new HashSet<String>();

                    if (file.matches("(.*)\\.zip(.*)")) {
                        importedFiles.add(file);
                        ZipUtils.unzipArchive(file, pathToImportFolder);
                        doImportInternal(listener, importedFiles, null);
                    } else {
                        doImportInternal(listener, importedFiles, file); //single file import
                    }
                    productService.clearEmptyAttributes();
                    listener.notifyCompleted(ImportService.BulkImportResult.OK);
                } catch (IOException ioe) {
                    // if we are here this is probably due images failure
                    LOG.error(ioe.getMessage(), ioe);
                    listener.notifyError(ioe.getMessage());
                    listener.notifyCompleted(ImportService.BulkImportResult.OK);
                } catch (Throwable trw) {
                    // something very wrong
                    LOG.error(trw.getMessage(), trw);
                    listener.notifyError(trw.getMessage());
                    listener.notifyCompleted(ImportService.BulkImportResult.ERROR);
                }
            }
        };
    }

    private void doImageImport(final BulkImportStatusListener listener, final Set<String> importedFiles, final String fileName) throws IOException {
        final File ycsimg = new File(applicationContext.getResource("WEB-INF").getFile().getAbsolutePath()
                + File.separator + ".." + File.separator + ".." + File.separator + "yes-shop"
                + File.separator + "default" + File.separator + "imagevault");
        bulkImportImagesService.setPathToRepository(ycsimg.getAbsolutePath()); //TODO remove this hardcoded value. also see remoteImageServiceimpl
        bulkImportImagesService.doImport(listener, importedFiles, fileName, this.pathToImportFolder);
    }

    private void doDataImport(final BulkImportStatusListener listener, final Set<String> importedFiles, final String fileName) throws IOException {
        for (String descriptor : importDescriptors) {
            Resource res = applicationContext.getResource("WEB-INF/" + pathToImportDescriptors + "/" + descriptor);
            bulkImportService.setPathToImportDescriptor(res.getFile().getAbsolutePath());
            bulkImportService.doImport(listener, importedFiles, fileName, this.pathToImportFolder);
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
