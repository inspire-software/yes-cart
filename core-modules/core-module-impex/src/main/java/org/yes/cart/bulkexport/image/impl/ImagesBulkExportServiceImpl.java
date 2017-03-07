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

package org.yes.cart.bulkexport.image.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.yes.cart.bulkcommon.service.ExportService;
import org.yes.cart.bulkexport.csv.CsvExportDescriptor;
import org.yes.cart.bulkexport.image.ImageExportDomainObjectStrategy;
import org.yes.cart.bulkimport.service.impl.AbstractExportService;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.federation.FederationFacade;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: denispavlov
 * Date: 27/11/2015
 * Time: 12:52
 */
public class ImagesBulkExportServiceImpl extends AbstractExportService implements ExportService {

    private static final Logger LOG = LoggerFactory.getLogger(ImagesBulkExportServiceImpl.class);

    private final ImageExportDomainObjectStrategy[] strategies;

    /**
     * Construct bulk import service.
     *
     * @param federationFacade federation facade
     * @param strategies   domain strategies to associate image with domain model
     */
    public ImagesBulkExportServiceImpl(final FederationFacade federationFacade,
                                       final ImageExportDomainObjectStrategy[] strategies) {
        super(federationFacade);
        this.strategies = strategies;
    }

    /**
     * {@inheritDoc}
     */
    public BulkExportResult doExport(final JobContext context) {

        final JobStatusListener statusListener = context.getListener();

        final CsvExportDescriptor imageExportDescriptor = context.getAttribute(JobContextKeys.EXPORT_DESCRIPTOR);
        final String imageExportDescriptorName = context.getAttribute(JobContextKeys.EXPORT_DESCRIPTOR_NAME);
        final String imageExportOverrideFile = context.getAttribute(JobContextKeys.EXPORT_FILE);


        try {

            String fileToExport = imageExportDescriptor.getExportFileDescriptor().getFileName();
            if (StringUtils.isNotBlank(imageExportOverrideFile)) {
                fileToExport = imageExportOverrideFile;
            } else {
                if (fileToExport.contains(TIMESTAMP_PLACEHOLDER)) {
                    final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    fileToExport = fileToExport.replace(TIMESTAMP_PLACEHOLDER, format.format(new Date()));
                    if (new File(fileToExport).exists()) {
                        final String msgErr = MessageFormat.format(
                                "export file already exists: {0}",
                                fileToExport);
                        statusListener.notifyError(msgErr);
                        return BulkExportResult.ERROR;
                    }
                }
            }

            final String msgInfo = MessageFormat.format(
                    "Export descriptor {0} specifies file {1} to export",
                    imageExportDescriptorName,
                    fileToExport);
            statusListener.notifyMessage(msgInfo);
            if (imageExportDescriptor.getSelectSql() == null) {
                final String msgErr = "export can not be started, because select-sql is empty";
                statusListener.notifyError(msgErr);
                return ExportService.BulkExportResult.ERROR;
            }
            doExport(statusListener, imageExportDescriptorName, imageExportDescriptor, fileToExport);

        } catch (Exception e) {

            /**
             * Programmatically rollback for any error during import - ALL or NOTHING.
             * But we do not throw exception since this is in a separate thread so not point
             * Need to finish gracefully with error status
             */
            if (!TransactionAspectSupport.currentTransactionStatus().isRollbackOnly()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

            final String msgError = MessageFormat.format(
                    "unexpected error {0}",
                    e.getMessage());
            statusListener.notifyError(msgError, e);
            return ExportService.BulkExportResult.ERROR;
        }
        return ExportService.BulkExportResult.OK;
    }

    private static final String TIMESTAMP_PLACEHOLDER = "{timestamp}";


    /**
     * Perform export for single file.
     * @param statusListener      error report
     * @param imageExportDescriptorName file name of the descriptor
     * @param imageExportDescriptor export descriptor.
     * @param fileToExport        file to export
     */
    void doExport(final JobStatusListener statusListener,
                  final String imageExportDescriptorName,
                  final CsvExportDescriptor imageExportDescriptor, final String fileToExport) throws Exception {

        final String msgInfoImp = MessageFormat.format("export file : {0}", fileToExport);
        statusListener.notifyMessage(msgInfoImp);
        LOG.info(msgInfoImp);

        final String select = imageExportDescriptor.getSelectSql();

        String info = MessageFormat.format(
                "start images export with {0} path using {1}",
                imageExportDescriptor.getSelectSql(),
                imageExportDescriptorName);
        statusListener.notifyMessage(info);
        LOG.info(info);

        for (final ImageExportDomainObjectStrategy domainStrategy : strategies) {
            if (domainStrategy.supports(select)) {
                domainStrategy.doImageExport(
                        statusListener,
                        fileToExport
                );
            }
        }

    }
}
