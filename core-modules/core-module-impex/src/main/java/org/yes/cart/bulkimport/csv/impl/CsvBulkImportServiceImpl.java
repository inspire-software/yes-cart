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

package org.yes.cart.bulkimport.csv.impl;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.bulkcommon.service.ImportService;
import org.yes.cart.bulkcommon.service.support.LookUpQuery;
import org.yes.cart.bulkcommon.service.support.LookUpQueryParameterStrategy;
import org.yes.cart.bulkimport.csv.CsvFileReader;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.csv.CsvImportTuple;
import org.yes.cart.bulkimport.model.ImportColumn;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.bulkimport.service.impl.AbstractImportService;
import org.yes.cart.bulkimport.service.support.EntityCacheKeyStrategy;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.federation.FederationFacade;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.*;

/**
 * Perform import from csv files. Import based on xml import description, that include
 * import file mask and column description. Csv import support following
 * import types:
 * <p/>
 * simple - when csv row can be present in table without any modification
 * with fk - when some of the column need to use lookup query to find actual value, that base on value in csv
 * example carrier and carries sla, carrier sla need to get Carrier object as foreign key.
 * with sub import - one of the column has a underlaying table values in csv
 * example - shop and shop url, in this case {@link ImportColumn} has a
 * {@link ImportDescriptor}. At this moment rows in cell are split by comma by default.
 */
public class CsvBulkImportServiceImpl extends AbstractImportService implements ImportService, CsvImportServiceSingleFile {

    private GenericDAO<Object, Long> genericDAO;

    private GenericConversionService extendedConversionService;

    private ValueAdapter valueDataAdapter;

    private ValueAdapter valueStringAdapter;
    private LookUpQueryParameterStrategy descriptorInsertLookUpQueryParameterStrategy;
    private LookUpQueryParameterStrategy columnLookUpQueryParameterStrategy;
    private EntityCacheKeyStrategy cacheKey;

    public CsvBulkImportServiceImpl(final FederationFacade federationFacade) {
        super(federationFacade);
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
    public BulkImportResult doImport(final JobContext context) {

        final JobStatusListener statusListener = context.getListener();
        final Set<String> importedFiles = context.getAttribute(JobContextKeys.IMPORT_FILE_SET);
        final String fileName = context.getAttribute(JobContextKeys.IMPORT_FILE);
        final CsvImportDescriptor csvImportDescriptor = context.getAttribute(JobContextKeys.IMPORT_DESCRIPTOR);
        final String csvImportDescriptorName = context.getAttribute(JobContextKeys.IMPORT_DESCRIPTOR_NAME);

        try {

            final File[] filesToImport = getFilesToImport(csvImportDescriptor, fileName);
            if (filesToImport == null) {
                final String msgWarn = MessageFormat.format(
                        "no files with mask {0} to import",
                        csvImportDescriptor.getImportFileDescriptor().getFileNameMask());
                statusListener.notifyWarning(msgWarn);
            } else {
                final String msgInfo = MessageFormat.format(
                        "Import descriptor {0} has {1} file(s) with mask {2} to import",
                        csvImportDescriptorName,
                        filesToImport.length,
                        csvImportDescriptor.getImportFileDescriptor().getFileNameMask());
                statusListener.notifyMessage(msgInfo);
                if (csvImportDescriptor.getSelectSql() == null) {
                    final String msgErr = "import can not be started, because select-sql is empty";
                    statusListener.notifyError(msgErr);
                    return BulkImportResult.ERROR;
                }
                return doImport(statusListener, filesToImport, csvImportDescriptorName, csvImportDescriptor, importedFiles);
            }
        } catch (Exception e) {

            return BulkImportResult.ERROR;

        }
        return BulkImportResult.OK;
    }


    /**
     * Perform import for each file.
     *
     * @param statusListener      error report
     * @param filesToImport       array of files to import
     * @param csvImportDescriptorName file name of the descriptor
     * @param csvImportDescriptor import descriptor.
     * @param importedFiles       imported files.
     */
    BulkImportResult doImport(final JobStatusListener statusListener,
                              final File[] filesToImport,
                              final String csvImportDescriptorName,
                              final CsvImportDescriptor csvImportDescriptor,
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

            final BulkImportResult status = doImport(statusListener, fileToImport, csvImportDescriptorName, csvImportDescriptor);
            if (status != BulkImportResult.OK) {
                return status;
            }
        }
        return BulkImportResult.OK;
    }

