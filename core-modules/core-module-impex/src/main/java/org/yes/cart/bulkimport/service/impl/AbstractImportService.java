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

import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.yes.cart.bulkcommon.service.ImportService;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.service.ImportServiceSingleFile;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.federation.FederationFacade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Set;

/**
 * Abstract import service to hold common methods.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/12/11
 * Time: 10:43 AM
 */
public abstract class AbstractImportService<ID extends ImportDescriptor> implements ImportService, ImportServiceSingleFile<ID> {

    protected final FederationFacade federationFacade;

    public AbstractImportService(final FederationFacade federationFacade) {
        this.federationFacade = federationFacade;
    }


    /**
     * Perform bulk import.
     * Service has s set of import descriptors, eac of them may perform the import
     * on files. Files selected by regular expression . If <code>fileName</code>
     * not empty, than only one may be imported instead of set, that satisfy
     * regular expressions.
     *
     * @param context job context
     * @return result of the import
     */
    @Override
    public BulkImportResult doImport(final JobContext context) {

        final JobStatusListener statusListener = context.getListener();
        final Set<String> importedFiles = context.getAttribute(JobContextKeys.IMPORT_FILE_SET);
        final String fileName = context.getAttribute(JobContextKeys.IMPORT_FILE);
        final ID importDescriptor = context.getAttribute(JobContextKeys.IMPORT_DESCRIPTOR);
        final String importDescriptorName = context.getAttribute(JobContextKeys.IMPORT_DESCRIPTOR_NAME);

        try {

            final File[] filesToImport = ImportFileUtils.getFilesToImport(importDescriptor, fileName);
            if (filesToImport == null) {
                final String msgWarn = MessageFormat.format(
                        "no files with mask {0} to import",
                        importDescriptor.getImportFileDescriptor().getFileNameMask());
                statusListener.notifyWarning(msgWarn);
            } else {
                final String msgInfo = MessageFormat.format(
                        "Import descriptor {0} has {1} file(s) with mask {2} to import",
                        importDescriptorName,
                        filesToImport.length,
                        importDescriptor.getImportFileDescriptor().getFileNameMask());
                statusListener.notifyMessage(msgInfo);
                return doImport(statusListener, filesToImport, importDescriptorName, importDescriptor, importedFiles);
            }
        } catch (Exception e) {

            return BulkImportResult.ERROR;

        }
        return BulkImportResult.OK;
    }

    /**
     * Perform import for each file.
     *
     * @param statusListener       error report
     * @param filesToImport        array of files to import
     * @param importDescriptorName file name of the descriptor
     * @param importDescriptor     import descriptor.
     * @param importedFiles        imported files.
     */
    protected BulkImportResult doImport(final JobStatusListener statusListener,
                                        final File[] filesToImport,
                                        final String importDescriptorName,
                                        final ID importDescriptor,
                                        final Set<String> importedFiles) throws Exception {
        // Need to add all file to the set for proper clean up after job in case exception occurs
        for (File fileToImport : filesToImport) {
            importedFiles.add(fileToImport.getAbsolutePath());
        }

        final int total = filesToImport.length;
        int count = 1;
        for (File fileToImport : filesToImport) {
            final String msgInfo = MessageFormat.format("Importing file {0} of {1}: {2}", count++, total, fileToImport.getAbsolutePath());
            statusListener.notifyMessage(msgInfo);

            final BulkImportResult status = doImport(statusListener, fileToImport, importDescriptorName, importDescriptor);
            if (status != BulkImportResult.OK) {
                return status;
            }
        }
        return BulkImportResult.OK;
    }


    protected BulkImportResult doImport(final JobStatusListener statusListener,
                                        final File fileToImport,
                                        final String importDescriptorName,
                                        final ID importDescriptor) throws Exception {

        return self().doSingleFileImport(statusListener, fileToImport, importDescriptorName, importDescriptor);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public BulkImportResult doSingleFileImport(final JobStatusListener statusListener,
                                               final File fileToImport,
                                               final String importDescriptorName,
                                               final ID importDescriptor) throws Exception {

        boolean failed = true;

        try {

            doSingleFileImportInternal(statusListener, fileToImport, importDescriptorName, importDescriptor);
            failed = false;

        } catch (FileNotFoundException e) {
            final String msgErr = MessageFormat.format(
                    "can not find the file : {0} {1}",
                    fileToImport.getAbsolutePath(),
                    e.getMessage());
            statusListener.notifyError(msgErr, e);
        } catch (UnsupportedEncodingException e) {
            final String msgErr = MessageFormat.format(
                    "wrong file encoding in xml descriptor : {0} {1}",
                    importDescriptor.getImportFileDescriptor().getFileEncoding(),
                    e.getMessage());
            statusListener.notifyError(msgErr, e);

        } catch (IOException e) {
            final String msgErr = MessageFormat.format("io exception : {0} {1}",
                    fileToImport.getAbsolutePath(),
                    e.getMessage());
            statusListener.notifyError(msgErr, e);
        } catch (Exception e) {
            final String msgErr = MessageFormat.format("error during processing import file : {0} {1}",
                    fileToImport.getAbsolutePath(),
                    e.getMessage());
            statusListener.notifyError(msgErr, e);
        }

        if (failed) {

            /*
             * Programmatically rollback for any error during import - ALL or NOTHING - single file.
             * But we do not throw exception since this is in a separate thread so not point
             * Need to finish gracefully with error status
             */
            if (!TransactionAspectSupport.currentTransactionStatus().isRollbackOnly()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

            return BulkImportResult.ERROR;

        }

        return BulkImportResult.OK;

    }



    /**
     * Implementation specific processing of a single file wrapper by a transaction
     */
    protected abstract void doSingleFileImportInternal(final JobStatusListener statusListener,
                                                       final File fileToImport,
                                                       final String importDescriptorName,
                                                       final ID importDescriptor) throws Exception;


    /**
     * Verify access of the current user.
     *
     * @param object target object to update
     *
     * @throws AccessDeniedException errors
     */
    protected void validateAccessBeforeUpdate(final Object object, final Class objectType) throws AccessDeniedException {
        if (!federationFacade.isManageable(object, objectType)) {
            throw new AccessDeniedException("access denied");
        }
    }

    /**
     * Verify access of the current user.
     *
     * @param object target object to update
     *
     * @throws AccessDeniedException errors
     */
    protected void validateAccessAfterUpdate(final Object object, final Class objectType) throws AccessDeniedException {
        if (!federationFacade.isManageable(object, objectType)) {
            throw new AccessDeniedException("access denied");
        }
    }



    private ImportServiceSingleFile<ID> self;

    private ImportServiceSingleFile<ID> self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public ImportServiceSingleFile<ID> getSelf() {
        return null;
    }


}
