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

package org.yes.cart.bulkexport.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.bulkcommon.service.DataDescriptorResolver;
import org.yes.cart.bulkcommon.service.ExportDirectorService;
import org.yes.cart.bulkcommon.service.ExportService;
import org.yes.cart.bulkcommon.service.model.JobContextDecoratorImpl;
import org.yes.cart.bulkexport.model.ExportDescriptor;
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
import org.yes.cart.web.service.ws.client.AsyncContextFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Export Director class to perform import via {@link ExportService}
 * collect imported files and move it to archive folder.
 * <p/> * User: denispavlov
 * Date: 27/11/2015
 * Time: 11:56
 */
public class ExportDirectorImplService extends SingletonJobRunner implements ExportDirectorService {

    private static final Logger LOG = LoggerFactory.getLogger(ExportDirectorImplService.class);

    private final String pathToExportDirectory;

    private final NodeService nodeService;

    private final AsyncContextFactory asyncContextFactory;

    private final SystemService systemService;

    private final DataDescriptorResolver<ExportDescriptor> dataDescriptorResolver;

    private final FederationFacade federationFacade;

    /**
     * Construct export director
     *  @param pathToExportDirectory   path to use.
     * @param dataDescriptorResolver  descriptor resolver
     * @param executor                async executor
     * @param nodeService             node service
     * @param asyncContextFactory     async context factory
     * @param systemService           system service
     * @param federationFacade        data federation service
     */
    public ExportDirectorImplService(final String pathToExportDirectory,
                                     final DataDescriptorResolver<ExportDescriptor> dataDescriptorResolver,
                                     final TaskExecutor executor,
                                     final NodeService nodeService,
                                     final AsyncContextFactory asyncContextFactory,
                                     final SystemService systemService,
                                     final FederationFacade federationFacade) {
        super(executor);
        this.pathToExportDirectory = pathToExportDirectory;
        this.nodeService = nodeService;
        this.asyncContextFactory = asyncContextFactory;
        this.systemService = systemService;
        this.dataDescriptorResolver = dataDescriptorResolver;
        this.federationFacade = federationFacade;
    }



    /**
     * Perform bulk export.
     *
     * @param context job context
     */
    public void doExportInternal(final JobContext context) throws IOException {
        doDataExport(context);
    }


    /** {@inheritDoc} */
    public JobStatus getExportStatus(final String token) {
        return getStatus(token);
    }

    /** {@inheritDoc} */
    public String doExport(final String descriptorGroup, final String fileName, final boolean async) {

        final AsyncContext ctx = getAsyncContext();

        final String imgVault = systemService.getImageRepositoryDirectory();

        // Max char of report to UI since it will get huge and simply will crash the UI, not to mention traffic cost.
        final int logSize = NumberUtils.toInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.IMPORT_JOB_LOG_SIZE), 100);
        // Timeout - just in case runnable crashes and we need to unlock through timeout.
        final int timeout = NumberUtils.toInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.IMPORT_JOB_TIMEOUT_MS), 100);

        final String rootPath = resolveExportDirectory();
        final String absFile = resolveExportFile((String) ctx.getAttribute(AsyncContext.USERNAME), fileName);

        return doJob(new JobContextImpl(async, new JobStatusListenerWithLoggerImpl(new JobStatusListenerImpl(logSize, timeout), LOG),
                new HashMap<String, Object>() {{
                    put(JobContextKeys.EXPORT_DESCRIPTOR_GROUP, descriptorGroup);
                    put(JobContextKeys.EXPORT_FILE, absFile);
                    put(JobContextKeys.IMAGE_VAULT_PATH, imgVault);
                    put(JobContextKeys.EXPORT_DIRECTORY_ROOT, rootPath);
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

                    doExportInternal(context);

                    listener.notifyMessage("Export Job completed");
                    listener.notifyCompleted();
                } catch (IOException ioe) {
                    // if we are here this is probably due images failure
                    listener.notifyError(ioe.getMessage(), ioe);
                    listener.notifyMessage("Export Job completed but there was an IO error: " + ioe.getMessage());
                    listener.notifyCompleted();
                } catch (Exception exp) {
                    // something very wrong
                    listener.notifyError(exp.getMessage(), exp);
                    listener.notifyMessage("Export Job was terminated. Error: " + exp.getMessage());
                    listener.notifyCompleted();
                } catch (Throwable trw) {
                    // something very, very wrong
                    LOG.error(trw.getMessage(), trw);
                    listener.notifyError(trw.getMessage());
                    listener.notifyMessage("Export Job was terminated. Error: " + trw.getMessage());
                    listener.notifyCompleted();
                } finally {
                    ThreadLocalAsyncContextUtils.clear();
                }
            }
        };
    }


    private void doDataExport(final JobContext context) throws IOException {

        final Map<String, ExportDescriptor> descriptorObjects = dataDescriptorResolver.getByGroup((String) context.getAttribute(JobContextKeys.EXPORT_DESCRIPTOR_GROUP));

        // remove export file override if we have multiple exports
        final String exportFile = descriptorObjects.size() != 1 ? null : (String) context.getAttribute(JobContextKeys.EXPORT_FILE);


        for (final Map.Entry<String, ExportDescriptor> descriptorObjectEntry : descriptorObjects.entrySet()) {

            final String descriptor = descriptorObjectEntry.getKey();
            final ExportDescriptor descriptorObject = descriptorObjectEntry.getValue();

            final JobContext dataJob = new JobContextDecoratorImpl(context, new HashMap<String, Object>() {{
                put(JobContextKeys.EXPORT_DESCRIPTOR, descriptorObject);
                put(JobContextKeys.EXPORT_DESCRIPTOR_NAME, descriptor);
                put(JobContextKeys.EXPORT_FILE, exportFile);
            }});

            if ("IMAGE".equals(descriptorObject.getEntityType())) {
                getNewBulkExportImagesService().doExport(dataJob);
            } else if ("org.yes.cart.payment.persistence.entity.CustomerOrderPayment".equals(descriptorObject.getEntityType())) {
                getNewBulkPaymentExportService().doExport(dataJob);
            } else {
                getNewBulkExportService().doExport(dataJob);
            }
        }
    }


    private String resolveExportDirectory() {
        return pathToExportDirectory;
    }

    private String resolveExportFile(final String username, final String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            if (fileName.startsWith("/") || fileName.startsWith("file://")) {
                return fileName;
            }

            final File home;
            if (pathToExportDirectory.endsWith(File.separator)) {
                home = new File(pathToExportDirectory + username);
            } else {
                home = new File(pathToExportDirectory + File.separator + username);
            }
            if (!home.exists()) {
                home.mkdirs();
            }
            return new File(home, fileName).getAbsolutePath();

        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Map<String, String>> getExportGroups(final String language) {

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
    public String getExportDirectory() {
        return pathToExportDirectory;
    }


    /**
     * @return IoC prototype instance
     */
    public ExportService getNewBulkExportService() {
        return null;
    }

    /**
     * @return IoC prototype instance
     */
    public ExportService getNewBulkPaymentExportService() {
        return null;
    }

    /**
     * @return IoC prototype instance
     */
    public ExportService getNewBulkExportImagesService() {
        return null;
    }


}