    BulkImportResult doImport(final JobStatusListener statusListener,
                              final File fileToImport,
                              final String csvImportDescriptorName,
                              final CsvImportDescriptor csvImportDescriptor) throws Exception {

        return self().doSingleFileImport(statusListener, fileToImport, csvImportDescriptorName, csvImportDescriptor);

    }

    /**
     * {@inheritDoc}
     */
    public BulkImportResult doSingleFileImport(final JobStatusListener statusListener,
                                               final File fileToImport,
                                               final String csvImportDescriptorName,
                                               final CsvImportDescriptor csvImportDescriptor) throws Exception {

        try {


            final Map<String, Pair<Object, Boolean>> entityCache = new HashMap<String, Pair<Object, Boolean>>();

            final ImportDescriptor.ImportMode mode = csvImportDescriptor.getMode();
            final String msgInfoImp = MessageFormat.format("import file : {0} in {1} mode", fileToImport.getAbsolutePath(), mode);
            statusListener.notifyMessage(msgInfoImp);

            CsvFileReader csvFileReader = new CsvFileReaderImpl();
            try {
                final String filename = fileToImport.getName();
                long lineNumber = 0;

                csvFileReader.open(
                        fileToImport.getAbsolutePath(),
                        csvImportDescriptor.getImportFileDescriptor().getColumnDelimiter(),
                        csvImportDescriptor.getImportFileDescriptor().getTextQualifier(),
                        csvImportDescriptor.getImportFileDescriptor().getFileEncoding(),
                        csvImportDescriptor.getImportFileDescriptor().isIgnoreFirstLine());

                String[] line;
                while ((line = csvFileReader.readLine()) != null) {
                    final CsvImportTuple tuple = new CsvImportTupleImpl(filename, lineNumber++, line);
                    if (mode == ImportDescriptor.ImportMode.DELETE) {
                        doImportDelete(statusListener, tuple, csvImportDescriptorName, csvImportDescriptor);
                    } else {
                        doImportMerge(statusListener, tuple, csvImportDescriptorName, csvImportDescriptor, null, entityCache);
                    }
                }
                final String msgInfoLines = MessageFormat.format("total data lines : {0}",
                        (csvImportDescriptor.getImportFileDescriptor().isIgnoreFirstLine() ? csvFileReader.getRowsRead() - 1 : csvFileReader.getRowsRead()));
                statusListener.notifyMessage(msgInfoLines);

                csvFileReader.close();
            } catch (FileNotFoundException e) {
                final String msgErr = MessageFormat.format(
                        "can not find the file : {0} {1}",
                        fileToImport.getAbsolutePath(),
                        e.getMessage());
                statusListener.notifyError(msgErr, e);
            } catch (UnsupportedEncodingException e) {
                final String msgErr = MessageFormat.format(
                        "wrong file encoding in xml descriptor : {0} {1}",
                        csvImportDescriptor.getImportFileDescriptor().getFileEncoding(),
                        e.getMessage());
                statusListener.notifyError(msgErr, e);

            } catch (IOException e) {
                final String msgErr = MessageFormat.format("cannot close the csv file : {0} {1}",
                        fileToImport.getAbsolutePath(),
                        e.getMessage());
                statusListener.notifyError(msgErr, e);
            }

        } catch (Exception e) {

            /**
             * Programmatically rollback for any error during import - ALL or NOTHING - single file.
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
            return BulkImportResult.ERROR;
        }

        return BulkImportResult.OK;

    }

    /*
     * Delete single line.
     */
    void doImportDelete(final JobStatusListener statusListener,
                        final CsvImportTuple tuple,
                        final String csvImportDescriptorName,
                        final CsvImportDescriptor descriptor) throws Exception {
        List<Object> objects = null;
        try {


            if (descriptor.getDeleteSql() != null) {

                // No need to validate sub imports
                validateAccessBeforeUpdate(null, null); // only allowed by system admins

                executeNativeQuery(descriptor, null, tuple, descriptor.getDeleteSql());


            } else {

                objects = getExistingEntities(descriptor, descriptor.getSelectSql(), null, tuple);

                if (CollectionUtils.isNotEmpty(objects)) {

                    for (final Object object : objects) {
                        /*
                            Note: for correct data federation processing we need ALL-OR-NOTHING update for all import.
                                  Once validation fails we fail the whole import with a rollback. Necessary to facilitate
                                  objects with complex relationships to shop (e.g. products, SKU)
                         */

                        // No need to validate sub imports
                        // Preliminary validation - not always applicable for transient object (e.g. products need category assignments)
                        validateAccessBeforeUpdate(object, descriptor.getEntityTypeClass());

                        genericDAO.delete(object);

                        genericDAO.flushClear();
                    }

                }

            }
            statusListener.notifyPing("Deleting tuple: " + tuple.getSourceId()); // make sure we do not time out

        } catch (AccessDeniedException ade) {

            String message = MessageFormat.format(
                    "Access denied during import row : {0} \ndescriptor {1} \nobject is {2}",
                    tuple,
                    csvImportDescriptorName,
                    objects
            );
            statusListener.notifyError(message, ade);
            genericDAO.clear();

            throw new Exception(message, ade);


        } catch (Exception e) {

            String additionalInfo = e.getMessage();
            String message = MessageFormat.format(
                    "during import row : {0} \ndescriptor {1} \nerror {2} \nadditional info {3} \nobject is {4}",
                    tuple,
                    csvImportDescriptorName,
                    e.getMessage(),
                    additionalInfo,
                    objects
            );
            statusListener.notifyError(message, e);
            genericDAO.clear();

            throw new Exception(message, e);
        }
    }


    /*
     * Import single line.
     * This method can be called recursive in case of sub imports.
     */
    void doImportMerge(final JobStatusListener statusListener,
                       final ImportTuple tuple,
                       final String csvImportDescriptorName,
                       final CsvImportDescriptor descriptor,
                       final Object masterObject,
                       final Map<String, Pair<Object, Boolean>> entityCache) throws Exception {
        Object object = null;
        try {


            if (descriptor.getInsertSql() != null) {
                // this is dirty hack , because of import speed
                if (masterObject == null) {
                    // No need to validate sub imports
                    validateAccessBeforeUpdate(null, null); // only allowed by system admins
                }
                executeNativeQuery(descriptor, masterObject, tuple, descriptor.getInsertSql());


            } else {

                final Pair<Object, Boolean> objectAndState = getEntity(tuple, null, masterObject, descriptor, entityCache);
                object = objectAndState != null ? objectAndState.getFirst() : null;
                final boolean insert = objectAndState != null ? objectAndState.getSecond() : false;

                fillEntityFields(tuple, object, insert, descriptor.getColumns(ImpExColumn.FIELD));
                final boolean skip = fillEntityForeignKeys(tuple, object, insert, descriptor.getColumns(ImpExColumn.FK_FIELD), masterObject, descriptor, entityCache);

                if (skip) {

                    statusListener.notifyWarning("Skipping tuple: " + tuple);

                } else {

                    /*
                        Note: for correct data federation processing we need ALL-OR-NOTHING update for all import.
                              Once validation fails we fail the whole import with a rollback. Necessary to facilitate
                              objects with complex relationships to shop (e.g. products, SKU)
                     */

                    if (masterObject == null) {
                        // No need to validate sub imports
                        // Preliminary validation - not always applicable for transient object (e.g. products need category assignments)
                        validateAccessBeforeUpdate(object, descriptor.getEntityTypeClass());
                    }
                    genericDAO.saveOrUpdate(object);
                    performSubImport(statusListener, tuple, csvImportDescriptorName, descriptor, object,
                            descriptor.getColumns(ImpExColumn.SLAVE_INLINE_FIELD), entityCache);
                    performSubImport(statusListener, tuple, csvImportDescriptorName, descriptor, object,
                            descriptor.getColumns(ImpExColumn.SLAVE_TUPLE_FIELD), entityCache);

                    if (masterObject == null) {
                        // No need to validate sub imports
                        // This validation is after sub imports to facilitate objects with complex relationships to shop (e.g. products)
                        validateAccessAfterUpdate(object, descriptor.getEntityTypeClass());
                    }

                    genericDAO.flushClear();
                }

            }
            statusListener.notifyPing("Importing tuple: " + tuple.getSourceId()); // make sure we do not time out

        } catch (AccessDeniedException ade) {

            String message = MessageFormat.format(
                    "Access denied during import row : {0} \ndescriptor {1} \nobject is {2} \nmaster object is {3}",
                    tuple,
                    csvImportDescriptorName,
                    object,
                    masterObject
            );
            statusListener.notifyError(message);
            genericDAO.clear();

            throw new Exception(message, ade);


        } catch (Exception e) {

            String additionalInfo = e.getMessage();
            String message = MessageFormat.format(
                    "during import row : {0} \ndescriptor {1} \nerror {2} \nadditional info {3} \nobject is {4} \nmaster object is {5}",
                    tuple,
                    csvImportDescriptorName,
                    e.getMessage(),
                    additionalInfo,
                    object,
                    masterObject
            );
            statusListener.notifyError(message, e);
            genericDAO.clear();

            throw new Exception(message, e);
        }
    }

    private void executeNativeQuery(final ImportDescriptor descriptor,
                                    final Object masterObject,
                                    final ImportTuple tuple,
                                    final String queryTemplate) {

        if (queryTemplate.contains(";\n")) {
            for (final String statement : queryTemplate.split(";\n")) {
                if (StringUtils.isNotBlank(statement)) {
                    final LookUpQuery query = descriptorInsertLookUpQueryParameterStrategy.getQuery(
                            descriptor, masterObject, tuple, valueStringAdapter, statement
                    );
                    genericDAO.executeNativeUpdate(query.getQueryString(), query.getParameters());
                }
            }
        } else {
            final LookUpQuery query = descriptorInsertLookUpQueryParameterStrategy.getQuery(
                    descriptor, masterObject, tuple, valueStringAdapter, queryTemplate
            );
            genericDAO.executeNativeUpdate(query.getQueryString(), query.getParameters());
        }
    }

    private void performSubImport(final JobStatusListener statusListener,
                                  final ImportTuple tuple,
                                  final String csvImportDescriptorName,
                                  final ImportDescriptor importDescriptor,
                                  final Object object,
                                  final Collection<ImportColumn> slaves,
                                  final Map<String, Pair<Object, Boolean>> entityCache) throws Exception {
        for (ImportColumn slaveTable : slaves) {
            final List<ImportTuple> subTuples = tuple.getSubTuples(importDescriptor, slaveTable, valueDataAdapter);
            CsvImportDescriptor innerCsvImportDescriptor = (CsvImportDescriptor) slaveTable.getDescriptor();
            for (ImportTuple subTuple : subTuples) {
                doImportMerge(statusListener,
                        subTuple,
                        csvImportDescriptorName,
                        innerCsvImportDescriptor,
                        object,
                        entityCache);
            }
        }
    }


    /**
     * Fill the given entity object with line information using import column descriptions.
     *
     * @param tuple         given csv line
     * @param object        entity object
     * @param insert        entity insert (true), update (false)
     * @param importColumns particular type column collection
     * @throws Exception in case if something wrong with reflection (IntrospectionException,
     *                   InvocationTargetException,
     *                   IllegalAccessException)
     */
    private void fillEntityFields(final ImportTuple tuple,
                                  final Object object,
                                  final boolean insert,
                                  final Collection<ImportColumn> importColumns) throws Exception {

        final Class clz = object.getClass();

        PropertyDescriptor propertyDescriptor = null;

        for (ImportColumn importColumn : importColumns) {
            try {
                if (StringUtils.isNotBlank(importColumn.getName())) { //can be just lookup query

                    if (importColumn.isInsertOnly() && !insert) {
                        continue; // skip update since this is insert only
                    }
                    if (importColumn.isUpdateOnly() && insert) {
                        continue; // skip insert since this is update only
                    }

                    Object writeObject = object;

                    if (importColumn.getName().indexOf('.') == -1) {
                        // direct property
                        propertyDescriptor = new PropertyDescriptor(importColumn.getName(), clz);
                    } else {
                        // object path
                        final String[] chain = importColumn.getName().split("\\.");
                        for (int i = 0; i < chain.length - 1; i++) {
                            propertyDescriptor = new PropertyDescriptor(chain[i], writeObject.getClass());
                            writeObject = propertyDescriptor.getReadMethod().invoke(writeObject);
                        }
                        propertyDescriptor = new PropertyDescriptor(chain[chain.length - 1], writeObject.getClass());
                    }


                    Object singleObjectValue = tuple.getColumnValue(importColumn, valueDataAdapter);
                    if (importColumn.getLanguage() != null) {
                        final I18NModel model = new StringI18NModel((String) propertyDescriptor.getReadMethod().invoke(object));
                        model.putValue(importColumn.getLanguage(), singleObjectValue != null ? String.valueOf(singleObjectValue) : null);
                        singleObjectValue = model.toString();
                    }
                    if (singleObjectValue != null && !singleObjectValue.getClass().equals(propertyDescriptor.getPropertyType())) {
                        // if we have mismatch try on the fly conversion - this happens if someone omits <data-type> for non String values
                        singleObjectValue =
                                extendedConversionService.convert(
                                        singleObjectValue,
                                        TypeDescriptor.valueOf(singleObjectValue.getClass()),
                                        TypeDescriptor.valueOf((propertyDescriptor.getPropertyType())
                                    ));
                    }

                    propertyDescriptor.getWriteMethod().invoke(writeObject, singleObjectValue);
                }
            } catch (Exception exp) {

                final String propName = propertyDescriptor != null ? propertyDescriptor.getName() : null;
                final String propType = propertyDescriptor != null ? propertyDescriptor.getPropertyType().getName() : null;

                throw new Exception(MessageFormat.format(
                        "Failed to process property name {0} type {1} object is {2}",
                        propName,
                        propType,
                        object
                ), exp);
            }
        }

    }

    /**
     * Fill the given entity object with line information using import column descriptions.
     *
     * @param tuple            given csv line
     * @param object           entity object
     * @param insert           entity insert (true), update (false)
     * @param importColumns    particular type column collection
     * @param masterObject     master object , that set from main import in case of sub import
     * @param importDescriptor import descriptor
     * @param entityCache      runtime cache
     *
     * @return true if this tuple should be skipped
     *
     * @throws Exception in case if something wrong with reflection (IntrospectionException,
     *                   InvocationTargetException,
     *                   IllegalAccessException)
     */
    private boolean fillEntityForeignKeys(final ImportTuple tuple,
                                          final Object object,
                                          final boolean insert,
                                          final Collection<ImportColumn> importColumns,
                                          final Object masterObject,
                                          final ImportDescriptor importDescriptor,
                                          final Map<String, Pair<Object, Boolean>> entityCache) throws Exception {

        ImportColumn currentColumn = null;
        final Class clz = object.getClass();
        Object singleObjectValue = null;
        PropertyDescriptor propertyDescriptor = null;

        try {
            for (ImportColumn importColumn : importColumns) {

                if (importColumn.isInsertOnly() && !insert) {
                    continue; // skip update since this is insert only
                }
                if (importColumn.isUpdateOnly() && insert) {
                    continue; // skip insert since this is update only
                }

                currentColumn = importColumn;

                if (importColumn.isUseMasterObject()) {
                    singleObjectValue = masterObject;
                } else {
                    final Pair<Object, Boolean> singleObjectValueAndState = getEntity(tuple, importColumn, masterObject, importDescriptor, entityCache);
                    singleObjectValue = singleObjectValueAndState != null ? singleObjectValueAndState.getFirst() : null;
                    if (singleObjectValueAndState != null && singleObjectValueAndState.getSecond() && currentColumn.isSkipUpdateForUnresolved()) {
                        return true; // This is new FK object and we should skip this tuple and not abort with exception
                    }
                }
                propertyDescriptor = new PropertyDescriptor(importColumn.getName(), clz);
                final Object oldValue = propertyDescriptor.getReadMethod().invoke(object);
                if (oldValue instanceof Identifiable || singleObjectValue instanceof Identifiable) {
                    final Object oldValuePK = oldValue != null ? genericDAO.getEntityIdentifier(oldValue) : null;
                    final Object newValuePK = singleObjectValue != null ? genericDAO.getEntityIdentifier(singleObjectValue) : null;

                    if (oldValuePK == null || !oldValuePK.equals(newValuePK)) {
                        // Update the object only if the value has changed
                        propertyDescriptor.getWriteMethod().invoke(object, singleObjectValue);
                    }
                } else {
                    // This is not identifiable, possibly primitive (PK) so write always
                    propertyDescriptor.getWriteMethod().invoke(object, singleObjectValue);
                }

            }

            return false; // Do not skip

        } catch (Exception exp) {

            final String propName = propertyDescriptor != null ? propertyDescriptor.getName() : null;
            final String propType = propertyDescriptor != null ? propertyDescriptor.getPropertyType().getName() : null;

            throw new Exception(MessageFormat.format(
                    "Failed to process property name {0} type {1} object is {2} caused by column {0} with value {1}",
                    propName,
                    propType,
                    object,
                    currentColumn,
                    singleObjectValue
            ), exp);
        }

    }

    /**
     * Get the entity object with following strategy: first attempt - locate
     * existing entity, if it not found create new instance.
     *
     * @param tuple            csv row to get the parameter value for lookup query.
     * @param column           import column, the describe lookup query to locale the entity
     * @param masterObject     in case of subimport will be not null, but will be used with flag only
     * @param importDescriptor import descriptor
     * @param entityCache      runtime cache
     *
     * @return new or existing entity first => object, second => isNew flag
     *
     * @throws ClassNotFoundException in case if entity interface is wrong.
     */
    private Pair<Object, Boolean> getEntity(final ImportTuple tuple,
                                            final ImportColumn column,
                                            final Object masterObject,
                                            final ImportDescriptor importDescriptor,
                                            final Map<String, Pair<Object, Boolean>> entityCache) throws ClassNotFoundException {

        if (column == null) {
            // no caching for prime select
            final Object prime = getExistingEntity(importDescriptor, importDescriptor.getSelectSql(), masterObject, tuple);
            if (prime == null) {
                return new Pair<Object, Boolean>(genericDAO.getEntityFactory().getByKey(importDescriptor.getEntityType()), Boolean.TRUE);
            }
            return new Pair<Object, Boolean>(prime, Boolean.FALSE);
        }

        final String key = cacheKey.keyFor(importDescriptor, column, masterObject, tuple, valueStringAdapter);

        Pair<Object, Boolean> object = null;

        if (key != null) {
            object = entityCache.get(key);
        }

        if (object == null) {

            if (column.getLookupQuery() == null || column.getLookupQuery().length() == 0) {
                throw new IllegalArgumentException("Missing look up query for field: " + column.getName()
                        + " at index: " + column.getColumnIndex() + " in tuple: " + tuple);
            }
            final Object existing = getExistingEntity(importDescriptor, column.getLookupQuery(), masterObject, tuple);
            if (existing == null) {
                if (column.getEntityType() != null) {
                    object = new Pair<Object, Boolean>(genericDAO.getEntityFactory().getByIface(
                            Class.forName(column.getEntityType())),
                            Boolean.TRUE
                    );
                } else {
                    return null; // no cache for nulls
                }
            } else {
                object = new Pair<Object, Boolean>(existing, Boolean.FALSE);
            }
            if (object != null) {
                entityCache.put(key, object);
            }
        }
        return object;
    }


    /**
     * Try to get existing entity for update. In case of sub import master object will be used in parameters if
     * {@link ImportColumn#isUseMasterObject()} set to true.
     *
     * @param importDescriptor descriptor
     * @param queryTemplate    template to use with tuple columns as parameter values
     * @param masterObject in case of subimport will be not null, but will be used with flag only
     * @param tuple        csv row to get the parameter value for lookup query.
     *
     * @return existing entity or null if not found
     */
    private Object getExistingEntity(final ImportDescriptor importDescriptor,
                                     final String queryTemplate,
                                     final Object masterObject,
                                     final ImportTuple tuple) {

        final LookUpQuery query = columnLookUpQueryParameterStrategy.getQuery(importDescriptor, masterObject, tuple, valueDataAdapter, queryTemplate);
        final Object object = genericDAO.findSingleByQuery(query.getQueryString(), query.getParameters());
        return object;

    }

    /**
     * Try to get existing entity for update. In case of sub import master object will be used in parameters if
     * {@link ImportColumn#isUseMasterObject()} set to true.
     *
     * @param importDescriptor descriptor
     * @param queryTemplate    template to use with tuple columns as parameter values
     * @param masterObject in case of subimport will be not null, but will be used with flag only
     * @param tuple        csv row to get the parameter value for lookup query.
     *
     * @return existing entity or null if not found
     */
    private List<Object> getExistingEntities(final ImportDescriptor importDescriptor,
                                             final String queryTemplate,
                                             final Object masterObject,
                                             final ImportTuple tuple) {

        final LookUpQuery query = columnLookUpQueryParameterStrategy.getQuery(importDescriptor, masterObject, tuple, valueDataAdapter, queryTemplate);
        final List<Object> object = genericDAO.findByQuery(query.getQueryString(), query.getParameters());
        return object;

    }

    /**
     * IoC. Set the {@link org.yes.cart.dao.GenericDAO} instance.
     *
     * @param genericDAO {@link GenericDAO} to use.
     */
    public void setGenericDAO(GenericDAO<Object, Long> genericDAO) {
        this.genericDAO = genericDAO;
    }

    /**
     * IoC. Set {@link GenericConversionService}.
     *
     * @param extendedConversionService {@link GenericConversionService}  to use.
     */
    public void setExtendedConversionService(final GenericConversionService extendedConversionService) {
        this.extendedConversionService = extendedConversionService;
    }

    /**
     * IoC.
     *
     * @param valueDataAdapter {@link ValueAdapter}  to use.
     */
    public void setValueDataAdapter(final ValueAdapter valueDataAdapter) {
        this.valueDataAdapter = valueDataAdapter;
    }

    /**
     * IoC.
     *
     * @param valueStringAdapter {@link ValueAdapter}  to use.
     */
    public void setValueStringAdapter(final ValueAdapter valueStringAdapter) {
        this.valueStringAdapter = valueStringAdapter;
    }

    /**
     * IoC.
     *
     * @param cacheKey {@link EntityCacheKeyStrategy}  to use.
     */
    public void setCacheKey(final EntityCacheKeyStrategy cacheKey) {
        this.cacheKey = cacheKey;
    }

    /**
     * IoC.
     *
     * @param descriptorInsertLookUpQueryParameterStrategy {@link LookUpQueryParameterStrategy}  to use.
     */
    public void setDescriptorInsertLookUpQueryParameterStrategy(final LookUpQueryParameterStrategy descriptorInsertLookUpQueryParameterStrategy) {
        this.descriptorInsertLookUpQueryParameterStrategy = descriptorInsertLookUpQueryParameterStrategy;
    }

    /**
     * IoC.
     *
     * @param columnLookUpQueryParameterStrategy {@link LookUpQueryParameterStrategy}  to use.
     */
    public void setColumnLookUpQueryParameterStrategy(final LookUpQueryParameterStrategy columnLookUpQueryParameterStrategy) {
        this.columnLookUpQueryParameterStrategy = columnLookUpQueryParameterStrategy;
    }


    private CsvImportServiceSingleFile self;

    private CsvImportServiceSingleFile self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public CsvImportServiceSingleFile getSelf() {
        return null;
    }


}