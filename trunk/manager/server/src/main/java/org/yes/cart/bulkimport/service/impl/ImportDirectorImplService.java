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
import org.yes.cart.bulkimport.service.BulkImportImagesService;
import org.yes.cart.bulkimport.service.BulkImportService;
import org.yes.cart.bulkimport.service.ImportDirectorService;
import org.yes.cart.bulkimport.service.model.JobContextDecoratorImpl;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.remote.service.RemoteBackdoorService;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.SingletonJobRunner;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.model.impl.JobContextImpl;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.utils.impl.ZipUtils;
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Import Director class to perform import via {@link BulkImportService}
 * collect imported files and move it to archive folder.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ImportDirectorImplService extends SingletonJobRunner implements ImportDirectorService, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ImportDirectorImplService.class);

    private final Map<String, List<String>> importDescriptors;

    private final String pathToImportDescriptors;

    private final String pathToArchiveFolder;

    private final String pathToImportFolder;

    private final ProductService productService;

    private ApplicationContext applicationContext;

    private final RemoteBackdoorService remoteBackdoorService;

    private final SystemService systemService;

    /**
     * Construct the import director
     *
     * @param importDescriptors       import descriptors
     * @param pathToArchiveFolder     path to archive folder.
     * @param pathToImportDescriptors path to use.
     * @param pathToImportFolder      path to use.
     * @param executor                async executor
     * @param productService          product service
     * @param remoteBackdoorService   backdoor service
     * @param systemService           system service
     */
    public ImportDirectorImplService(
            final Map<String, List<String>> importDescriptors,
            final String pathToArchiveFolder,
            final String pathToImportDescriptors,
            final String pathToImportFolder,
            final ProductService productService,
            final TaskExecutor executor,
            final RemoteBackdoorService remoteBackdoorService,
            final SystemService systemService) {
        super(executor);
        this.pathToImportDescriptors = pathToImportDescriptors;
        this.pathToArchiveFolder = pathToArchiveFolder;
        this.pathToImportFolder = pathToImportFolder;
        this.importDescriptors = importDescriptors;
        this.productService = productService;
        this.remoteBackdoorService = remoteBackdoorService;
        this.systemService = systemService;
    }


    /**
     * Perform bulk import.
     *
     * @param context job context
     */
    public void doImportInternal(final JobContext context) throws IOException {
        doDataImport(context);
        doImageImport(context);
        moveImportFilesToArchive((Set<String>) context.getAttribute(JobContextKeys.IMPORT_FILE_SET));
    }

    /** {@inheritDoc} */
    public JobStatus getImportStatus(final String token) {
        return getStatus(token);
    }

    /** {@inheritDoc} */
    public String doImport(final String descriptorGroup, final boolean async) {
        return doImport(descriptorGroup, null, async);
    }

    /** {@inheritDoc} */
    public String doImport(final String descriptorGroup, final String fileName, final boolean async) {

        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.WEB_SERVICE_URI, systemService.getBackdoorURI());

        final AsyncContext flex = new AsyncFlexContextImpl(param);

        final String imgVault;
        try {
            imgVault = remoteBackdoorService.getImageVaultPath(flex) + File.separator;
        } catch (IOException ioe) {
            LOG.error("Unable to get image vault path", ioe);
            throw new IllegalStateException("Unable to get image vault path");
        }

        /*
         * Max 10K char of report to UI since it will get huge and simply will crash the UI,
         * not to mention traffic cost.
         * Timeout is set to 60sec - just in case runnable crashes and we need to unlock through
         * timeout.
         */
        return doJob(new JobContextImpl(async, new JobStatusListenerImpl(10000, 60000),
                new HashMap<String, Object>() {{
                    put(JobContextKeys.IMPORT_DESCRIPTOR_GROUP, descriptorGroup);
                    put(JobContextKeys.IMPORT_FILE, fileName);
                    put(JobContextKeys.IMPORT_FILE_SET, new HashSet<String>());
                    put(JobContextKeys.IMPORT_DIRECTORY_ROOT, getOsAwarePath(pathToImportFolder));
                    put(JobContextKeys.IMAGE_VAULT_PATH, getOsAwarePath(imgVault));
                    putAll(flex.getAttributes());
                }}));
    }

    protected Runnable createJobRunnable(final JobContext ctx) {
        return new Runnable() {

            private final JobContext context = ctx;
            private final JobStatusListener listener = ctx.getListener();

            public void run() {
                try {

                    final String file = context.getAttribute(JobContextKeys.IMPORT_FILE);
                    final Set<String> importedFiles = context.getAttribute(JobContextKeys.IMPORT_FILE_SET);

                    if (file.matches("(.*)\\.zip(.*)")) {
                        importedFiles.add(file);
                        ZipUtils.unzipArchive(file, pathToImportFolder);
                        final JobContext zipJob = new JobContextDecoratorImpl(context, new HashMap<String, Object>() {{
                            put(JobContextKeys.IMPORT_FILE, null); // remove individual file name
                        }});
                        doImportInternal(zipJob);
                    } else {
                        doImportInternal(context); //single file import
                    }
                    productService.clearEmptyAttributes();
                    listener.notifyMessage("Import Job completed successfully");
                    listener.notifyCompleted(JobStatus.Completion.OK);
                } catch (IOException ioe) {
                    // if we are here this is probably due images failure
                    LOG.error(ioe.getMessage(), ioe);
                    listener.notifyError(ioe.getMessage());
                    listener.notifyMessage("Import Job completed but there was an IO error: " + ioe.getMessage());
                    listener.notifyCompleted(JobStatus.Completion.OK);
                } catch (Throwable trw) {
                    // something very wrong
                    LOG.error(trw.getMessage(), trw);
                    listener.notifyError(trw.getMessage());
                    listener.notifyMessage("Import Job was terminated. Error: " + trw.getMessage());
                    listener.notifyCompleted(JobStatus.Completion.ERROR);
                }
            }
        };
    }

    private void doImageImport(final JobContext context) throws IOException {
        final BulkImportImagesService bulkImportImagesService = getNewBulkImportImagesService();
        bulkImportImagesService.doImport(context);
    }

    private void doDataImport(final JobContext context) throws IOException {
        final List<String> descriptors = importDescriptors.get(context.getAttribute(JobContextKeys.IMPORT_DESCRIPTOR_GROUP));
        if (descriptors == null) {
            return;
        }
        for (final String descriptor : descriptors) {
            final Resource res = applicationContext.getResource("WEB-INF/" + pathToImportDescriptors + "/" + descriptor);
            final BulkImportService bulkImportService = getNewBulkImportService();
            final JobContext dataJob = new JobContextDecoratorImpl(context, new HashMap<String, Object>() {{
                put(JobContextKeys.IMPORT_DESCRIPTOR_PATH, res.getFile().getAbsolutePath());
            }});
            bulkImportService.doImport(dataJob);
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

    private String getOsAwarePath(String path) {
        final String repoPath;
        if (File.separatorChar == '/') {
            repoPath = path.replace('\\', '/');
        } else {
            repoPath = path.replace('/', '\\');
        }

        if (repoPath.endsWith(File.separator)) {
            return repoPath;
        }
        return repoPath + File.separator;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getImportGroups() {
        return new ArrayList<String>(importDescriptors.keySet());
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * @return IoC prototype instance
     */
    public BulkImportService getNewBulkImportService() {
        return null;
    }

    /**
     * @return IoC prototype instance
     */
    public BulkImportImagesService getNewBulkImportImagesService() {
        return null;
    }
}
