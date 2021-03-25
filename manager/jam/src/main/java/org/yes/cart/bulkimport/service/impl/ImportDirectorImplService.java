/*
 * Copyright 2009 Inspire-Software.com
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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.bulkcommon.service.DataDescriptorResolver;
import org.yes.cart.bulkcommon.service.ImportDirectorService;
import org.yes.cart.bulkcommon.service.ImportService;
import org.yes.cart.bulkcommon.service.model.JobContextDecoratorImpl;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.RspMessage;
import org.yes.cart.cluster.node.impl.ContextRspMessageImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.entity.DataGroup;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.async.AsyncContextFactory;
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
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.impl.ZipUtils;
import org.yes.cart.utils.log.Markers;
import org.yes.cart.utils.spring.LinkedHashMapBean;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Import Director class to perform import via {@link ImportService}
 * collect imported files and move it to archive folder.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ImportDirectorImplService extends SingletonJobRunner implements ImportDirectorService, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ImportDirectorImplService.class);

    private String pathToArchiveDirectory;

    private String pathToImportDirectory;

    private final NodeService nodeService;

    private final AsyncContextFactory asyncContextFactory;

    private final SystemService systemService;

    private final ZipUtils zipUtils;

    private final DataDescriptorResolver<ImportDescriptor> dataDescriptorResolver;

    private final FederationFacade federationFacade;

    private final Map<String, String> bulkImportServiceBeanMap;

    private ApplicationContext applicationContext;


    /**
     * Construct the import director
     *
     * @param pathToArchiveDirectory   path to archive folder.
     * @param pathToImportDirectory    path to use.
     * @param dataDescriptorResolver   descriptor resolver
     * @param executor                 async executor
     * @param nodeService              node service
     * @param asyncContextFactory      async context factory
     * @param systemService            system service
     * @param zipUtils                 zip algorithm
     * @param federationFacade         data federation service
     * @param bulkImportServiceBeanMap map of import services
     */
    public ImportDirectorImplService(final String pathToArchiveDirectory,
                                     final String pathToImportDirectory,
                                     final DataDescriptorResolver<ImportDescriptor> dataDescriptorResolver,
                                     final TaskExecutor executor,
                                     final NodeService nodeService,
                                     final AsyncContextFactory asyncContextFactory,
                                     final SystemService systemService,
                                     final ZipUtils zipUtils,
                                     final FederationFacade federationFacade,
                                     final LinkedHashMapBean<String, String> bulkImportServiceBeanMap) {
        super(executor);
        this.pathToArchiveDirectory = pathToArchiveDirectory;
        this.pathToImportDirectory = pathToImportDirectory;
        this.dataDescriptorResolver = dataDescriptorResolver;
        this.nodeService = nodeService;
        this.asyncContextFactory = asyncContextFactory;
        this.systemService = systemService;
        this.zipUtils = zipUtils;
        this.federationFacade = federationFacade;
        this.bulkImportServiceBeanMap = bulkImportServiceBeanMap;
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
    @Override
    public JobStatus getImportStatus(final String token) {
        return getStatus(token);
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus doImport(final String descriptorGroup, final String fileName, final boolean async) {

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
            // This is manual access via admin
            return this.asyncContextFactory.getInstance(
                    Collections.singletonMap(AsyncContext.NO_BROADCAST, AsyncContext.NO_BROADCAST)
            );
        } catch (IllegalStateException exp) {
            // This is auto access with thread local
            return new BulkJobAutoContextImpl(
                    Collections.singletonMap(AsyncContext.NO_BROADCAST, AsyncContext.NO_BROADCAST)
            );
        }
    }

    private void clearCacheAfterImport(final JobContext context) {

        try {
            final List<Node> cluster = nodeService.getSfNodes();
            final List<String> targets = new ArrayList<>();
            for (final Node node : cluster) {
                targets.add(node.getId());
            }

            final JobContext contextWithBroadcast = new JobContextDecoratorImpl(
                    context,
                    Collections.singletonMap(AsyncContext.NO_BROADCAST, "false")
            );

            final RspMessage message = new ContextRspMessageImpl(
                    nodeService.getCurrentNodeId(),
                    targets,
                    "CacheDirector.evictAllCache",
                    false,
                    contextWithBroadcast
            );

            nodeService.broadcast(message);

            context.getListener().notifyInfo("Performing full cache evict");
        } catch (Exception exp) {
            context.getListener().notifyError("Unable to perform full clear cache: " + exp.getMessage(), exp);
        }

    }

    @Override
    protected Runnable createJobRunnable(final JobContext ctx) {
        return new Runnable() {

            private final JobContext context = ctx;
            private final JobStatusListener listener = ctx.getListener();

            @Override
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

                    clearCacheAfterImport(context);

                    listener.notifyInfo("Import Job completed");
                    listener.notifyCompleted();

                } catch (IOException ioe) {
                    // if we are here this is probably due images failure
                    listener.notifyError(ioe.getMessage(), ioe);
                    listener.notifyInfo("Import Job completed but there was an IO error: " + ioe.getMessage());
                    listener.notifyCompleted();
                } catch (Exception exp) {
                    // something very wrong
                    listener.notifyError(exp.getMessage(), exp);
                    listener.notifyInfo("Import Job was terminated. Error: " + exp.getMessage());
                    listener.notifyCompleted();
                } catch (Throwable trw) {
                    // something very, very wrong
                    LOG.error(trw.getMessage(), trw);
                    listener.notifyError(trw.getMessage());
                    listener.notifyInfo("Import Job was terminated. Error: " + trw.getMessage());
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


            final String entityType = descriptorObject.getEntityType();
            final String impExService = descriptorObject.getContext().getImpExService();

            final String specific = impExService + '-' + entityType;

            final String specificServiceBean = this.bulkImportServiceBeanMap.get(specific);
            if (specificServiceBean != null) {
                this.applicationContext.getBean(specificServiceBean, ImportService.class).doImport(dataJob);
            } else {
                final String generalServiceBean = this.bulkImportServiceBeanMap.get(impExService);
                if (generalServiceBean != null) {
                    this.applicationContext.getBean(generalServiceBean, ImportService.class).doImport(dataJob);
                } else {
                    LOG.error(Markers.alert(), "Unknown import service {} for entity {}",
                            descriptorObject.getContext().getImpExService(),
                            descriptorObject.getEntityType());
                }
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
            final String fullPathToArchiveFolder = pathToArchiveDirectory;
            final LocalDateTime now = LocalDateTime.now();
            File dir = new File(fullPathToArchiveFolder + File.separator + DateUtils.format(now, "yyyy-MM-dd") + File.separator + DateUtils.impexFileTimestamp() + File.separator);
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
                    LOG.error("Can not move file " + importFileName + " to directory " + dir.getAbsolutePath(), e);
                }
            }

            if (tempRoot != null && !pathToImportDirectory.equals(tempRoot)) {
                final File tempRootFile = new File(tempRoot);
                if (tempRootFile.exists()) {
                    tempRootFile.delete();
                }
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
    @Override
    public List<Map<String, String>> getImportGroups(final String language) {

        final List<DataGroup> dataGroups = dataDescriptorResolver.getGroups();

        final Set<Map<String, String>> out = new TreeSet<>((o1, o2) -> {
            int comp = o1.get("label").compareToIgnoreCase(o2.get("label"));
            if (comp == 0) {
                comp = o1.get("name").compareToIgnoreCase(o2.get("name"));
            }
            return comp;
        });
        for (final DataGroup dataGroup : dataGroups) {
            if (StringUtils.isBlank(dataGroup.getQualifier()) ||
                    federationFacade.isManageable(dataGroup.getQualifier(), ShopDTO.class)) {
                final Map<String, String> grp = new HashMap<>();
                grp.put("name", dataGroup.getName());
                final I18NModel model = new FailoverStringI18NModel(dataGroup.getDisplayName(), dataGroup.getName());
                grp.put("label", model.getValue(language));
                out.add(grp);
            }
        }

        return new ArrayList<>(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getImportDirectory() {
        return pathToImportDirectory;
    }

    public void setPathToImportDirectory(final String pathToImportDirectory) {
        this.pathToImportDirectory = pathToImportDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getArchiveDirectory() {
        return pathToArchiveDirectory;
    }

    public void setPathToArchiveDirectory(final String pathToArchiveDirectory) {
        this.pathToArchiveDirectory = pathToArchiveDirectory;
    }

    /**
     * Spring IoC.
     *
     * @param applicationContext application context.
     *
     * @throws BeansException exception
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
