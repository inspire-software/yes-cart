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

package org.yes.cart.bulkexport.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.yes.cart.bulkcommon.service.ExportService;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.utils.DateUtils;

import java.io.File;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 14:09
 */
public abstract class AbstractExportService<ED extends ExportDescriptor> implements ExportService {

    protected final FederationFacade federationFacade;

    public AbstractExportService(final FederationFacade federationFacade) {
        this.federationFacade = federationFacade;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public BulkExportResult doExport(final JobContext context) {

        final JobStatusListener statusListener = context.getListener();

        final ED exportDescriptor = context.getAttribute(JobContextKeys.EXPORT_DESCRIPTOR);
        final String exportDescriptorName = context.getAttribute(JobContextKeys.EXPORT_DESCRIPTOR_NAME);
        final String exportRoot = context.getAttribute(JobContextKeys.EXPORT_DIRECTORY_ROOT);
        final String exportOverrideFile = context.getAttribute(JobContextKeys.EXPORT_FILE);

        try {

            String fileToExport = exportDescriptor.getExportFileDescriptor().getFileName();
            if (StringUtils.isNotBlank(exportOverrideFile)) {
                fileToExport = exportOverrideFile;
            } else {
                if (fileToExport.contains(ROOT_PLACEHOLDER)) {
                    fileToExport = fileToExport.replace(ROOT_PLACEHOLDER, exportRoot);
                }
                if (fileToExport.contains(TIMESTAMP_PLACEHOLDER)) {
                    fileToExport = fileToExport.replace(TIMESTAMP_PLACEHOLDER, DateUtils.exportFileTimestamp());
                    if (new File(fileToExport).exists()) {
                        // Only do this for timestamped files, otherwise we assume that files are re-writable
                        statusListener.notifyError("export file already exists: {}", fileToExport);
                        return BulkExportResult.ERROR;
                    }
                }
            }

            statusListener.notifyMessage("Export descriptor {} specifies file {} to export", exportDescriptorName, fileToExport);
            if (exportDescriptor.getSelectCmd() == null) {
                final String msgErr = "export can not be started, because 'select' is empty";
                statusListener.notifyError(msgErr);
                return BulkExportResult.ERROR;
            }
            doExport(statusListener, exportDescriptorName, exportDescriptor, fileToExport);

        } catch (Exception e) {

            /*
             * Programmatically rollback for any error during import - ALL or NOTHING.
             * But we do not throw exception since this is in a separate thread so not point
             * Need to finish gracefully with error status
             */
            if (!TransactionAspectSupport.currentTransactionStatus().isRollbackOnly()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

            statusListener.notifyError("unexpected error {}", e, e.getMessage());
            return BulkExportResult.ERROR;
        }
        return BulkExportResult.OK;
    }

    private static final String ROOT_PLACEHOLDER = "{root}";
    private static final String TIMESTAMP_PLACEHOLDER = "{timestamp}";

    /**
     * Perform export for single file.
     *
     * @param statusListener       error report
     * @param exportDescriptorName file name of the descriptor
     * @param exportDescriptor     export descriptor.
     * @param fileToExport         file to export
     */
    protected abstract void doExport(final JobStatusListener statusListener,
                                     final String exportDescriptorName,
                                     final ED exportDescriptor,
                                     final String fileToExport) throws Exception;


    /**
     * Verify access of the current user.
     *
     * @param object target object to update
     *
     * @throws AccessDeniedException errors
     */
    protected void validateAccessBeforeExport(final Object object, final Class objectType) throws AccessDeniedException {
        if (!federationFacade.isManageable(object, objectType)) {
            throw new AccessDeniedException("access denied");
        }
    }

}
