/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.bulkcommon.service.DataDescriptorResolver;
import org.yes.cart.bulkcommon.service.ImportDirectorService;
import org.yes.cart.bulkcommon.service.ImportService;
import org.yes.cart.bulkcommon.service.model.JobContextDecoratorImpl;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.entity.DataGroup;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.SingletonJobRunner;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.model.impl.JobContextImpl;
import org.yes.cart.service.async.utils.ThreadLocalAsyncContextUtils;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.utils.impl.ZipUtils;
import org.yes.cart.web.service.ws.client.AsyncContextFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Import Director class to perform import via {@link ImportService}
 * collect imported files and move it to archive folder.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ImportDirectorImplService extends SingletonJobRunner implements ImportDirectorService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportDirectorImplService.class);

    private final String pathToArchiveDirectory;

    private final String pathToImportDirectory;

    private final NodeService nodeService;

    private final AsyncContextFactory asyncContextFactory;

    private final SystemService systemService;

    private final ZipUtils zipUtils;

    private final DataDescriptorResolver<ImportDescriptor> dataDescriptorResolver;

    private final FederationFacade federationFacade;


    /**
     * Construct the import director
     *  @param pathToArchiveDirectory  path to archive folder.
     * @param pathToImportDirectory   path to use.
     * @param dataDescriptorResolver  descriptor resolver
     * @param executor                async executor
     * @param nodeService             node service
     * @param asyncContextFactory     async context factory
     * @param systemService           system service
     * @param zipUtils                zip algorithm
     * @param federationFacade        data federation service
     */
    public ImportDirectorImplService(final String pathToArchiveDirectory,
                                     final String pathToImportDirectory,
                                     final DataDescriptorResolver<ImportDescriptor> dataDescriptorResolver,
                                     final TaskExecutor executor,
                                     final NodeService nodeService,
                                     final AsyncContextFactory asyncContextFactory,
                                     final SystemService systemService,
                                     final ZipUtils zipUtils,
                                     final FederationFacade federationFacade) {
        super(executor);
        this.pathToArchiveDirectory = pathToArchiveDirectory;
        this.pathToImportDirectory = pathToImportDirectory;
        this.dataDescriptorResolver = dataDescriptorResolver;
        this.nodeService = nodeService;
        this.asyncContextFactory = asyncContextFactory;
        this.systemService = systemService;
        this.zipUtils = zipUtils;
        this.federationFacade = federationFacade;
    }


    /**
     * Perform bulk import.
     *
     * @param context job context
     */
    public void doImportInternal(final JobContext context) throws IOException {
        doDataImport(context);
    }

    /** {@inheritDoc} */
    public JobStatus getImportStatus(final String token) {
        return getStatus(token);
    }

    /** {@inheritDoc} */
    public String doImport(final String descriptorGroup, final String fileName, final boolean async) {

        final AsyncContext ctx = getAsyncContext();

        final String imgVault = systemService.getImageRepositoryDirectory();

        // Max char of report to UI since it will get huge and simply will crash the UI, not to mention traffic cost.
        final int logSize = NumberUtils.toInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.IMPORT_JOB_LOG_SIZE), 100);
        // Timeout - just in case runnable crashes and we need to unlock through timeout.
        final int timeout = NumberUtils.toInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.IMPORT_JOB_TIMEOUT_MS), 100);

        final String rootPath = resolveImportDirectory(fileName);

        return doJob(new JobContextImpl(async, new JobStatusListenerWithLoggerImpl(new JobStatusListenerImpl(logSize, timeout), LOG),
                new HashMap<String, Object>() {{
                    put(JobContextKeys.IMPORT_DESCRIPTOR_GROUP, descriptorGroup);
                    put(JobContextKeys.IMPORT_FILE, fileName);
                    put(JobContextKeys.IMPORT_FILE_SET, new HashSet<String>());
                    put(JobContextKeys.IMAGE_VAULT_PATH, imgVault);
                    put(JobContextKeys.IMPORT_DIRECTORY_ROOT, rootPath);
                    putAll(ctx.getAttributes());
                }}));
    }

    private AsyncContext getAsyncContext() {
        try {
            // This is manual access via YUM
            return this.asyncContextFactory.getInstance(new HashMap<String, Object>());
        } catch (IllegalStateException exp) {
            // This is auto access with thread local
            return new BulkJobAutoContextImpl(new HashMap<String, Object>());
        }
    }

    protected Runnable createJobRunnable(final JobContext ctx) {
        return new Runnable() {

            private final JobContext context = ctx;
            private final JobStatusListener listener = ctx.getListener();

            public void run() {
                try {
                    ThreadLocalAsyncContextUtils.init(context);

                    final String file = context.getAttribute(JobContextKeys.IMPORT_FILE);
                    final Set<String> importedFiles = context.getAttribute(JobContextKeys.IMPORT_FILE_SET);

                    if (file.matches("(.*)\\.zip(.*)")) {
                        importedFiles.add(file);
                        zipUtils.unzipArchive(file, resolveImportDirectory(file));
                        final JobContext zipJob = new JobContextDecoratorImpl(context, new HashMap<String, Object>() {{
                            put(JobContextKeys.IMPORT_FILE, null); // remove individual file name
                        }});
                        doImportInternal(zipJob);
                    } else {
                        doImportInternal(context); //single file import
                    }
                    listener.notifyMessage("Import Job completed");
                    listener.notifyCompleted();
                } catch (IOException ioe) {
                    // if we are here this is probably due images failure
                    listener.notifyError(ioe.getMessage(), ioe);
                    listener.notifyMessage("Import Job completed but there was an IO error: " + ioe.getMessage());
                    listener.notifyCompleted();
                } catch (Exception exp) {
                    // something very wrong
                    listener.notifyError(exp.getMessage(), exp);
                    listener.notifyMessage("Import Job was terminated. Error: " + exp.getMessage());
                    listener.notifyCompleted();
                } catch (Throwable trw) {
                    // something very, very wrong
                    LOG.error(trw.getMessage(), trw);
                    listener.notifyError(trw.getMessage());
                    listener.notifyMessage("Import Job was terminated. Error: " + trw.getMessage());
                    listener.notifyCompleted();
                } finally {
                    try {
                        moveImportFilesToArchive((Set<String>) context.getAttribute(JobContextKeys.IMPORT_FILE_SET));
                    } catch (Exception exp) {
                        LOG.error("Exception occurred while archiving import files." + exp.getMessage(), exp);
                    }
                    ThreadLocalAsyncContextUtils.clear();
                }
            }
        };
    }

    private void doDataImport(final JobContext context) throws IOException {

        final Map<String, ImportDescriptor> descriptorObjects = dataDescriptorResolver.getByGroup((String) context.getAttribute(JobContextKeys.IMPORT_DESCRIPTOR_GROUP));

        for (final Map.Entry<String, ImportDescriptor> descriptorObjectEntry : descriptorObjects.entrySet()) {

            final String descriptor = descriptorObjectEntry.getKey();
            final ImportDescriptor descriptorObject = descriptorObjectEntry.getValue();

            final String pathToImportRootDirectory = context.getAttribute(JobContextKeys.IMPORT_DIRECTORY_ROOT);
            if (StringUtils.isNotBlank(pathToImportRootDirectory)) {
                descriptorObject.setImportDirectory(pathToImportRootDirectory);
            }

            final JobContext dataJob = new JobContextDecoratorImpl(context, new HashMap<String, Object>() {{
                put(JobContextKeys.IMPORT_DESCRIPTOR, descriptorObject);
                put(JobContextKeys.IMPORT_DESCRIPTOR_NAME, descriptor);
            }});

            if ("IMAGE".equals(descriptorObject.getEntityType())) {
                getNewBulkImportImagesService().doImport(dataJob);
            } else {
                getNewBulkImportService().doImport(dataJob);
            }
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
            final String fullPathToArchiveFolder = pathToArchiveDirectory;
            File dir = new File(fullPathToArchiveFolder + File.separator + dateFormat.format(new Date()) + File.separator);
            dir.mkdirs();

            String tempRoot = null;
            for (String importFileName : importedFiles) {
                try {
                    File importFile = new File(importFileName);
                    FileUtils.copyFileToDirectory(importFile, dir, true);
                    if (tempRoot == null) {
                        tempRoot = resolveImportDirectory(importFile.getAbsolutePath());
                    }
                    importFile.delete();
                } catch (IOException e) {
                    LOG.error(
                            MessageFormat.format("Cant move file {0} to folder {1}", importFileName, dir.getAbsolutePath()),
                            e
                    );
                }
            }

            if (!pathToImportDirectory.equals(tempRoot)) {
                new File(tempRoot).delete();
            }
        }
    }

    private String resolveImportDirectory(String fileName) {
        if (fileName.startsWith(pathToImportDirectory)) {
            return new File(fileName).getParentFile().getAbsolutePath();
        }
        return pathToImportDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public List<Map<String, String>> getImportGroups(final String language) {

        final List<DataGroup> dataGroups = dataDescriptorResolver.getGroups();

        final Set<Map<String, String>> out = new TreeSet<Map<String, String>>(new Comparator<Map<String, String>>() {
            @Override
            public int compare(final Map<String, String> o1, final Map<String, String> o2) {
                int comp = o1.get("label").compareToIgnoreCase(o2.get("label"));
                if (comp == 0) {
                    comp = o1.get("name").compareToIgnoreCase(o2.get("name"));
                }
                return comp;
            }
        });
        for (final DataGroup dataGroup : dataGroups) {
            if (StringUtils.isBlank(dataGroup.getQualifier()) ||
                    federationFacade.isManageable(dataGroup.getQualifier(), ShopDTO.class)) {
                final Map<String, String> grp = new HashMap<String, String>();
                grp.put("name", dataGroup.getName());
                final I18NModel model = new FailoverStringI18NModel(dataGroup.getDisplayName(), dataGroup.getName());
                grp.put("label", model.getValue(language));
                out.add(grp);
            }
        }

        return new ArrayList<Map<String, String>>(out);
    }

    /**
     * {@inheritDoc}
     */
    public String getImportDirectory() {
        return pathToImportDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public String getArchiveDirectory() {
        return pathToArchiveDirectory;
    }

    /**
     * @return IoC prototype instance
     */
    public ImportService getNewBulkImportService() {
        return null;
    }

    /**
     * @return IoC prototype instance
     */
    public ImportService getNewBulkImportImagesService() {
        return null;
    }
}
